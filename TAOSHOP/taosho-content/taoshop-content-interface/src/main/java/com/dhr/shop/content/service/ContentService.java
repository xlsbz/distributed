/**
 * 
 */
package com.dhr.shop.content.service;

import java.util.List;

import com.dhr.shop.pojo.TbContent;
import com.dhr.shop.utils.EasyUIDataGridResult;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @ClassName: ContentService
 * @Description: TODO(内容接口)
 * @author Mr DU
 * @date 2019年4月2日
 * 
 */
public interface ContentService {

	/**
	 * 查询内容
	 * 
	 * @param id
	 * @param page
	 * @param rows
	 * @return
	 */
	EasyUIDataGridResult getContentList(long id, Integer page, Integer rows);

	/**
	 * 增加分类内容
	 * 
	 * @param content
	 * @return
	 */
	TaoShopResult saveContent(TbContent content);

	/**
	 * 编辑内容
	 * 
	 * @param content
	 * @return
	 */
	TaoShopResult updateContent(TbContent content);

	/**
	 * 删除内容
	 * 
	 * @param idList
	 * @return
	 */
	TaoShopResult deleteContent(List<Long> idList);
}
