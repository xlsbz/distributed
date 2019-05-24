package com.dhr.jd.shop.web;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dhr.jd.sellergoods.service.GoodsService;
import com.jd.pojo.TbGoods;
import com.pojo.group.Goods;
import com.util.PageResult;

import entity.Result;

/**
 * controller
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll() {
		return goodsService.findAll();
	}

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows) {
		return goodsService.findPage(page, rows);
	}

	/**
	 * 增加
	 * 
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods) {
		try {
			// 设置商家信息
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(name);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 * 
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods) {
		// 判断是否是操作的自己的商品
		// 查询操作的商品
		Goods one = goodsService.findOne(goods.getGoods().getId());
		String sellerId = goods.getGoods().getSellerId();
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if ((!sellerId.equals(name)) || (!one.getGoods().getSellerId().equals(sellerId))) {
			return new Result(false, "非法执行！！！");
		}
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 审核商品
	 * 
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status) {
		// 获取审核商品的IDs
		try {
			List<Long> id = new ArrayList<Long>();
			for (Long l : ids) {
				id.add(l);
			}
			goodsService.updateStatus(id, status);
		} catch (Exception e) {
			e.printStackTrace();
			new Result(false, "失败!");
		}
		return new Result(true, "成功!");
	}

	/**
	 * 获取实体
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id) {
		return goodsService.findOne(id);
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 查询+分页
	 * 
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
		// 只查询该商家自己的商品
		// 获取当前用户
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);
		return goodsService.findPage(goods, page, rows);
	}

//	@Reference
//	private ImportAllItemService importAllItemService;

	// 注入activemq相关
	@Autowired
	private Destination solrInsertDestination;
	@Autowired
	private Destination solrDeleteDestination;

	@Autowired
	private JmsTemplate template;

	/**
	 * 上下架
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("/updateMarketable")
	public Result updateMarketable(Long[] ids, String status) {
		// 根据ID查询商品状态
		// 获取审核商品的IDs
		try {
			List<Long> id = new ArrayList<Long>();
			for (Long l : ids) {
				id.add(l);
			}
			// 修改上下架状态
			goodsService.updateMarketable(id, status);

			// 判断所选择商品是否审核通过
			Goods goods = goodsService.findOne(ids[0]);
			if (!goods.getGoods().getAuditStatus().equals("1")) {
				return new Result(true, "所选商品中存在未审核通过的商品!");
			}
			if (status.equals("0")) {
				// 删除索引库
				// importAllItemService.deleteSolrItem(id);
				template.send(solrDeleteDestination, new MessageCreator() {

					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}
			if (status.equals("1")) {
				// 根据goodsId查询所有sku商品
				List itemList = goodsService.findAllIdAndStausItem(id, status);
				if (itemList.size() > 0) {
					// 添加到索引库
					// importAllItemService.updateSolrItem(itemList);
					template.send(solrInsertDestination, new MessageCreator() {

						@Override
						public Message createMessage(Session session) throws JMSException {
							final String jsonString = JSON.toJSONString(itemList);
							return session.createTextMessage(jsonString);
						}
					});
				} else {
					System.out.println("没有可添加的商品");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			new Result(false, "操作失败!");
		}
		return new Result(true, "操作成功!");
	}

}
