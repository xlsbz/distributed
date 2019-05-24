package com.dhr.jd.seckill.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.seckill.service.SeckillGoodsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.mapper.TbSeckillGoodsMapper;
import com.jd.mapper.TbSeckillOrderMapper;
import com.jd.pojo.TbSeckillGoods;
import com.jd.pojo.TbSeckillGoodsExample;
import com.jd.pojo.TbSeckillGoodsExample.Criteria;
import com.jd.pojo.TbSeckillOrder;
import com.util.IdWorker;
import com.util.PageResult;

import entity.Result;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorder;

	/**
	 * 查询
	 */
	@Override
	public List<TbSeckillGoods> findAll() {

		return seckillGoodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillGoods seckillGoods) {
		// 补全属性

		seckillGoods.setCreateTime(new Date());
		seckillGoods.setGoodsId(idWorder.nextId());
		seckillGoods.setItemId(idWorder.nextId());
		seckillGoods.setStatus("0");
		seckillGoods.setStockCount(seckillGoods.getNum());
		seckillGoodsMapper.insert(seckillGoods);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillGoods seckillGoods) {
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOne(Long id) {
		return seckillGoodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();

		criteria.andSellerIdEqualTo(seckillGoods.getSellerId());

		if (seckillGoods != null) {
			if (seckillGoods.getTitle() != null && seckillGoods.getTitle().length() > 0) {
				criteria.andTitleLike("%" + seckillGoods.getTitle() + "%");
			}
			if (seckillGoods.getSmallPic() != null && seckillGoods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + seckillGoods.getSmallPic() + "%");
			}
			if (seckillGoods.getSellerId() != null && seckillGoods.getSellerId().length() > 0) {
				criteria.andSellerIdLike("%" + seckillGoods.getSellerId() + "%");
			}
			if (seckillGoods.getStatus() != null && seckillGoods.getStatus().length() > 0) {
				criteria.andStatusLike("%" + seckillGoods.getStatus() + "%");
			}
			if (seckillGoods.getIntroduction() != null && seckillGoods.getIntroduction().length() > 0) {
				criteria.andIntroductionLike("%" + seckillGoods.getIntroduction() + "%");
			}

		}

		Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(example);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * @return
	 */
	@Override
	public List<TbSeckillGoods> findList() {
		// 先从缓存取数据
		List<TbSeckillGoods> seckillList = redisTemplate.boundHashOps("JDSECKILL").values();
		// 有任务调度完成
//		if (seckillList == null || seckillList.size() == 0) {
//			TbSeckillGoodsExample example = new TbSeckillGoodsExample();
//			Criteria criteria = example.createCriteria();
//			criteria.andStatusEqualTo("1");// 状态为1
//			criteria.andStockCountGreaterThan(0);// 库存大于0
//			criteria.andStartTimeLessThanOrEqualTo(new Date());// 开始时间小于当前时间
//			criteria.andEndTimeGreaterThanOrEqualTo(new Date());// 结束时间大于当前时间
//			seckillList = seckillGoodsMapper.selectByExample(example);
//
//			for (TbSeckillGoods sg : seckillList) {
//				// 将数据存入缓存
//				redisTemplate.boundHashOps("JDSECKILL").put(sg.getId(), sg);
//			}
//
//			System.out.println("从数据库中读出秒杀数据");
//		}
		System.out.println("从缓存中读出秒杀数据");
		return seckillList;
	}

	/**
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOneFromRedis(Long id) {
		return (TbSeckillGoods) redisTemplate.boundHashOps("JDSECKILL").get(id);
	}

	/**
	 * @param id
	 * @param userId
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void submitOrder(Long id, String userId) {
		// 1.根据ID从缓存中查询秒杀商品
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("JDSECKILL").get(id);
		// 2.判断商品状态
		if (seckillGoods == null) {
			throw new RuntimeException("商品不存在!");
		}
		if (seckillGoods.getStockCount() <= 0) {
			throw new RuntimeException("商品已经被抢空了！");
		}
		// 3.修改商品缓存中的库存
		seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
		redisTemplate.boundHashOps("JDSECKILL").put(id, seckillGoods);
		// 4.如果商品抢完后数量为0，清空缓存同步数据库
		if (seckillGoods.getStockCount() == 0) {
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			redisTemplate.boundHashOps("JDSECKILL").delete(id);
		}
		// 5.生成抢购订单
		TbSeckillOrder seckillOrder = new TbSeckillOrder();
		seckillOrder.setCreateTime(new Date());
		Long l = idWorder.nextId();
		String seckid = l.toString().substring(0, 14);
		seckillOrder.setId(new Long(seckid));
		seckillOrder.setMoney(seckillGoods.getCostPrice());
		seckillOrder.setSeckillId(id);
		seckillOrder.setSellerId(seckillGoods.getSellerId());
		seckillOrder.setUserId(userId);
		seckillOrder.setStatus("0");
		seckillOrderMapper.insert(seckillOrder);
		// 6加入到缓存订单日志
		redisTemplate.boundHashOps("JDSECKILLORDER").put(userId, seckillOrder);
	}

	/**
	 * @param ids
	 * @param status
	 * @return
	 */
	@Override
	public Result updateStatusGoods(Long[] ids, String status) {
		// 1.查询所有商品
		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(Arrays.asList(ids));
		List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example);
		for (TbSeckillGoods sk : list) {
			sk.setStatus(status);
			// 2.修改回去
			seckillGoodsMapper.updateByPrimaryKey(sk);
		}
		return new Result(true, "成功");
	}

}
