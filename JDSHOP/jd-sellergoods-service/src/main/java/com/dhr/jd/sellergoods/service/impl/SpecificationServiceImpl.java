package com.dhr.jd.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.sellergoods.service.SpecificationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.mapper.TbSpecificationMapper;
import com.jd.mapper.TbSpecificationOptionMapper;
import com.jd.pojo.TbSpecification;
import com.jd.pojo.TbSpecificationExample;
import com.jd.pojo.TbSpecificationExample.Criteria;
import com.jd.pojo.TbSpecificationOption;
import com.jd.pojo.TbSpecificationOptionExample;
import com.pojo.group.Specification;
import com.util.PageResult;

/**
 * 规格服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 增加---->两个表
	 */
	@Override
	public void add(Specification specification) {
		// 向规格表中插入数据，返回自增的ID
		specificationMapper.insertSelective(specification.getSpecification());
		// 遍历插入规格项表
		for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
			// 设置id
			option.setSpecId(specification.getSpecification().getId());
			specificationOptionMapper.insertSelective(option);
		}
	}

	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification) {
		// 更新规格表
		specificationMapper.updateByPrimaryKeySelective(specification.getSpecification());
		// 删除原来所有的规格项列表
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.jd.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpecification().getId());
		// 删除数据
		specificationOptionMapper.deleteByExample(example);
		// 遍历插入规格项表
		for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
			// 设置id
			option.setSpecId(specification.getSpecification().getId());
			specificationOptionMapper.insertSelective(option);
		}
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id) {
		// 获取规格对象
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		// 通过规格对象的ID获取规格列表对象
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.jd.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> list = specificationOptionMapper.selectByExample(example);
		// 创建包装类
		Specification specification = new Specification();
		specification.setSpecification(tbSpecification);
		specification.setSpecificationOptionList(list);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		// 批量删除
		List<Long> id = new ArrayList<>();
		for (Long i : ids) {
			id.add(i);
		}
		TbSpecificationExample example = new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(id);
		specificationMapper.deleteByExample(example);
	}

	@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbSpecificationExample example = new TbSpecificationExample();
		Criteria criteria = example.createCriteria();

		if (specification != null) {
			if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
				criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
			}

		}

		Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * @return
	 */
	@Override
	public List<Map> findSpecList() {
		return specificationMapper.findSpecList();
	}

}
