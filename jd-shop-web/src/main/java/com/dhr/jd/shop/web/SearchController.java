package com.dhr.jd.shop.web;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dhr.jd.search.service.SearchService;

/**
 * @author ali 搜索
 */
@RestController
public class SearchController {

	@Reference
	private SearchService searchService;

	/**
	 * 搜索
	 * 
	 * @param searchMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/search")
	public Map<String, Object> search(@RequestBody Map searchMap) {

		return searchService.search(searchMap);
	}

}
