package com.dhr.shop.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dhr.shop.pojo.TbItem;
import com.dhr.shop.pojo.TbItemDesc;
import com.dhr.shop.service.ItemService;
import com.dhr.shop.utils.EasyUIDataGridResult;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @ClassName: ItemController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Mr DU
 * @date 2019年3月27日
 * 
 */
@Controller
public class ItemController {

	// 注入接口
	@Autowired

	private ItemService itemService;

	/**
	 * @Title: showItemList @Description: TODO(商品列表页) @param @param
	 *         page @param @return @return String @throws
	 */
	@RequestMapping(value = "/item/list", method = RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		return itemService.getItemList(page, rows);
	}

	/**
	 * @Title: addItem @Description: TODO(添加商品) @param @param item @param @param
	 *         desc @param @return @return TaoShopResult @throws
	 */
	@RequestMapping("/item/save")
	@ResponseBody
	public TaoShopResult addItem(TbItem item, String desc) {
		return itemService.saveItem(item, desc);
	}

	/**
	 * 回显数据
	 * 
	 * @Title: editItem @param @return @return TaoShopResult @throws
	 */
	@RequestMapping("/rest/page/{item-edit}")
	public String editItem(@PathVariable(value = "item-edit") String item, String ids) {
		return item;
	}

	/**
	 * 异步加载商品描述 @Title: descItem @param @return @return String @throws
	 */
	@RequestMapping("/rest/item/query/item/desc/{id}")
	@ResponseBody
	public TaoShopResult descItem(@PathVariable Long id) {
		// 通过ID查询商品描述
		TbItemDesc desc = itemService.getItemById(id);
		return TaoShopResult.ok(desc);
	}

	/**
	 * 修改商品 @Title: updateItem @param @return @return TaoShopResult @throws
	 */
	@RequestMapping("/rest/item/update")
	@ResponseBody
	public TaoShopResult updateItem(TbItem item, TbItemDesc desc, Long id) {
		TaoShopResult result = itemService.updateItem(item, desc, id);
		return result.ok();
	}

	/**
	 * 删除 @Title: delItem @param @param params @param @return @return
	 * TaoShopResult @throws
	 */
	@RequestMapping("/rest/item/delete")
	@ResponseBody

	public TaoShopResult delItem(String ids) {
		// 判断是单个商品还是多个商品
		if (ids.contains(",")) {
			// 多个商品拆分字符串
			TaoShopResult result = null;
			String[] id = ids.split(",");
			for (String string : id) {
				result = itemService.delItem(Long.valueOf(string));
			}
			return result;
		}
		// 1.调用服务
		return itemService.delItem(Long.valueOf(ids));
	}

	/**
	 * 商品下架 @Title: inStock @param @param ids @param @return @return
	 * TaoShopResult @throws
	 */
	@RequestMapping("/rest/item/instock")
	@ResponseBody
	public TaoShopResult inStock(String ids) {
		// 判断是单个商品还是多个商品
		if (ids.contains(",")) {
			// 多个商品拆分字符串
			TaoShopResult result = null;
			String[] id = ids.split(",");
			// 封装到list集合
			List<Long> lists = new ArrayList<>();
			for (String string : id) {
				lists.add(Long.valueOf(string));
			}
			return itemService.inStockItem(lists);
		}
		// 1.调用服务
		List<Long> list = new ArrayList<>();
		list.add(Long.valueOf(ids));
		return itemService.inStockItem(list);
	}

	/**
	 * 商品上架 @Title: inStock @param @param ids @param @return @return
	 * TaoShopResult @throws
	 */
	@RequestMapping("/rest/item/reshelf")
	@ResponseBody
	public TaoShopResult reshelf(String ids) {
		// 判断是单个商品还是多个商品
		if (ids.contains(",")) {
			// 多个商品拆分字符串
			TaoShopResult result = null;
			String[] id = ids.split(",");
			// 封装到list集合
			List<Long> lists = new ArrayList<>();
			for (String string : id) {
				lists.add(Long.valueOf(string));
			}
			return itemService.reshelf(lists);
		}
		// 1.调用服务
		List<Long> list = new ArrayList<>();
		list.add(Long.valueOf(ids));
		return itemService.reshelf(list);
	}
}
