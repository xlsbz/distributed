package com.dhr.shop.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dhr.shop.pojo.SearchItem;
import com.dhr.shop.search.mapper.SearchItemMapper;
import com.dhr.shop.utils.SearchResult;
import com.dhr.shop.utils.TaoShopResult;

/**
 * 对索引库的数据查询
 * 
 * @author Mr DU
 *
 */
@Repository
public class SearchItemDao {

	// 注入solrServer
	@Autowired
	private SolrServer solrServer;

	// 注入集群版solr
	// @Autowired
	// private CloudSolrServer cloudSolrServer;

	@Autowired
	private SearchItemMapper mapper;

	/**
	 * 搜索索引库
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public SearchResult searchItemList(SolrQuery query) throws Exception {
		// 1.根据参数查询索引库
		QueryResponse response = solrServer.query(query);
		// 2.取出查询结果集
		SolrDocumentList results = response.getResults();
		// 3.创建商品列
		List<SearchItem> searchItems = new ArrayList<>();
		// 返回类型
		SearchResult result = new SearchResult();
		// 获得总记录数
		result.setTotalRecords((int) results.getNumFound());
		// 4.遍历商品结果集
		// 取高亮显示
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		// 创建搜索出来的商品对象
		SearchItem item = null;
		for (SolrDocument document : results) {
			// 创建商品对象
			item = new SearchItem();
			// 将索引库的数据封装到商品对象
			item.setId(Long.parseLong(document.get("id").toString()));
			item.setCategory_name(document.get("item_category_name").toString());
			item.setImage(document.get("item_image").toString());
			item.setPrice((Long) document.get("item_price"));
			item.setSell_point(document.get("item_sell_point").toString());

			List<String> list = highlighting.get(document.get("id")).get("item_title");
			String gaoliangstr = "";
			if (list != null && list.size() > 0) {
				gaoliangstr = list.get(0);
			} else {
				gaoliangstr = document.get("item_title").toString();
			}
			item.setTitle(gaoliangstr);
			// 5.将商品添加到商品列
			searchItems.add(item);
		}
		// 7.设置参数并返回

		result.setItemList(searchItems);
		return result;
	}

	/**
	 * 更新索引库
	 * 
	 * @param itemId
	 * @return
	 */
	public TaoShopResult updateItemById(Long itemId) {
		System.out.println(itemId);
		// 查询商品
		SearchItem item = mapper.getSearchItemById(itemId);
		// 如果有该商品就添加到索引库
		if (item != null) {
			// 注入solr服务
			// 删除修改之前的商品
			try {
				solrServer.deleteById(itemId + "");
				solrServer.commit();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// 创建document域对象
			SolrInputDocument document = new SolrInputDocument();
			// 设置值
			document.addField("id", item.getId());
			document.addField("item_title", item.getTitle());
			document.addField("item_sell_point", item.getSell_point());
			document.addField("item_price", item.getPrice());
			document.addField("item_image", item.getImage());
			document.addField("item_category_name", item.getCategory_name());
			document.addField("item_desc", item.getItem_desc());
			// 添加到索引库
			try {
				solrServer.add(document);
				solrServer.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return TaoShopResult.ok();
	}
}
