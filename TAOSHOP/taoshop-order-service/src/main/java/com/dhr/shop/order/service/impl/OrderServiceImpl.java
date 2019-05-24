package com.dhr.shop.order.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhr.shop.dao.TbOrderItemMapper;
import com.dhr.shop.dao.TbOrderMapper;
import com.dhr.shop.dao.TbOrderShippingMapper;
import com.dhr.shop.order.pojo.OrderInfo;
import com.dhr.shop.order.service.OrderService;
import com.dhr.shop.pojo.TbOrderItem;
import com.dhr.shop.pojo.TbOrderShipping;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU 处理订单
 */
@Service
public class OrderServiceImpl implements OrderService {

	// 注入订单mapper
	@Autowired
	private TbOrderMapper orderMapper;
	// 注入配送信息mapper
	@Autowired
	private TbOrderShippingMapper shipMapper;
	// 注入订单明细mappper
	@Autowired
	private TbOrderItemMapper orderItemMapper;

	/**
	 * 创建订单
	 * 
	 * @param orderInfo
	 * @return
	 */
	@Override
	public TaoShopResult creatOrder(OrderInfo orderInfo) {
		// 接收表单数据
		// 2.补全属性
		// 设置订单ID
		String orderId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		orderInfo.setOrderId(orderId);
		System.out.println(orderId);
		orderInfo.setPostFee("0");
		// 设置订单状态
		// 1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		// 设置订单shijian
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(orderInfo.getCreateTime());
		// 3.向订单表插入数据
		orderMapper.insert(orderInfo);

		// 4.向订单明细插入数据
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			// 生成明细表ID
			String itemId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
			tbOrderItem.setId(itemId);
			tbOrderItem.setItemId(tbOrderItem.getId());
			tbOrderItem.setOrderId(orderId);
			// 插数据
			orderItemMapper.insert(tbOrderItem);
		}
		// 5.向配送表插入数据
		TbOrderShipping tbOrderShipping = orderInfo.getOrderShipping();
		tbOrderShipping.setCreated(new Date());
		tbOrderShipping.setUpdated(new Date());
		tbOrderShipping.setOrderId(orderId);
		shipMapper.insert(tbOrderShipping);
		// 6.返回
		return TaoShopResult.ok(orderId);
	}

}
