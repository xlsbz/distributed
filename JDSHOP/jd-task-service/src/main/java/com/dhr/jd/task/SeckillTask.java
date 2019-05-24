package com.dhr.jd.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jd.mapper.TbSeckillGoodsMapper;
import com.jd.pojo.TbSeckillGoods;
import com.jd.pojo.TbSeckillGoodsExample;
import com.jd.pojo.TbSeckillGoodsExample.Criteria;

/**
 * @author ali 任务调度
 */
@Component
public class SeckillTask {

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

	/**
	 * 定时增量更新
	 */
	@Scheduled(cron = "0/20 * * * * ?")
	public void refreshSeckillGoods() {
		System.out.println("执行了秒杀商品的任务调度" + new Date());
		// 查询所有参与秒杀的商品
		List ids = new ArrayList(redisTemplate.boundHashOps("JDSECKILL").keys());
		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");// 状态为1
		criteria.andStockCountGreaterThan(0);// 库存大于0
		criteria.andStartTimeLessThanOrEqualTo(new Date());// 开始时间小于当前时间
		criteria.andEndTimeGreaterThanOrEqualTo(new Date());// 结束时间大于当前时间
		if (ids.size() > 0) {
			// 排除已经在秒杀队列的
			criteria.andIdNotIn(ids);
		}
		List<TbSeckillGoods> seckillList = seckillGoodsMapper.selectByExample(example);
		System.out.println(ids);
		for (TbSeckillGoods sg : seckillList) {
			// 将数据存入缓存
			redisTemplate.boundHashOps("JDSECKILL").put(sg.getId(), sg);
		}
		System.out.println("将" + seckillList.size() + "个商品放入秒杀缓存");

	}

	/**
	 * 定时清理过期商品
	 */
	@Scheduled(cron = "0/5 * * * * ?")
	public void removeSeckillGoodss() {
		System.out.println("执行过期商品移除");
		// 1.得到缓存所有数据
		List<TbSeckillGoods> seckillList = redisTemplate.boundHashOps("JDSECKILL").values();
		// 2.遍历所有缓存数据
		for (TbSeckillGoods tbSeckillGoods : seckillList) {
			// 当前日期大于结束日期表示过期
			if (new Date().getTime() > tbSeckillGoods.getEndTime().getTime()) {
				// 同步数据库
				seckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
				// 移除该商品
				redisTemplate.boundHashOps("JDSECKILL").delete(tbSeckillGoods.getId());
				System.out.println("移除了过期商品" + tbSeckillGoods.getTitle());
			}
		}
	}
}
