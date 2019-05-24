package com.dhr.jd.shop.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ali 显示用户登陆
 */
@RestController
@RequestMapping("/login")
public class LoginController {

	@RequestMapping("/showName")
	public Map showName() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String, String> map = new HashMap<String, String>();
		map.put("loginName", name);
		return map;
	}
}
