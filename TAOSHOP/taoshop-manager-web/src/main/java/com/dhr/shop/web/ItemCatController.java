package com.dhr.shop.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dhr.shop.service.ItemCatService;
import com.dhr.shop.utils.EasyUITreeNode;

/**
 * @ClassName: ItemCatController
 * @Description: TODO(商品类目表现层)
 * @author Mr DU
 * @date 2019年3月29日 /item/cat/list
 */
@Controller
public class ItemCatController {

	@Autowired
	private ItemCatService itemCatService;

	@RequestMapping("/item/cat/list")
	@ResponseBody
	public List<EasyUITreeNode> getItemCatList(@RequestParam(value = "id", defaultValue = "0") Long parentId) {
		// 注入service
		List<EasyUITreeNode> easyUITreeNodes = itemCatService.getItemCatList(parentId);
		return easyUITreeNodes;
	}
}
