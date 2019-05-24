package com.dhr.shop.cart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dhr.shop.cart.jedis.impl.JedisClient;
import com.dhr.shop.cart.service.CartService;
import com.dhr.shop.pojo.TbItem;
import com.dhr.shop.utils.JsonUtils;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU 购物车业务
 */
@Service
public class CartServiceImpl implements CartService {

	// 注入redis客户端
	@Autowired
	private JedisClient jedisClient;

	@Value("${TT_CART_REDIS_PRE_KEY}")
	private String TT_CART_REDIS_PRE_KEY;

	/**
	 * 添加到购物车
	 * 
	 * @param userId
	 * @param item
	 * @param num
	 * @return
	 */
	@Override
	public TaoShopResult addCartItem(Long userId, TbItem item, Integer num) {
		// 1.查询该商品是否已经存在与redis
		TbItem tbItem = getTbItemByUserId(userId, item.getId());
		// 2.存在就修改数量
		if (tbItem != null) {
			System.out.println(tbItem.getNum() + num);
			item.setNum(tbItem.getNum() + num);
			jedisClient.hset(TT_CART_REDIS_PRE_KEY + ":" + userId, item.getId() + "", JsonUtils.objectToJson(item));
		} else {
			// 3.不存在就添加新商品
			// 添加一个图片
			item.setNum(num);
			String images = item.getImage();
			if (StringUtils.isNotBlank(images)) {
				String[] split = images.split(",");
				item.setImage(split[0]);
			}

			jedisClient.hset(TT_CART_REDIS_PRE_KEY + ":" + userId, item.getId() + "", JsonUtils.objectToJson(item));
		}

		return TaoShopResult.ok();
	}

	/**
	 * 根据用户查询商品是否已经在redis中了
	 * 
	 * @param userId
	 * @param itemId
	 * @return
	 */
	@Override
	public TbItem getTbItemByUserId(Long userId, Long itemId) {
		String hget = jedisClient.hget("TT_CART_REDIS_PRE_KEY" + ":" + userId, itemId + "");
		if (StringUtils.isNotBlank(hget)) {
			TbItem tbItem = JsonUtils.jsonToPojo(hget, TbItem.class);
			return tbItem;
		}
		return null;
	}

	/**
	 * 查询该用户购物车所有数据
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public List<TbItem> getCartListByUserId(Long userId) {
		Map<String, String> map = jedisClient.getAll(TT_CART_REDIS_PRE_KEY + ":" + userId);
		List<TbItem> list = new ArrayList<>();
		if (map != null) {
			for (Entry<String, String> item : map.entrySet()) {
				TbItem tbItem = JsonUtils.jsonToPojo(item.getValue(), TbItem.class);
				list.add(tbItem);
			}
			return list;
		}
		return null;
	}

	/**
	 * @param userId
	 * @param itemId
	 * @param num
	 * @return
	 */
	@Override
	public TaoShopResult updateCartItem(Long userId, Long itemId, Integer num) {
		// 1.调用查询商品是否在购物车方法
		TbItem tbItem = getTbItemByUserId(userId, itemId);
		if (tbItem != null) {
			tbItem.setNum(num);
			jedisClient.hset(TT_CART_REDIS_PRE_KEY + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
		}
		return TaoShopResult.ok();
	}

	/**
	 * @param userId
	 * @param itemId
	 * @return
	 */
	@Override
	public TaoShopResult deleteCartItem(Long userId, Long itemId) {
		jedisClient.del(TT_CART_REDIS_PRE_KEY + ":" + userId, itemId + "");
		return TaoShopResult.ok();
	}

}
