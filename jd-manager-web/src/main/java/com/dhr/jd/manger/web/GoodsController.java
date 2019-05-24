package com.dhr.jd.manger.web;

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

	// @Reference
	// private PageGenService pageGenService;

	// @Reference
	// private ImportAllItemService importAllItemService;

	// 注入activemq相关
	@Autowired
	private Destination solrDestination;
	@Autowired
	private Destination topicPageDestination;
	@Autowired
	private JmsTemplate template;

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
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
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
		return goodsService.findPage(goods, page, rows);
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
			if (status.equals("1")) {
				// 根据goo dsId查询所有sku商品添加到solr
				List itemList = goodsService.findAllIdAndStausItem(id, status);
				if (itemList.size() > 0) {
					// 添加到索引库
					// importAllItemService.updateSolrItem(itemList);
					// 通过active发送消息
					final String jsonString = JSON.toJSONString(itemList);
					template.send(solrDestination, new MessageCreator() {

						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});
				} else {
					System.out.println("没有可添加的商品");
				}

				// 将商品生成静态页
				for (Long goodsId : ids) {
					// pageGenService.genHtmlPages(goodsId);
					// 向页面生成服务发送消息
					template.send(topicPageDestination, new MessageCreator() {

						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(goodsId + "");
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			new Result(false, "失败!");
		}
		return new Result(true, "成功!");
	}

	/**
	 * 批量逻辑删除
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("/deleteGoods")
	public Result deleteGoods(Long[] ids) {
		// 获取审核商品的IDs
		try {
			List<Long> id = new ArrayList<Long>();
			for (Long l : ids) {
				id.add(l);
			}
			goodsService.deleteGoods(id);
		} catch (Exception e) {
			e.printStackTrace();
			new Result(false, "失败!");
		}
		return new Result(true, "成功!");
	}

}
