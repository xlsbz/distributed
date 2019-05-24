package com.dhr.jd.cart.service;

import java.util.List;

import com.pojo.group.Cart;

/**
 * @author ali 购物车接口
 */
public interface CartService {

	/**
	 * 添加到购物车
	 * 
	 * @param cartList
	 * @param itemId
	 * @param num
	 * @return
	 */
	List<Cart> addItemCart(List<Cart> cartList, Long itemId, Integer num);

	/**
	 * 查询redis的购物车数据
	 * 
	 * @param username
	 * @return
	 */
	List<Cart> findAllCartFromRedis(String username);

	/**
	 * 购物车保存到redis
	 * 
	 * @param cartList
	 * @param username
	 */
	void addItemCartToRedis(List<Cart> cartList, String username);

	/**
	 * 合并购物车
	 * 
	 * @param cart1
	 * @param cart2
	 * @return
	 */
	List<Cart> mergeCart(List<Cart> cart1, List<Cart> cart2);
}
