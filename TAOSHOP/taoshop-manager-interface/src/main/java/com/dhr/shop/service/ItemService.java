/**
 * 
 */
package com.dhr.shop.service;

import java.util.List;

import com.dhr.shop.pojo.TbItem;
import com.dhr.shop.pojo.TbItemDesc;
import com.dhr.shop.utils.EasyUIDataGridResult;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @ClassName: ItemService
 * @Description: TODO(商品接口)
 * @author Mr DU
 * @date 2019年3月27日
 * 
 */
public interface ItemService {

	/**
	 * @Title: getItemList @Description: TODO(分页查询数据) @param @param
	 *         pageNumber @param @param pageSize @param @return @return
	 *         EasyUIDataGridResult @throws
	 */
	EasyUIDataGridResult getItemList(int pageNumber, int pageSize);

	/**
	 * 
	 * @Title: saveItem @Description: TODO(增加商品) @param @param item @param @param
	 *         desc @param @return @return TaoShopResult @throws
	 */
	TaoShopResult saveItem(TbItem item, String desc);

	/**
	 * @Title: delItem @param @param params @param @return @return
	 *         TaoShopResult @throws
	 */
	TaoShopResult delItem(long ids);

	/**
	 * @Title: inStock @param @param valueOf @param @return @return
	 *         TaoShopResult @throws
	 */

	TaoShopResult inStockItem(List<Long> lists);

	/**
	 * @Title: reshelf @param @param valueOf @param @return @return
	 *         TaoShopResult @throws
	 */

	TaoShopResult reshelf(List<Long> list);

	/**
	 * @Title: getItemById @param @param id @param @return @return TbItem @throws
	 */

	TbItemDesc getItemById(Long id);

	/**
	 * 修改商品
	 * 
	 * @Title: updateItem @param @param item @param @param desc @param @param
	 *         id @param @return @return TaoShopResult @throws
	 */

	TaoShopResult updateItem(TbItem item, TbItemDesc desc, Long id);

	/**
	 * 根据商品Id查询商品信息
	 * 
	 * @param itemId
	 * @return
	 */
	TbItem getItemDetailById(long itemId);

	/**
	 * 根据商品Id查询商品详情
	 * 
	 * @param itemId
	 * @return
	 */
	TbItemDesc getItemDetailDesc(long itemId);

}
