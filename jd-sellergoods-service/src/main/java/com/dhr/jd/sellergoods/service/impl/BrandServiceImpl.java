package com.dhr.jd.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.sellergoods.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.mapper.TbBrandMapper;
import com.jd.pojo.TbBrand;
import com.jd.pojo.TbBrandExample;
import com.jd.pojo.TbBrandExample.Criteria;
import com.util.JDResult;
import com.util.PageResult;

/**
 * @author ali
 *
 */
@Service
public class BrandServiceImpl implements BrandService {

	// 注入mapper
	@Autowired
	private TbBrandMapper brandMapper;

	/**
	 * @return
	 */
	@Override
	public List<TbBrand> findAllBrand() {
		return brandMapper.selectByExample(null);
	}

	/**
	 * 分页查询品牌
	 * 
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	@Override
	public PageResult findBrandByPage(int pageSize, int pageNumber) {
		// 1.创建pageHelp对象
		PageHelper.startPage(pageNumber, pageSize);
		// 2.查询数据
		List<TbBrand> list = brandMapper.selectByExample(null);
		// 3.将数据放到pageInfo
		PageInfo<TbBrand> pageInfo = new PageInfo<>(list);
		// 4.设置数据到包装类
		PageResult result = new PageResult((int) pageInfo.getTotal(), pageInfo.getList());
		// 5.返回
		return result;
	}

	/**
	 * @param brand
	 */
	@Override
	public void saveBrand(TbBrand brand) {
		int i = brandMapper.insertSelective(brand);
	}

	/**
	 * @param id
	 * @return
	 */
	@Override
	public JDResult findBrandById(Integer id) {
		TbBrand brand = brandMapper.selectByPrimaryKey(id.longValue());
		JDResult jdResult = new JDResult();
		jdResult.setStatus(200);
		jdResult.setObject(brand);
		return jdResult;
	}

	/**
	 * @param brand
	 */
	@Override
	public void updateBrand(TbBrand brand) {
		brandMapper.updateByPrimaryKeySelective(brand);
	}

	/**
	 * @param id
	 * @return
	 */
	@Override
	public int deleteBrand(List<Long> id) {
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(id);
		int i = brandMapper.deleteByExample(example);
		return i;
	}

	/**
	 * @param brand
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageResult searchFindBrand(TbBrand brand, int pageNumber, int pageSize) {
		// 创建查询条件
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if (brand != null) {
			if (brand.getName() != null && brand.getName().trim().length() > 0) {
				criteria.andNameLike("%" + brand.getName() + "%");
			}
			if (brand.getFirstChar() != null && brand.getFirstChar().trim().length() > 0) {
				criteria.andFirstCharEqualTo(brand.getFirstChar());
			}
		}
		PageHelper.startPage(pageNumber, pageSize);
		List<TbBrand> list = brandMapper.selectByExample(example);
		PageInfo<TbBrand> pageInfo = new PageInfo<>(list);
		// 封装到包装对象
		PageResult result = new PageResult((int) pageInfo.getTotal(), pageInfo.getList());
		return result;
	}

	/**
	 * @return
	 */
	@Override
	public List<Map> findBrandList() {
		return brandMapper.finBrandList();
	}

}
