package com.dhr.jd.user.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 显示用户登陆信息
 * 
 * @author ali
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {

	@RequestMapping("/showName")
	public Map<String, String> showName() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", name);

		return map;
	}
}
