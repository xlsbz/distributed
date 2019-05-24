package com.dhr.jd.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.cart.service.CartService;
import com.jd.mapper.TbItemMapper;
import com.jd.pojo.TbItem;
import com.jd.pojo.TbOrderItem;
import com.pojo.group.Cart;

/**
 * @author ali购物车实现
 *
 */
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private RedisTemplate<String, ?> redisTemplate;

	/**
	 * @param cartList:原来购物车的商品列表
	 * @param itemId:商品项ID
	 * @param num:购买数量
	 * @return
	 */
	@Override
	public List<Cart> addItemCart(List<Cart> cartList, Long itemId, Integer num) {
		// 1.先根据商品Id查询出sku列表
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		// 1.1获取商家ID
		String sellerId = "";
		if (item == null) {
			throw new RuntimeException("商品不存在!");
		} else if (!item.getStatus().equals("1")) {
			throw new RuntimeException("商品状态不正常!");
		} else {
			sellerId = item.getSellerId();
		}
		// 2.根据商家ID判断购物车中是否有该商家的商品
		Cart cart = searchCart(cartList, sellerId);

		// 2.1.如果有，再判断该商品是否存在该商家购物项的商品列表
		if (cart != null) {
			TbOrderItem orderItem = searchOrderItem(cart, itemId);
			if (orderItem != null) {
				// 修改数量
				orderItem.setNum(orderItem.getNum() + num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));
				// 如果数量为0，移除该商品项
				if (orderItem.getNum() == 0) {
					cart.getOrderItemList().remove(orderItem);
				}
				// 如果移除后该商家没有商品在购物车则移除该商家的购物车
				if (cart.getOrderItemList().size() == 0) {
					cartList.remove(cart);
				}
			} else {
				// 如没有这个商品项，向该商家购物列表中添加一个新的商品
				orderItem = createOrderItem(item, num);
				cart.getOrderItemList().add(orderItem);
			}
		} else {
			// 2.2.如果没有，则创建一个新的商家购物车，将商品添加到这个 新购物车中
			cart = new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());
			TbOrderItem orederItem = createOrderItem(item, num);
			List<TbOrderItem> orderItems = new ArrayList<TbOrderItem>();
			orderItems.add(orederItem);
			cart.setOrderItemList(orderItems);
			cartList.add(cart);

		}

		return cartList;
	}

	/**
	 * 根据购物车查询是否某个商家的商品
	 * 
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart searchCart(List<Cart> cartList, String sellerId) {
		for (Cart cart : cartList) {
			if (cart.getSellerId().equals(sellerId)) {
				// 购物车中存在该商家的购物车
				return cart;
			}
		}
		return null;
	}

	/**
	 * 根据商品项列表查询是否存在该商品
	 * 
	 * @param orderItem
	 * @param sellerId
	 * @return
	 */
	private TbOrderItem searchOrderItem(Cart cart, Long itemId) {
		for (TbOrderItem oi : cart.getOrderItemList()) {
			if (oi.getItemId().longValue() == itemId.longValue()) {
				// 2.1.1.如果存在，返回购物项对象
				return oi;
			}
		}
		return null;
	}

	/**
	 * 根据商品详情和数量创建一个购物明细
	 * 
	 * @param item
	 * @param num
	 * @return
	 */
	private TbOrderItem createOrderItem(TbItem item, Integer num) {
		if (num < 0) {
			throw new RuntimeException("数量非法!");
		}
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setNum(num);
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
		return orderItem;
	}

	/**
	 * @param username
	 * @return
	 */
	@Override
	public List<Cart> findAllCartFromRedis(String username) {
		System.out.println("从redis中读取数据" + username);
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("JDCART").get(username);
		if (cartList == null) {
			cartList = new ArrayList<>();
		}
		return cartList;
	}

	/**
	 * @param cartList
	 * @param username
	 */
	@Override
	public void addItemCartToRedis(List<Cart> cartList, String username) {
		System.out.println("从redis中存数据" + cartList.size());
		redisTemplate.boundHashOps("JDCART").put(username, cartList);
	}

	/**
	 * @param cart1
	 * @param cart2
	 * @return
	 */
	@Override
	public List<Cart> mergeCart(List<Cart> cart1, List<Cart> cart2) {
		for (Cart cart : cart2) {
			for (TbOrderItem oi : cart.getOrderItemList()) {
				// 以一个购物车作为源，另一个向这个购物车添加即可
				cart1 = addItemCart(cart1, oi.getItemId(), oi.getNum());
			}
		}
		System.out.println("合并购物车" + cart1.size());
		return cart1;
	}

}
