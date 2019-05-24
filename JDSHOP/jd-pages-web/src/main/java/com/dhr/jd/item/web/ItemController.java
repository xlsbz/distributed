package com.dhr.jd.item.web;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dhr.jd.item.service.PageItemService;

/**
 * @author ali 处理商品详情
 */
@RestController
@RequestMapping("/pages")
public class ItemController {

	@Reference
	private PageItemService pageItemService;

	/**
	 * 根据商品ID查询商品
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("findByIdPage")
	public Map findPageById(Long id) {
		Map map = null;
		try {
			map = pageItemService.findPageById(id);
		} catch (Exception e) {
		}
		return map;
	}

}
