package com.dhr.jd.order.service;

import java.util.List;

import com.jd.pojo.TbOrder;
import com.jd.pojo.TbPayLog;
import com.util.PageResult;

/**
 * 服务层接口
 * 
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	public List<TbOrder> findAll();

	/**
	 * 返回分页列表
	 * 
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);

	/**
	 * 增加
	 */
	public void add(TbOrder order);

	/**
	 * 修改
	 */
	public void update(TbOrder order);

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	public TbOrder findOne(Long id);

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
	public PageResult findPage(TbOrder order, int pageNum, int pageSize);

	/**
	 * 从缓存中查询日志
	 * 
	 * @param userId
	 * @return
	 */
	TbPayLog findPayLogByRedis(String userId);

	/**
	 * 
	 * @param out_trade_no   订单号
	 * @param transaction_id 支付成功流水号
	 */
	void updateOrderStatus(String out_trade_no, String transaction_id);
}
