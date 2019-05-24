package com.dhr.jd.cart.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dhr.jd.cart.service.CartService;
import com.pojo.group.Cart;
import com.util.CookieUtil;

import entity.Result;

/**
 * @author ali 购物车处理
 */
@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout = 6000)
	private CartService cartService;

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;

	@RequestMapping("/showName")
	public Map<String, String> showName() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", name);

		return map;
	}

	/**
	 * 获取用户登陆名
	 * 
	 * @return
	 */
	private String getUserName() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		return name;
	}

	/**
	 * 查询购物车
	 * 
	 * @return
	 */
	@RequestMapping("/findAllCart")
	public List<Cart> findAllCart() {
		String userName = getUserName();
		List<Cart> cart_redis = null;
		List<Cart> cart_cookie = null;
		// 用户未登录
		// 1.得到cookie的数据
		String cookieValue = CookieUtil.getCookieValue(request, "JD_CART", "UTF-8");
		if (cookieValue == null || "".equals(cookieValue)) {
			cookieValue = "[]";
		}
		// 2.判断是否有数据并解析
		cart_cookie = JSON.parseArray(cookieValue, Cart.class);
		// 用户已经登陆,向redis读取数据
		if (!userName.equals("anonymousUser")) {
			// 读取redis
			cart_redis = cartService.findAllCartFromRedis(userName);
			if (cart_cookie.size() > 0) {
				// 合并购物车
				List<Cart> mergeCart = cartService.mergeCart(cart_redis, cart_cookie);
				// 删除cookie
				CookieUtil.deleteCookie(request, response, "JD_CART");
				// 将合并后的数据存到redis
				cartService.addItemCartToRedis(mergeCart, userName);
			}
			return cart_redis;
		}
		return cart_cookie;
	}

	/**
	 * 添加到购物车
	 * 
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping("/add2Cart")
	@CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
	public Result add2Cart(Long itemId, Integer num) {
		// 设置允许跨域请求
//		response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
//		// 处理cookie可以跨域
//		response.setHeader("Access-Control-Allow-Credentials", "true");

		// 1.获取购物车列表
		List<Cart> cartList = findAllCart();
		// 2.调用添加购车车方法
		cartList = cartService.addItemCart(cartList, itemId, num);
		try {
			String userName = getUserName();
			if (!userName.equals("anonymousUser")) {
				// 向redis存数据
				cartService.addItemCartToRedis(cartList, userName);
			} else {
				// 3.放到cookie
				String jsonString = JSON.toJSONString(cartList);
				CookieUtil.setCookie(request, response, "JD_CART", jsonString, 3600 * 24, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败!");
		}
		return new Result(true, "添加成功!");
	}
}
