package com.dhr.jd.seckill.service;

import java.util.List;

import com.jd.pojo.TbSeckillOrder;
import com.util.PageResult;

/**
 * @author ali 秒杀订单处理
 */
public interface SeckillOrderService {
	/**
	 * 从缓存中查询日志
	 * 
	 * @param userId
	 * @return
	 */
	TbSeckillOrder findPayLogByRedis(String userId);

	/**
	 * 
	 * @param id 订单号
	 */
	void updateSeckillOrderStatus(Long id);

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	public List<TbSeckillOrder> findAll();

	/**
	 * 返回分页列表
	 * 
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);

	/**
	 * 增加
	 */
	public void add(TbSeckillOrder seckillOrder);

	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);

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
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);
}
