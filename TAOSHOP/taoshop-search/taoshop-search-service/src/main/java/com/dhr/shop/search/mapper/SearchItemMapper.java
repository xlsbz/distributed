package com.dhr.shop.search.mapper;

import java.util.List;

import com.dhr.shop.pojo.SearchItem;

/**
 * 数据接口
 * 
 * @author Mr DU
 *
 */
public interface SearchItemMapper {
	// 查询所有商品
	public List<SearchItem> getAllItemList();

	// 根据ID查询商品
	SearchItem getSearchItemById(Long itemId);
}
