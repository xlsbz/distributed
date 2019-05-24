package com.dhr.jd.search.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dhr.jd.search.service.ImportAllItemService;

import entity.Result;

/**
 * @author ali 导入索引库
 */
@Controller
public class ImportAllItemController {

	@Reference
	private ImportAllItemService importAllItemService;

	@RequestMapping("/importAllItem")
	@ResponseBody
	public Result importAllItem() {
		// 调用服务
		try {
			importAllItemService.importAllItem();
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "导入失败!");
		}
		return new Result(true, "导入成功");

	}

}
