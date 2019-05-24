/**
 * 
 */
package com.dhr.shop.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dhr.shop.content.service.ContentCategoryService;
import com.dhr.shop.utils.EasyUITreeNode;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @ClassName: ContentCategoryController
 * @Description: TODO(商品内容)
 * @author Mr DU
 * @date 2019年4月1日
 * 
 */
@Controller
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentService;

	/**
	 * 查询所有内容分类 @Title: getContentCategoryList @param @param
	 * parentId @param @return @return List<EasyUITreeNode> @throws
	 */
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCategoryList(@RequestParam(value = "id", defaultValue = "0") long parentId) {
		// 1.注入服务
		// 2.调用服务
		List<EasyUITreeNode> result = contentService.getContentCategory(parentId);
		return result;
	}

	/**
	 * 新增分类 @Title: insertCategory @param @param parentId @param @param
	 * name @param @return @return TaoShopResult @throws
	 */
	@RequestMapping("/content/category/create")
	@ResponseBody
	public TaoShopResult insertCategory(long parentId, String name) {
		return contentService.insertCategory(parentId, name);
	}

	/**
	 * 修改分类 @Title: updateCategory @param @param id @param @param
	 * name @param @return @return TaoShopResult @throws
	 */
	@RequestMapping("/content/category/update")
	@ResponseBody
	public TaoShopResult updateCategory(long id, String name) {
		return contentService.updateCategory(id, name);
	}

	@RequestMapping("/content/category/delete/")
	@ResponseBody
	public TaoShopResult deleteCategory(long id) {
		return contentService.deleteCategory(id);
	}
}
