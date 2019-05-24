package com.dhr.jd.sellergoods.service;

import java.util.List;

import com.jd.pojo.TbGoods;
import com.pojo.group.Goods;
import com.util.PageResult;

/**
 * 服务层接口
 * 
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	public List<TbGoods> findAll();

	/**
	 * 返回分页列表
	 * 
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);

	/**
	 * 增加
	 */
	public void add(Goods goods);

	/**
	 * 修改
	 */
	public void update(Goods goods);

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);

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
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	/**
	 * 审核
	 * 
	 * @param id
	 */
	public void updateStatus(List<Long> id, String status);

	/**
	 * 逻辑删除
	 * 
	 * @param id
	 */
	public void deleteGoods(List<Long> id);

	/**
	 * 上下架
	 * 
	 * @param id
	 * @param status
	 */
	public void updateMarketable(List<Long> id, String status);

	/**
	 * 根据ID和状态查询所有商品
	 * 
	 * @param ids
	 * @param status
	 * @return
	 */
	List findAllIdAndStausItem(List<Long> ids, String status);
}
