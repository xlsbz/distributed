package com.dhr.shop.service.impl;

import java.util.List;

import com.dhr.shop.pojo.TbContent;

public interface ContentIndexListService {
	/**
	 * 查询首页内容
	 * 
	 * @param id
	 * @return
	 */
	List<TbContent> getContentList(long id);
}
