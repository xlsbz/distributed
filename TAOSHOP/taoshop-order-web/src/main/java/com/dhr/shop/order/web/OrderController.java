package com.dhr.shop.order.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dhr.shop.cart.service.CartService;
import com.dhr.shop.order.pojo.OrderInfo;
import com.dhr.shop.order.service.OrderService;
import com.dhr.shop.pojo.TbItem;
import com.dhr.shop.pojo.TbUser;
import com.dhr.shop.sso.service.UserService;
import com.dhr.shop.utils.CookieUtils;
import com.dhr.shop.utils.JsonUtils;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU 处理订单业务
 */
@Controller
public class OrderController {

	@Value("${TT_TOKEN_KEY}")
	private String TT_TOKEN_KEY;
	@Value("${TT_CART_KEY}")
	private String TT_CART_KEY;

	// 注入sso
	@Autowired
	private UserService userService;
	// 注入cart
	@Autowired
	private CartService cartService;
	// 注入order
	@Autowired
	private OrderService orderService;

	/**
	 * 展示订单商品
	 * 
	 * @return
	 */
	@RequestMapping("/order/order-cart")
	public String showOrderItem(HttpServletRequest request, HttpServletResponse response) {
		// 1.获取用户登录信息
		TbUser user = null;
		// String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY);
		// if (StringUtils.isNotBlank(token)) {
		// // 调用sso服务
		// TaoShopResult result = userService.getUserByToken(token);
		// if (result.getStatus() == 200) {
		// user = (TbUser) result.getData();
		// }
		// }
		// 拦截器传来的request里面有user
		user = (TbUser) request.getAttribute("TT_USER");
		// 展示用户的支付方式和地址静态数据

		// 2.从redis中查询该用户的购物车数据
		List<TbItem> redisItem = cartService.getCartListByUserId(user.getId());
		// 3.获取cookie购物车数据
		List<TbItem> jsonToList = getCookieItem(request);
		// 4.合并购物车数据到redis
		// 遍历购物车所有商品
		if (jsonToList != null) {
			for (TbItem cItem : jsonToList) {
				boolean flag = false;
				if (redisItem != null) {
					// 对比redis中的商品
					for (TbItem rItem : redisItem) {
						// 判断redis中是否有该商品
						if (cItem.getId().longValue() == rItem.getId()) {
							// 有商品数量改变
							int num = cItem.getNum() + rItem.getNum();
							// 修改redis
							cartService.updateCartItem(user.getId(), rItem.getId(), num);
							flag = true;
						}
					}
				}
				// 直接添加到redis
				if (!flag) {
					cartService.addCartItem(user.getId(), cItem, cItem.getNum());
				}
			}
			// 5.合并后删除cookie
			if (!jsonToList.isEmpty()) {
				CookieUtils.deleteCookie(request, response, TT_CART_KEY);
			}
		}

		// 6.设置数据到request,从新取redis最新数据
		List<TbItem> redisnewItem = cartService.getCartListByUserId(user.getId());
		request.setAttribute("cartList", redisnewItem);
		return "order-cart";
	}

	/**
	 * 获取cookie的商品列表
	 * 
	 * @param request
	 * @return
	 */
	public List<TbItem> getCookieItem(HttpServletRequest request) {
		String cookieItem = CookieUtils.getCookieValue(request, TT_CART_KEY, true);
		List<TbItem> list = null;
		if (StringUtils.isNotBlank(cookieItem)) {
			list = JsonUtils.jsonToList(cookieItem, TbItem.class);
		}
		return list;
	}

	/**
	 * 创建订单
	 * 
	 * @param orderInfo
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/order/create", method = RequestMethod.POST)
	public String createOrder(HttpServletRequest request, OrderInfo info) {
		// 1.补全用户信息
		TbUser user = (TbUser) request.getAttribute("TT_USER");
		info.setUserId(user.getId());
		info.setBuyerNick(user.getUsername());

		// 2.调用orderInfoservice创建订单
		TaoShopResult result = orderService.creatOrder(info);

		// 3.根据返回值取出订单号
		String orderId = "";
		if (result.getStatus() == 200) {
			orderId = (String) result.getData();
		}

		// 4.回显数据订单号
		request.setAttribute("orderId", orderId);
		request.setAttribute("payment", info.getPayment());

		// 5.当前日期＋3天
		DateTime dateTime = new DateTime();
		DateTime time = dateTime.plusDays(3);
		request.setAttribute("date", time.toString("yyyy-MM-dd"));

		// 6.返回
		return "success";
	}

}
