package com.dhr.jd.solr.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.jd.mapper.TbItemMapper;
import com.jd.pojo.TbItem;
import com.jd.pojo.TbItemExample;
import com.jd.pojo.TbItemExample.Criteria;

/**
 * @author ali 导入索引库
 */
@Component
public class SolrImportAll {

	// 注入daomapper
	@Autowired
	private TbItemMapper itemMapper;

	public void importAllItem() {
		// 查询所有已经审核的商品
		TbItemExample example = new TbItemExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andStatusEqualTo("1");
		List<TbItem> list = itemMapper.selectByExample(example);
		for (TbItem tbItem : list) {
			System.out.println(tbItem.getTitle());
		}
	}

	public static void main(String[] args) {
		// 加载spring
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
		SolrImportAll importAll = (SolrImportAll) context.getBean("solrImportAll");
		importAll.importAllItem();
	}
}
