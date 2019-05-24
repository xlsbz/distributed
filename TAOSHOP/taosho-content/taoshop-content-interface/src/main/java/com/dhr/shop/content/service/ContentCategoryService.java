/**
 * 
 */
package com.dhr.shop.content.service;

import java.util.List;

import com.dhr.shop.utils.EasyUITreeNode;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @ClassName: ContentCategoryService
 * @Description: TODO(内容业务)
 * @author Mr DU
 * @date 2019年4月1日
 * 
 */
public interface ContentCategoryService {
	/**
	 * 查询所有内容分类 @Title: getContentCategory @param @param
	 * parentId @param @return @return List<EasyUITreeNode> @throws
	 */
	List<EasyUITreeNode> getContentCategory(long parentId);

	/**
	 * 创建分类 @Title: insertCategory @param @param parentId @param @param
	 * name @param @return @return TaoShopResult @throws
	 */
	TaoShopResult insertCategory(long parentId, String name);

	/**
	 * 更新节点 @Title: updateCategory @param @param id @param @return @return
	 * TaoShopResult @throws
	 */
	TaoShopResult updateCategory(long id, String name);

	/**
	 * 删除节点 @Title: deleteCategory @param @param id @param @return @return
	 * TaoShopResult @throws
	 */
	TaoShopResult deleteCategory(long id);
}
