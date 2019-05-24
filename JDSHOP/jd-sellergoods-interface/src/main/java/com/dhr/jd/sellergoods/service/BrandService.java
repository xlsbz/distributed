package com.dhr.jd.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.jd.pojo.TbBrand;
import com.util.JDResult;
import com.util.PageResult;

/**
 * @author ali 品牌接口
 */
public interface BrandService {

	/**
	 * 查询所有品牌
	 */
	List<TbBrand> findAllBrand();

	/**
	 * 分页查询品牌
	 * 
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	PageResult findBrandByPage(int pageSize, int pageNumber);

	/**
	 * 录入品牌
	 * 
	 * @param brand
	 */
	void saveBrand(TbBrand brand);

	/**
	 * 查询单个
	 * 
	 * @param id
	 * @return
	 */
	JDResult findBrandById(Integer id);

	/**
	 * 修改
	 * 
	 * @param brand
	 */
	void updateBrand(TbBrand brand);

	/**
	 * 批量删除
	 * 
	 * @param id
	 * @return
	 */
	int deleteBrand(List<Long> id);

	/**
	 * 条件查询
	 * 
	 * @param brand
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	PageResult searchFindBrand(TbBrand brand, int pageNumber, int pageSize);

	/**
	 * 查询品牌
	 * 
	 * @return
	 */
	List<Map> findBrandList();

}
