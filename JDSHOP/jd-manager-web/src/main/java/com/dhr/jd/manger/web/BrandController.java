package com.dhr.jd.manger.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dhr.jd.sellergoods.service.BrandService;
import com.jd.pojo.TbBrand;
import com.util.JDResult;
import com.util.PageResult;

/**
 * @author ali 控制器
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

	// 注入service服务
	@Reference
	private BrandService brandService;

	/**
	 * 查询所有品牌
	 * 
	 * @return
	 */
	@RequestMapping("/findAllBrand")
	public List<TbBrand> findAllBrand() {
		return brandService.findAllBrand();
	}

	/**
	 * 分页查询所有品牌
	 * 
	 * @return
	 */
	@RequestMapping("/findAllBrandByPage")
	public PageResult findAllBrandByPage(@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "1") int pageNumber) {
		return brandService.findBrandByPage(pageSize, pageNumber);
	}

	/**
	 * 录入品牌
	 * 
	 * @param brand
	 * @return
	 */
	@RequestMapping(value = "/saveBrand")
	public JDResult saveBrand(@RequestBody TbBrand brand) {
		JDResult result = new JDResult();
		try {
			brandService.saveBrand(brand);
			result.setMsg("录入成功!");
			result.setStatus(200);
			return result;
		} catch (Exception e) {
			result.setStatus(400);
			e.printStackTrace();
			return result;
		}
	}

	/**
	 * 查询单个品牌
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findBrandById/{id}")
	public JDResult findBrandById(@PathVariable Integer id) {
		return brandService.findBrandById(id);
	}

	/**
	 * 修改品牌
	 * 
	 * @param brand
	 * @return
	 */
	@RequestMapping("/updateBrand")
	public JDResult updateBrand(@RequestBody TbBrand brand) {
		JDResult result = new JDResult();
		try {
			brandService.updateBrand(brand);
			result.setMsg("修改成功!");
			result.setStatus(200);
			return result;
		} catch (Exception e) {
			result.setStatus(400);
			e.printStackTrace();
			return result;
		}
	}

	/**
	 * 删除品牌(批量)
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("/deleteBrand")
	public JDResult deleteBrand(Long[] ids) {
		JDResult result = new JDResult();
		// 将其转换为list
		List<Long> id = new ArrayList<Long>();
		for (Long l : ids) {
			id.add(l);
		}
		try {
			int i = brandService.deleteBrand(id);
			if (i > 0) {
				result.setMsg("删除成功!");
				result.setStatus(200);
				return result;
			}
		} catch (Exception e) {
			result.setStatus(400);
			e.printStackTrace();
			return result;
		}
		result.setStatus(400);
		return result;
	}

	/**
	 * 条件查询
	 * 
	 * @param brand
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/searchBrand", method = { RequestMethod.POST, RequestMethod.GET })
	public PageResult searchBrand(@RequestBody TbBrand brand, @RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "1") int pageNumber) {
		return brandService.searchFindBrand(brand, pageNumber, pageSize);
	}

	/**
	 * 查询品牌
	 * 
	 * @return
	 */
	@RequestMapping("/findBrandList")
	public List<Map> findBrandList() {
		return brandService.findBrandList();
	}
}
