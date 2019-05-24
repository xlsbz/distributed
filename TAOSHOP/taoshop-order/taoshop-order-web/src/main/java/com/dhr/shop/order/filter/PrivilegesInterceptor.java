package com.dhr.shop.order.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dhr.shop.pojo.TbUser;
import com.dhr.shop.sso.service.UserService;
import com.dhr.shop.utils.CookieUtils;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU 权限拦截
 */
public class PrivilegesInterceptor implements HandlerInterceptor {

	@Autowired
	private UserService userService;

	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 进入目标方法之前执行拦截
		// 1.获取页面的token查询sso系统的用户
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		TaoShopResult result = userService.getUserByToken(token);
		if (result.getStatus() != 200) {
			if (result.getData() == null) {
				// 用户不存在重定向到登录页 redirect 被拦截之前获得到那个页面的url，登录成功后跳转到请求被拦截的那个页面
				response.sendRedirect("http://localhost:8088/page/login?redirect=" + request.getRequestURL());
				return false;
			}
		} else {
			TbUser tbUser = (TbUser) result.getData();
			request.setAttribute("TT_USER", tbUser);
			return true;
		}
		return false;
	}

	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 * @throws Exception
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// 执行方法之后，返回视图之前执行
	}

	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @param ex
	 * @throws Exception
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// 返回视图之后执行
	}

}
