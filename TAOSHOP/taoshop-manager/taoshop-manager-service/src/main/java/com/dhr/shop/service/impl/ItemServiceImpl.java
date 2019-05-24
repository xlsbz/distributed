package com.dhr.shop.service.impl;

import java.util.Date;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.dhr.shop.dao.TbItemDescMapper;
import com.dhr.shop.dao.TbItemMapper;
import com.dhr.shop.item.jedis.impl.JedisClient;
import com.dhr.shop.pojo.TbItem;
import com.dhr.shop.pojo.TbItemDesc;
import com.dhr.shop.pojo.TbItemDescExample;
import com.dhr.shop.pojo.TbItemExample;
import com.dhr.shop.pojo.TbItemExample.Criteria;
import com.dhr.shop.service.ItemService;
import com.dhr.shop.utils.EasyUIDataGridResult;
import com.dhr.shop.utils.JsonUtils;
import com.dhr.shop.utils.TaoShopResult;
import com.dhr.shop.utils.UUIDUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ItemServiceImpl implements ItemService {

	// 注入mapper
	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemDescMapper itemDescMapper;

	// 注入redis
	@Autowired
	private JedisClient jedisPoolClient;

	// 注入active模板
	@Autowired
	private JmsTemplate template;
	@Autowired
	private Destination topicDestination;

	// 注入属性值
	@Value("${ITEM_INFO_KEY}")
	private String ITEM_INFO_KEY;
	@Value("${ITEN_INFO_EXPRIE}")
	private int ITEN_INFO_EXPRIE;

	/*
	 * 分页查询
	 * 
	 * @see com.dhr.shop.service.ItemService#delItem(java.lang.String)
	 */
	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		if (page == 0) {
			page = 1;
		}
		if (rows == 0) {
			rows = 30;
		}
		// 创建page
		PageHelper.startPage(page, rows);// 设置分页条件 开始页 每页记录数
		// 执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		// 取出分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		// 创建包装对象返回结果
		EasyUIDataGridResult dataGridResult = new EasyUIDataGridResult();
		dataGridResult.setTotal((int) pageInfo.getTotal());
		dataGridResult.setRows(list);
		return dataGridResult;
	}

	@Override
	public TaoShopResult saveItem(TbItem item, String desc) {
		// 1.生成商品ID
		Long itemId = UUIDUtils.getItemId();
		// 2.补全item属性
		item.setId(itemId);
		// 设置商品状态 1-正常 2-下架 3-删除
		item.setStatus((byte) 1);
		// 设置时间
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		// 3.向商品表插入数据
		itemMapper.insertSelective(item);
		// 4.创建商品详情对象
		TbItemDesc itemDesc = new TbItemDesc();
		// 5.补全商品详情对象
		itemDesc.setItemDesc(desc);
		itemDesc.setItemId(itemId);
		itemDesc.setUpdated(date);
		itemDesc.setCreated(date);
		// 6.向商品详情表插入数据
		itemDescMapper.insertSelective(itemDesc);
		// 向activemq发送商品增加消息--同步更新索引库
		// 1.注入服务
		// 2.发送消息
		template.send(topicDestination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				// 获取商品id发送
				return session.createTextMessage(itemId.toString());
			}
		});
		return TaoShopResult.ok();
	}

	/*
	 * 删除
	 * 
	 * @see com.dhr.shop.service.ItemService#delItem(java.lang.String)
	 */
	@Override
	public TaoShopResult delItem(long ids) {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(ids);
		// 删除该商品
		int i = itemMapper.deleteByExample(example);
		// 删除该商品描述
		TbItemDescExample descExample = new TbItemDescExample();
		com.dhr.shop.pojo.TbItemDescExample.Criteria criteria2 = descExample.createCriteria();
		criteria2.andItemIdEqualTo(ids);
		int j = itemDescMapper.deleteByExample(descExample);
		if (i > 0 && j > 0) {
			return TaoShopResult.ok();
		}
		return null;
	}

	/*
	 * 下架商品
	 * 
	 * @see com.dhr.shop.service.ItemService#inStock(java.util.List)
	 */
	@Override
	public TaoShopResult inStockItem(List<Long> lists) {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(lists);
		List<TbItem> list = itemMapper.selectByExample(example);
		for (TbItem tb : list) {
			tb.setStatus((byte) 2);
			itemMapper.updateByPrimaryKey(tb);
		}
		return TaoShopResult.ok();
	}

	/*
	 * 上架商品
	 * 
	 * @see com.dhr.shop.service.ItemService#reshelf(java.util.List)
	 */
	@Override
	public TaoShopResult reshelf(List<Long> lists) {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(lists);
		List<TbItem> list = itemMapper.selectByExample(example);
		for (TbItem tb : list) {
			tb.setStatus((byte) 1);
			itemMapper.updateByPrimaryKey(tb);
		}
		return TaoShopResult.ok();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dhr.shop.service.ItemService#getItemById(java.lang.Long)
	 */
	@Override
	public TbItemDesc getItemById(Long id) {
		// 根据ID查询商品详情
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
		return itemDesc;
	}

	/*
	 * 修改商品
	 * 
	 * @see com.dhr.shop.service.ItemService#updateItem(com.dhr.shop.pojo.TbItem,
	 * java.lang.String)
	 */
	@Override
	public TaoShopResult updateItem(TbItem item, TbItemDesc desc, Long id) {
		// 创建条件
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(id);
		// 补全item属性
		item.setId(id);
		// 设置商品状态 1-正常 2-下架 3-删除
		item.setStatus((byte) 1);
		// 设置时间
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		// 调用mapper
		itemMapper.updateByExample(item, example);
		// 修改商品描述
		TbItemDescExample descExample = new TbItemDescExample();
		com.dhr.shop.pojo.TbItemDescExample.Criteria criteria2 = descExample.createCriteria();
		criteria2.andItemIdEqualTo(id);
		// 补全属性
		desc.setItemId(id);
		desc.setCreated(new Date());
		desc.setUpdated(new Date());
		itemDescMapper.updateByExample(desc, descExample);
		// 通知发送消息修改索引库
		template.send(topicDestination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(id + "");
			}
		});
		return TaoShopResult.ok();
	}

	/**
	 * 查询商品信息
	 * 
	 * @param itemId
	 * @return
	 */
	@Override
	public TbItem getItemDetailById(long itemId) {
		TbItem tbItem = null;
		try {
			// 1.先从缓存中查询，如果有直接返回
			if (Long.toString(itemId) != null) {
				String jsonUtils = jedisPoolClient.get(ITEM_INFO_KEY + ":" + itemId + "BASE");
				// 重新设置过期时间
				if (StringUtils.isNotBlank(jsonUtils)) {
					jedisPoolClient.expire(ITEM_INFO_KEY + ":" + itemId + "BASE", ITEN_INFO_EXPRIE);
					return JsonUtils.jsonToPojo(jsonUtils, TbItem.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 2.没有，从数据库查
		tbItem = itemMapper.selectByPrimaryKey(itemId);
		// 3.设置到缓存
		try {
			if (tbItem != null) {
				jedisPoolClient.set(ITEM_INFO_KEY + ":" + itemId + "BASE", JsonUtils.objectToJson(tbItem));
				// 设置过期时间
				jedisPoolClient.expire(ITEM_INFO_KEY + ":" + itemId + "BASE", ITEN_INFO_EXPRIE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tbItem;
	}

	/**
	 * 查询商品详情信息
	 * 
	 * @param itemId
	 * @return
	 */
	@Override
	public TbItemDesc getItemDetailDesc(long itemId) {
		TbItemDesc itemDesc = null;
		try {
			// 1.先从缓存中查询，如果有直接返回
			if (Long.toString(itemId) != null) {
				String jsonUtil = jedisPoolClient.get(ITEM_INFO_KEY + ":" + itemId + "DESC");
				if (StringUtils.isNotBlank(jsonUtil)) {
					itemDesc = JsonUtils.jsonToPojo(jsonUtil, TbItemDesc.class);
					// 重新设置过期时间
					jedisPoolClient.expire(ITEM_INFO_KEY + ":" + itemId + "DESC", ITEN_INFO_EXPRIE);
					return itemDesc;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 2.没有，从数据库查
		itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		// 3.设置到缓存
		try {
			if (itemDesc != null) {
				jedisPoolClient.set(ITEM_INFO_KEY + ":" + itemId + "DESC", JsonUtils.objectToJson(itemDesc));
				jedisPoolClient.expire(ITEM_INFO_KEY + ":" + itemId + "DESC", ITEN_INFO_EXPRIE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return itemDesc;
	}

}
