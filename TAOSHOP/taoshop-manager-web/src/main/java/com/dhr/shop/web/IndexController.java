package com.dhr.shop.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**  
 * @ClassName: IndexConyroller  
 * @Description: TODO(首页表现层)  
 * @author Mr DU  
 * @date 2019年3月27日  
*    
*/
@Controller
public class IndexController {
	
	

	@RequestMapping("/")
	/**
	 * 
	 * @Title: indexPage  
	 * @Description: TODO(前往主页)  
	 * @param @return    
	 * @return String
	 * @throws
	 */
	public String indexPage() {
		return "index";
	}
	
	
	@RequestMapping("/{page}")
	/**
	 * 
	 * @Title: showItemList  
	 * @Description: TODO(商品列表页)  
	 * @param @param page
	 * @param @return    
	 * @return String
	 * @throws
	 */
	public String showItemList(@PathVariable String page) {
		return page;
	}
	
	
	
}
