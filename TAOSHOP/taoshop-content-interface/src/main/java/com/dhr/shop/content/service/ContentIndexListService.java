package com.dhr.shop.content.service;
/**
 * 首页内容
 * @author Mr DU
 *
 */

import java.util.List;

import com.dhr.shop.pojo.TbContent;

public interface ContentIndexListService {
	/**
	 * 查询门户内容
	 * 
	 * @param cid
	 * @return
	 */
	List<TbContent> getContentIndexList(long cid);
}
