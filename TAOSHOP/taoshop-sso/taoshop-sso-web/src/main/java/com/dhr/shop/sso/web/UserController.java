package com.dhr.shop.sso.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dhr.shop.pojo.TbUser;
import com.dhr.shop.sso.service.UserService;
import com.dhr.shop.utils.CookieUtils;
import com.dhr.shop.utils.JsonUtils;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU 处理用户请求
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * 检测用户名
	 * 
	 * @param param
	 * @param type
	 * @return
	 */
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public TaoShopResult checkUser(@PathVariable String param, @PathVariable Integer type) {
		// 1.注入服务
		// 2.查询
		TaoShopResult result = userService.userCheck(param, type);
		// 3.返回
		return result;
	}

	/**
	 * 用户注册
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public TaoShopResult registerUser(TbUser user) {
		// 注入服务
		// 调用注册方法
		TaoShopResult result = userService.registerUser(user);
		return result;
	}

	/**
	 * 用户登录
	 * 
	 * @param username
	 * @param password
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public TaoShopResult loginUser(String username, String password, HttpServletRequest request,
			HttpServletResponse response) {
		TaoShopResult result = userService.loginUser(username, password);
		// 把返回的token设置到cookie cookie要跨域
		if (result.getStatus() == 200) {
			String token = result.getData().toString();
			CookieUtils.setCookie(request, response, "TT_TOKEN", token);
		}
		return result;
	}

	/**
	 * 查询用户
	 * 
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/token/{token}", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
	@ResponseBody
	public String getUserBean(@PathVariable String token, String callback) {
		TaoShopResult result = userService.getUserByToken(token);
		// 检查是否为jsonp请求
		if (StringUtils.isNotBlank(callback)) {
			String rs = callback + "(" + JsonUtils.objectToJson(result) + ");";
			return rs;
		}
		return JsonUtils.objectToJson(result);
	}

	/**
	 * 退出登录
	 * 
	 * @param token
	 * @return
	 */
	@RequestMapping("/quit/{token}")
	public String quitUserToken(@PathVariable String token, HttpServletRequest request, HttpServletResponse response) {
		userService.quitUserByToken(token);
		// 清除cookie
		CookieUtils.deleteCookie(request, response, "TT_TOKEN");
		return "redirect:/user/logout";
	}

	@RequestMapping("/logout")
	public String logout() {
		return "login";
	}
}
