package com.dhr.shop.cart.service;

import java.util.List;

import com.dhr.shop.pojo.TbItem;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU 购物车接口
 */
public interface CartService {

	/**
	 * 添加到购物车
	 * 
	 * @param userId用户ID
	 * @param item商品
	 * @param num数量
	 * @return
	 */
	TaoShopResult addCartItem(Long userId, TbItem item, Integer num);

	/**
	 * 根据用户和商品Id，查看该商品是否存在这个用户的购物车中
	 * 
	 * @param userId
	 * @param itemId
	 * @return null 不存在 否则就存在直接是修改数量
	 */
	TbItem getTbItemByUserId(Long userId, Long itemId);

	/**
	 * 根据userId查询购物车
	 * 
	 * @param userId
	 * @return
	 */
	List<TbItem> getCartListByUserId(Long userId);

	/**
	 * 更新商品
	 * 
	 * @param userId
	 * @param itemId
	 * @param num
	 * @return
	 */
	TaoShopResult updateCartItem(Long userId, Long itemId, Integer num);

	/**
	 * 删除商品
	 * 
	 * @param userId
	 * @param itemId
	 * @return
	 */
	TaoShopResult deleteCartItem(Long userId, Long itemId);
}
