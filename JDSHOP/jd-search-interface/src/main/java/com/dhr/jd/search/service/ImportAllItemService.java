package com.dhr.jd.search.service;

import java.util.List;

import entity.Result;

/**
 * @author ali
 * 
 */
public interface ImportAllItemService {

	/**
	 * 导入所有商品到solr
	 * 
	 * @return
	 */
	Result importAllItem();

	/**
	 * 更新索引库
	 * 
	 * @param item
	 */
	void updateSolrItem(List itemList);

	/**
	 * 删除索引库
	 * 
	 * @param goodsId
	 */
	void deleteSolrItem(List<Long> goodsId);
}
