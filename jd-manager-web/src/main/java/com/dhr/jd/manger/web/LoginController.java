package com.dhr.jd.manger.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ali 运营商登陆
 */
@RestController
@RequestMapping("/login")
public class LoginController {

	/**
	 * 登陆
	 * 
	 * @return
	 */
	@RequestMapping("/loginName")
	public Map loginName() {
		// 用户名和密码如果匹配上配置文件 即获得security的name
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map map = new HashMap();
		map.put("loginName", name);
		return map;
	}

}
