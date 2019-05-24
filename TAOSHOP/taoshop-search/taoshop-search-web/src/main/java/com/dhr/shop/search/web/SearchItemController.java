/**
 * 
 */
package com.dhr.shop.search.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dhr.shop.search.service.SearchItemService;
import com.dhr.shop.utils.SearchResult;

/**
 * @author Mr DU 搜索商品
 *
 */
@Controller
public class SearchItemController {

	// 注入服务
	@Autowired
	private SearchItemService searchItemService;

	// 注入每页显示记录数
	@Value("${ItemRows}")
	private Integer ItemRows;

	/**
	 * 搜索商品
	 * 
	 * @return
	 */
	@RequestMapping("/search")
	public String searchItem(@RequestParam(value = "q") String queryString,
			@RequestParam(value = "page", defaultValue = "1") Integer page, Model model) throws Exception {
		queryString = new String(queryString.getBytes("iso-8859-1"), "utf-8");
		// int i = 1 / 0;
		// 1.接收查询参数
		// 2.调用服务查询商品
		SearchResult searchResult = null;
		try {
			searchResult = searchItemService.searchItem(queryString, page, ItemRows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 3.把数据放到域对象
		model.addAttribute("page", page);
		model.addAttribute("itemList", searchResult.getItemList());
		model.addAttribute("totalPages", searchResult.getTotalPages());
		model.addAttribute("query", queryString);
		return "search";
	}
}
