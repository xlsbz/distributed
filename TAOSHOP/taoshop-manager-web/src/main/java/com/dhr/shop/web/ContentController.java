/**
 * 
 */
package com.dhr.shop.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dhr.shop.content.service.ContentService;
import com.dhr.shop.pojo.TbContent;
import com.dhr.shop.utils.EasyUIDataGridResult;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @ClassName: ContentController
 * @Description: TODO(内容管理)
 * @author Mr DU
 * @date 2019年4月2日
 * 
 */
@Controller
public class ContentController {
	@Autowired
	private ContentService contentService;

	/**
	 * 分页查询内容数据
	 * 
	 * @param id
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult getAllContent(@RequestParam(value = "categoryId") long id,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "rows", defaultValue = "8") Integer rows) {
		// 1.注入服务
		// 2.调用服务
		EasyUIDataGridResult contentList = contentService.getContentList(id, page, rows);
		// 3.返回
		return contentList;
	}

	/**
	 * 新增分类
	 * 
	 * @param content
	 * @return
	 */
	@RequestMapping("/content/save")
	@ResponseBody
	public TaoShopResult saveContent(TbContent content) {
		// 1.注入服务
		// 2.调用服务
		TaoShopResult result = contentService.saveContent(content);
		// 3.返回
		return result;
	}

	/**
	 * 编辑商品
	 * 
	 * @param content
	 * @return
	 */
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public TaoShopResult updateContent(TbContent content) {
		// 1.注入服务
		// 2.调用服务
		return contentService.updateContent(content);
	}

	/**
	 * 删除商品
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("/content/delete")
	@ResponseBody
	public TaoShopResult deleteContent(String ids) {
		// 1.获取一个字符串，切割字符串
		String[] split = ids.split(",");
		// 2.把字符串放入list
		List<Long> idList = new ArrayList<>();
		// 3.注入调用服务
		for (String id : split) {
			idList.add(Long.valueOf(id));
		}
		// 4.返回
		return contentService.deleteContent(idList);
	}

}
