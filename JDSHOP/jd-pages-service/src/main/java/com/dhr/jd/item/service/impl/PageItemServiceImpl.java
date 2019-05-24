package com.dhr.jd.item.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.item.service.PageItemService;
import com.jd.mapper.TbGoodsDescMapper;
import com.jd.mapper.TbGoodsMapper;
import com.jd.mapper.TbItemCatMapper;
import com.jd.mapper.TbItemMapper;
import com.jd.pojo.TbGoods;
import com.jd.pojo.TbGoodsDesc;
import com.jd.pojo.TbItem;
import com.jd.pojo.TbItemExample;
import com.jd.pojo.TbItemExample.Criteria;

/**
 * @author ali 动态生成详情页
 */
@Service
public class PageItemServiceImpl implements PageItemService {

	// 注入mapper
	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbItemMapper itemMapper;

	/**
	 * @param id
	 * @return
	 */
	@Override
	public Map findPageById(Long id) {

		Map map = new HashMap();
		// 查询商品信息
		// 查询商品的所属分类
		try {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
			map.put("goods", goods);
			map.put("goodsDesc", goodsDesc);
			String category01 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String category02 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String category03 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			map.put("category01", category01);
			map.put("category02", category02);
			map.put("category03", category03);
			// 读取sku商品列表
			TbItemExample example = new TbItemExample();
			Criteria criteria = example.createCriteria();
			// 查找状态为1的降序排列
			criteria.andStatusEqualTo("1");
			criteria.andGoodsIdEqualTo(id);
			example.setOrderByClause("is_default desc");
			List<TbItem> itemList = itemMapper.selectByExample(example);
			map.put("itemList", itemList);
		} catch (Exception e) {
		}

		return map;
	}

}
