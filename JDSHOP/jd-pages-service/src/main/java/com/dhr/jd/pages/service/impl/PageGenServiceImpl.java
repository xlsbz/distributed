package com.dhr.jd.pages.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.dhr.jd.pages.service.PageGenService;
import com.jd.mapper.TbGoodsDescMapper;
import com.jd.mapper.TbGoodsMapper;
import com.jd.mapper.TbItemCatMapper;
import com.jd.mapper.TbItemMapper;
import com.jd.pojo.TbGoods;
import com.jd.pojo.TbGoodsDesc;
import com.jd.pojo.TbItem;
import com.jd.pojo.TbItemExample;
import com.jd.pojo.TbItemExample.Criteria;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;

@Service
public class PageGenServiceImpl implements PageGenService {

	// 注入mapper
	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbItemMapper itemMapper;

	// 注入模板
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;

	/**
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @throws MalformedTemplateNameException
	 * @throws Exception
	 */
	@Override
	public boolean genHtmlPages(Long id) {
		try {
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			// 得到模板
			Template template = configuration.getTemplate("item.ftl");
			Map map = new HashMap();
			// 查询商品信息
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
			map.put("goods", goods);
			map.put("goodsDesc", goodsDesc);
			// 查询商品的所属分类
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
			// 输出文件
			Writer writer = new FileWriter("D:\\freemarker\\jd\\" + id + ".html");
			template.process(map, writer);
			// 关闭资源
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
