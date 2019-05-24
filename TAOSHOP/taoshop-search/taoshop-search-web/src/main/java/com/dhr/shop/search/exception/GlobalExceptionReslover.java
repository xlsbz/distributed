package com.dhr.shop.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Mr DU 处理搜索的全局异常处理器
 *
 */
public class GlobalExceptionReslover implements HandlerExceptionResolver {

	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @param ex
	 * @return
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// 1.将错误写道日志文件
		Logger logger = LoggerFactory.getLogger(GlobalExceptionReslover.class);
		logger.error("搜索程序出现错误", ex);
		// 发邮件
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", "不好意思，系统发生异常！");
		modelAndView.setViewName("error/exception");
		return modelAndView;
	}

}
