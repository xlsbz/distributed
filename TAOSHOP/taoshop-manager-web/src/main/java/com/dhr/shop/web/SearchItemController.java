package com.dhr.shop.web;
/**
 * 搜索商品
 * @author Mr DU
 *
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dhr.shop.search.service.SearchItemService;
import com.dhr.shop.utils.TaoShopResult;

@Controller
public class SearchItemController {

	@Autowired
	private SearchItemService searchItemService;

	/**
	 * 导入所有商品到索引库
	 * 
	 * @return
	 */
	@RequestMapping("/index/importall")
	@ResponseBody
	public TaoShopResult importAllItem() {
		// 1.注入服务
		// 2.导入
		TaoShopResult result = null;
		try {
			result = searchItemService.importAllItem();
		} catch (Exception e) {
			e.printStackTrace();
			return TaoShopResult.build(500, "商品导入失败！");
		}
		// 3.返回
		return TaoShopResult.ok();
	}
}
