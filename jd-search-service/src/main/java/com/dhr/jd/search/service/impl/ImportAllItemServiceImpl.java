package com.dhr.jd.search.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dhr.jd.search.service.ImportAllItemService;
import com.jd.mapper.TbItemMapper;
import com.jd.pojo.TbItem;
import com.jd.pojo.TbItemExample;
import com.jd.pojo.TbItemExample.Criteria;

import entity.Result;

/**
 * @author ali
 *
 */
@Service(timeout = 5000)
public class ImportAllItemServiceImpl implements ImportAllItemService {

	// 注入solr模板
	@Autowired
	private SolrTemplate solrTemplate;

	// 注入mapper
	@Autowired
	private TbItemMapper itemMapper;

	/**
	 * @return
	 */
	@Override
	public Result importAllItem() {
		// 1.查询所有商品
		try {
			TbItemExample example = new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo("1");
			List<TbItem> list = itemMapper.selectByExample(example);
			for (TbItem tbItem : list) {
				// 处理动态域
				String spec = tbItem.getSpec();
				Map map = JSON.parseObject(spec);
				// 设置到索引带动态域注解的字段
				tbItem.setSpecMap(map);
			}
			// 2.导入
			solrTemplate.saveBeans(list);
			solrTemplate.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true, "error");
		}
		return new Result(true, "ok");
	}

	/**
	 * @param itemList
	 */
	@Override
	public void updateSolrItem(List itemList) {
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();

	}

	/**
	 * @param goodsId
	 */
	@Override
	public void deleteSolrItem(List<Long> goodsId) {
		// 创建查询条件
		org.springframework.data.solr.core.query.Criteria criteria = new org.springframework.data.solr.core.query.Criteria(
				"item_goodsid");
		criteria.in(goodsId);
		Query query = new SimpleQuery(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}

}
