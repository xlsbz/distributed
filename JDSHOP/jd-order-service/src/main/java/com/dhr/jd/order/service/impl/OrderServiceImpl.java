package com.dhr.jd.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.order.service.OrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.mapper.TbOrderItemMapper;
import com.jd.mapper.TbOrderMapper;
import com.jd.mapper.TbPayLogMapper;
import com.jd.pojo.TbOrder;
import com.jd.pojo.TbOrderExample;
import com.jd.pojo.TbOrderExample.Criteria;
import com.jd.pojo.TbOrderItem;
import com.jd.pojo.TbPayLog;
import com.pojo.group.Cart;
import com.util.IdWorker;
import com.util.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private RedisTemplate<String, ?> redisTemplate;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private TbPayLogMapper payMapper;

	/*
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		// 补全属性
		// 1.查询购物车的购物项数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("JDCART").get(order.getUserId());
		// 订单总金额
		double money_total = 0.0;
		// 订单ID列表
		List idList = new ArrayList();

		// 2.遍历购物车
		for (Cart cart : cartList) {
			Long orderId = idWorker.nextId();
			TbOrder tborder = new TbOrder();
			tborder.setOrderId(orderId);// 订单ID
			tborder.setUserId(order.getUserId());// 用户名
			tborder.setPaymentType(order.getPaymentType());// 支付类型
			tborder.setStatus("1");// 状态：未付款
			tborder.setCreateTime(new Date());// 订单创建日期
			tborder.setUpdateTime(new Date());// 订单更新日期
			tborder.setReceiverAreaName(order.getReceiverAreaName());// 地址
			tborder.setReceiverMobile(order.getReceiverMobile());// 手机号
			tborder.setReceiver(order.getReceiver());// 收货人
			tborder.setSourceType(order.getSourceType());// 订单来源
			tborder.setSellerId(cart.getSellerId());// 商家ID--->每个商家都必须创建自己独有的订单
			// 遍历购物车明细
			double money = 0.0;

			// 所有订单
			idList.add(orderId);
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);
				orderItem.setSellerId(cart.getSellerId());
				money += orderItem.getTotalFee().doubleValue();
				orderItemMapper.insertSelective(orderItem);
			}
			// 累加金额
			money_total += money;
			tborder.setPayment(new BigDecimal(money));
			orderMapper.insert(tborder);
		}
		// 判断如果是微信支付就向支付日志中写入数据
		if ("1".equals(order.getPaymentType())) {
			TbPayLog pay = new TbPayLog();
			pay.setCreateTime(new Date());
			pay.setOutTradeNo(idWorker.nextId() + "");// 支付单号
			pay.setOrderList(idList.toString().replace("[", "").replace("]", "").replace(" ", ""));
			pay.setPayType("1");// 支付类型
			pay.setTotalFee((long) money_total * 100);// 支付金额
			pay.setTradeState("0");// 未支付
			pay.setUserId(order.getUserId());
			payMapper.insertSelective(pay);
			System.out.println("向支付日志表存入数据");
			// 将支付日志放入缓存
			redisTemplate.boundHashOps("JDPAYLOG").put(order.getUserId(), pay);
		}
		// 清除购物车
		redisTemplate.boundHashOps("JDCART").delete(order.getUserId());
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order) {
		orderMapper.updateByPrimaryKey(order);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id) {
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			orderMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();

		criteria.andSellerIdEqualTo(order.getSellerId());

		if (order != null) {
			if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
				criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
			}
			if (order.getPostFee() != null && order.getPostFee().length() > 0) {
				criteria.andPostFeeLike("%" + order.getPostFee() + "%");
			}
			if (order.getStatus() != null && order.getStatus().length() > 0) {
				criteria.andStatusLike("%" + order.getStatus() + "%");
			}
			if (order.getShippingName() != null && order.getShippingName().length() > 0) {
				criteria.andShippingNameLike("%" + order.getShippingName() + "%");
			}
			if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
				criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
			}
			if (order.getUserId() != null && order.getUserId().length() > 0) {
				criteria.andUserIdLike("%" + order.getUserId() + "%");
			}
			if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
				criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
			}
			if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
				criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
			}
			if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
				criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
			}
			if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
				criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
			}
			if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
				criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
			}
			if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
				criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
			}
			if (order.getReceiver() != null && order.getReceiver().length() > 0) {
				criteria.andReceiverLike("%" + order.getReceiver() + "%");
			}
			if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
				criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
			}
			if (order.getSourceType() != null && order.getSourceType().length() > 0) {
				criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
			}
			if (order.getSellerId() != null && order.getSellerId().length() > 0) {
				criteria.andSellerIdLike("%" + order.getSellerId() + "%");
			}

		}

		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public TbPayLog findPayLogByRedis(String userId) {
		return (TbPayLog) redisTemplate.boundHashOps("JDPAYLOG").get(userId);
	}

	/**
	 * @param out_trade_no
	 * @param transaction_id
	 */
	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		// 1.查询修改日志
		TbPayLog payLog = payMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date());
		payLog.setTradeState("1");// 已支付
		payLog.setTransactionId(transaction_id);
		payMapper.updateByPrimaryKeySelective(payLog);

		// 2.得到所有订单修改订单状态
		String[] orderIds = payLog.getOrderList().split(",");
		for (String string : orderIds) {
			TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(string));
			if (order != null) {
				order.setStatus("2");
				orderMapper.updateByPrimaryKeySelective(order);
			}
		}

		// 3.清楚支付日志缓存
		redisTemplate.boundHashOps("JDPAYLOG").delete(payLog.getUserId());

	}

}
