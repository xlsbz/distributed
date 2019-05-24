package com.dhr.jd.protal.web;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dhr.jd.content.service.ContentService;
import com.jd.pojo.TbContent;

/**
 * @author ali
 */
@RestController
@RequestMapping("/content")
public class ContentController {

	@Reference
	private ContentService contentService;

	/**
	 * 查询某个分类下的所有广告
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findCategoryContent")
	public List<TbContent> findCategoryContent(Long id) {
		return contentService.findCategoryById(id);
	}
}
