package com.dhr.shop.search.service;

import com.dhr.shop.utils.SearchResult;
import com.dhr.shop.utils.TaoShopResult;

/**
 * 商品服务接口
 * 
 * @author Mr DU
 *
 */
public interface SearchItemService {

	/**
	 * 导入所有商品到索引库
	 * 
	 * @return
	 * @throws Exception
	 */
	TaoShopResult importAllItem() throws Exception;

	/**
	 * 搜索商品
	 * 
	 * @param query
	 * @param pageNumber
	 * @param rows
	 * @return
	 * @throws Exception
	 */
	SearchResult searchItem(String query, Integer pageNumber, Integer rows) throws Exception;

	/**
	 * 更新商品索引库
	 * 
	 * @param itemId
	 * @return
	 */
	TaoShopResult updateItemById(Long itemId);
}
