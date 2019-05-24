package com.dhr.shop.utils;

import java.io.Serializable;
import java.util.List;

import com.dhr.shop.pojo.SearchItem;

public class SearchResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<SearchItem> itemList;// 查出来的数据
	private Integer totalRecords;// 总数居
	private Integer totalPages;// 总页数

	public List<SearchItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<SearchItem> itemList) {
		this.itemList = itemList;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

}
