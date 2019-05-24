package com.dhr.shop.cart.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dhr.shop.cart.service.CartService;
import com.dhr.shop.pojo.TbItem;
import com.dhr.shop.pojo.TbUser;
import com.dhr.shop.service.ItemService;
import com.dhr.shop.sso.service.UserService;
import com.dhr.shop.utils.CookieUtils;
import com.dhr.shop.utils.JsonUtils;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU 购物车
 */
@Controller
public class CartController {

	// 注入商品服务
	@Autowired
	private ItemService itemService;
	// 注入sso
	@Autowired
	private UserService userService;
	// 注入购物车服务
	@Autowired
	private CartService cartService;

	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable long itemId, Integer num, HttpServletRequest request,
			HttpServletResponse response) {
		// 1.接收商品Id
		// 2.调用sso服务判断用户是否登录
		// 获取页面的token
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		TaoShopResult result = userService.getUserByToken(token);
		if (result.getStatus() == 200) {
			// 3.如果登录，则向redis存放数据
			TbUser user = (TbUser) result.getData();
			// 调用商品服务
			TbItem tbItem = itemService.getItemDetailById(itemId);
			// 调用购物车服务
			cartService.addCartItem(user.getId(), tbItem, num);
		} else {
			// 4 .没有登录就存到cookie
			addCookieItem(itemId, num, request, response);
		}

		return "cartSuccess";
	}

	/**
	 * 查看购物车
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/cart/cart")
	public String getCartList(Model model, HttpServletRequest request) {
		// 1.根据cookie查看用户是否登录
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		TaoShopResult result = userService.getUserByToken(token);
		if (result.getStatus() == 200) {
			// 登陆了查询该用户购物车
			TbUser user = (TbUser) result.getData();
			List<TbItem> list = cartService.getCartListByUserId(user.getId());
			model.addAttribute("cartList", list);
		} else {
			// 查看cookie购物车
			List<TbItem> cookieList = getCookieList(request);
			model.addAttribute("cartList", cookieList);
		}
		return "cart";
	}

	/**
	 * 修改购物车
	 * 
	 * @param num
	 * @param itemId
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public TaoShopResult updateCartItem(@PathVariable Integer num, @PathVariable long itemId,
			HttpServletRequest request, HttpServletResponse response) {
		// 1.调用sso服务查询用户
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		TaoShopResult result = userService.getUserByToken(token);
		if (result.getStatus() == 200) {
			TbUser user = (TbUser) result.getData();
			// 调用服务
			cartService.updateCartItem(user.getId(), itemId, num);
		} else {
			// 没登陆，修改cookie
			updateCookieItem(itemId, num, request, response);
		}
		return TaoShopResult.ok();
	}

	/**
	 * 删除购物车数据
	 * 
	 * @param itemId
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable long itemId, HttpServletRequest request, HttpServletResponse response) {
		// 调用sso
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		TaoShopResult result = userService.getUserByToken(token);
		if (result.getStatus() == 200) {
			TbUser user = (TbUser) result.getData();
			cartService.deleteCartItem(user.getId(), itemId);
		} else {
			// 从cookie里删除
			deleteCookieItem(itemId, request, response);
		}
		return "redirect:/cart/cart.html";
	}

	/**
	 * 从cookie读取商品列表
	 * 
	 * @param request
	 * @return
	 */
	public List<TbItem> getCookieList(HttpServletRequest request) {
		// 1.获得页面的cookie，从cookie中读取商品字符串
		String ttCart = CookieUtils.getCookieValue(request, "TT_CART", true);
		// 2.将字符串转换为对象
		List<TbItem> items = new ArrayList<>();
		if (StringUtils.isNotBlank(ttCart)) {
			items = JsonUtils.jsonToList(ttCart, TbItem.class);
		}
		// 3.返回
		return items;
	}

	/**
	 * 把商品添加到cookie购物车中
	 * 
	 * @param request
	 * @param response
	 */
	public void addCookieItem(long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
		// 1.从cookie中获取购物车列表
		List<TbItem> cookieList = getCookieList(request);
		// 2.判断商品是否存在购物车中
		boolean flag = false;
		for (TbItem tbItem : cookieList) {
			if (tbItem.getId() == itemId) {
				// 修改数量
				tbItem.setNum(tbItem.getNum() + num);
				flag = true;
				break;
			}
		}
		// 3.存在就修改数量
		if (flag) {
			CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(cookieList), true);
		} else {
			// 4 .不存在就直接将商品添加到cookie
			// 根据商品id调用服务查询商品信息
			TbItem item = itemService.getItemDetailById(itemId);
			// 设置数量
			item.setNum(num);
			// 设置一张图片
			if (StringUtils.isNotBlank(item.getImage())) {
				item.setImage(item.getImage().split(",")[0]);
			}
			// 添加到商品集合
			cookieList.add(item);
			// 设置到cookie
			CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(cookieList), true);
		}

	}

	/**
	 * 修改购物车cookie
	 * 
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 */
	public void updateCookieItem(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
		// 1.获取购物车列表
		List<TbItem> cookieList = getCookieList(request);
		// 2.查询是否有该商品
		boolean flag = false;
		for (TbItem tbItem : cookieList) {
			if (tbItem.getId() == itemId.longValue()) {
				// 修改数量
				tbItem.setNum(num);
				flag = true;
				break;
			}
		}
		if (flag) {
			CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(cookieList), true);
		} else {
			// 什么也不做
		}
	}

	/**
	 * 删除商品cookie
	 * 
	 * @param itemId
	 */
	public void deleteCookieItem(Long itemId, HttpServletRequest request, HttpServletResponse response) {
		// 1.获取购物车列表
		List<TbItem> cookieList = getCookieList(request);
		boolean flag = false;
		for (TbItem tbItem : cookieList) {
			if (tbItem.getId() == itemId.longValue()) {
				boolean b = cookieList.remove(tbItem);
				flag = true;
				break;
			}
		}
		// 重新设置cookie
		if (flag) {
			CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(cookieList), true);
		} else {
			// 什么也不做
		}
	}

}
