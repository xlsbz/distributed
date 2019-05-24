package com.dhr.jd.seckill.service;

import java.util.List;

import com.jd.pojo.TbSeckillGoods;
import com.util.PageResult;

import entity.Result;

/**
 * 服务层接口
 * 
 * @author Administrator
 *
 */
public interface SeckillGoodsService {

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	public List<TbSeckillGoods> findAll();

	/**
	 * 返回分页列表
	 * 
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);

	/**
	 * 增加
	 */
	public void add(TbSeckillGoods seckillGoods);

	/**
	 * 修改
	 */
	public void update(TbSeckillGoods seckillGoods);

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	public TbSeckillGoods findOne(Long id);

	/**
	 * 批量删除
	 * 
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * 
	 * @param pageNum  当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize);

	/**
	 * 查询出正在参与秒杀的商品
	 * 
	 * @return
	 */
	List<TbSeckillGoods> findList();

	/**
	 * 从redis查询出秒杀商品详情
	 * 
	 * @param id
	 * @return
	 */
	TbSeckillGoods findOneFromRedis(Long id);

	/**
	 * 抢购
	 * 
	 * @param id
	 * @param userId
	 */
	void submitOrder(Long id, String userId);

	/**
	 * 审核秒杀商品
	 * 
	 * @param ids
	 * @param status
	 * @return
	 */
	public Result updateStatusGoods(Long[] ids, String status);
}
