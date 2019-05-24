package com.dhr.shop.search.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhr.shop.pojo.SearchItem;
import com.dhr.shop.search.dao.SearchItemDao;
import com.dhr.shop.search.mapper.SearchItemMapper;
import com.dhr.shop.search.service.SearchItemService;
import com.dhr.shop.utils.SearchResult;
import com.dhr.shop.utils.TaoShopResult;

/**
 * 商品服务接口
 * 
 * @author Mr DU
 *
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {

	@Autowired
	private SearchItemMapper searchItemMapper;

	@Autowired
	private SolrServer solrServer;

	// 注入集群版solr
	// @Autowired
	// private CloudSolrServer cloudSolrServer;

	@Autowired
	private SearchItemDao searchItemDao;

	/**
	 * 导入数据到索引库
	 * 
	 * @throws @throws
	 *             Exception
	 */
	@Override
	public TaoShopResult importAllItem() throws Exception {
		// 1.注入mapper服务
		// 2.注入solrJ服务
		// 3.查询所有商品
		List<SearchItem> allItemList = searchItemMapper.getAllItemList();
		// 4.为每个商品创建域对象放到索引库
		for (SearchItem searchItem : allItemList) {
			SolrInputDocument document = new SolrInputDocument();
			// 把商品信息添加到文档域
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			document.addField("item_desc", searchItem.getItem_desc());

			solrServer.add(document);
			// cloudSolrServer.add(document);
			// 提交
			solrServer.commit();
			// cloudSolrServer.commit();
		}

		// 5.返回
		return TaoShopResult.ok();
	}

	/**
	 * @param query
	 *            查询条件
	 * @param pageNumber
	 *            页码
	 * @param rows
	 *            总页数
	 * @return
	 * @throws Exception
	 */
	@Override
	public SearchResult searchItem(String query, Integer pageNumber, Integer rows) throws Exception {
		// 1.创建一个solrQuery查询对象
		SolrQuery solrQuery = new SolrQuery();
		// 2.设置查询条件
		if (StringUtils.isNotBlank(query)) {
			solrQuery.setQuery(query);
		} else {
			solrQuery.setQuery("*:*");
		}
		// 3.设置分页条件
		if (pageNumber == null)
			pageNumber = 1;
		if (rows == null)
			rows = 60;
		solrQuery.setStart((pageNumber - 1) * rows);
		solrQuery.setRows(rows);
		// 4.设置指定默认的搜索域
		solrQuery.set("df", "item_keywords");
		// 5.设置高亮
		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<em color=\"red\">");
		solrQuery.setHighlightSimplePost("</em>");
		// 6.执行查询，调用方法
		SearchResult result = searchItemDao.searchItemList(solrQuery);
		// 7.根据总记录计算总页数
		Integer totalRecords = result.getTotalRecords();
		int totalPages = 0;
		if (totalRecords % rows != 0) {
			totalPages = totalRecords / rows + 1;
		} else {
			totalPages = totalRecords / rows;
		}
		result.setTotalPages(totalPages);
		// 8.返回result
		return result;
	}

	/**
	 * @param itemId
	 * @return
	 */
	@Override
	public TaoShopResult updateItemById(Long itemId) {
		return searchItemDao.updateItemById(itemId);
	}

}
