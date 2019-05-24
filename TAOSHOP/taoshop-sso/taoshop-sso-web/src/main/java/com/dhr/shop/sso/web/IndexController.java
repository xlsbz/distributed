package com.dhr.shop.sso.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Mr DU
 *
 */
@Controller
public class IndexController {

	@RequestMapping("/page/{page}")
	public String gotoLoginPage(@PathVariable String page, Model model, String redirect) {
		model.addAttribute("redirect", redirect);
		return page;
	}
}
