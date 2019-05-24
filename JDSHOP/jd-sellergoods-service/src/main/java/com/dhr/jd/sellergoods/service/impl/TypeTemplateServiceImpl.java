package com.dhr.jd.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dhr.jd.sellergoods.service.TypeTemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.mapper.TbSpecificationOptionMapper;
import com.jd.mapper.TbTypeTemplateMapper;
import com.jd.pojo.TbSpecificationOption;
import com.jd.pojo.TbSpecificationOptionExample;
import com.jd.pojo.TbTypeTemplate;
import com.jd.pojo.TbTypeTemplateExample;
import com.jd.pojo.TbTypeTemplateExample.Criteria;
import com.util.PageResult;

/**
 * @author ali 模板管理实现类
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	// 注入mapper
	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	/**
	 * @return 查询所有
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 分页查询
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(null);
		PageInfo<TbTypeTemplate> pageInfo = new PageInfo<TbTypeTemplate>(list);
		PageResult result = new PageResult((int) pageInfo.getTotal(), pageInfo.getList());
		return result;
	}

	/**
	 * 新增
	 * 
	 * @param typeTemplate
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insertSelective(typeTemplate);
	}

	/**
	 * @param typeTemplate
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
	}

	/**
	 * 查询单个
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id) {
		TbTypeTemplate template = typeTemplateMapper.selectByPrimaryKey(id);
		return template;
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 */
	@Override
	public void delete(Long[] ids) {
		List<Long> id = new ArrayList<Long>();
		for (Long l : ids) {
			id.add(l);
		}
		TbTypeTemplateExample example = new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(id);
		typeTemplateMapper.deleteByExample(example);
	}

	/**
	 * 搜索
	 * 
	 * @param typeTemplate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbTypeTemplateExample example = new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();

		if (typeTemplate != null) {
			if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
				criteria.andNameLike("%" + typeTemplate.getName() + "%");
			}
			if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
				criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
			}

			if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
				criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
			}

			if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
				criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
			}

		}
		List<TbTypeTemplate> list = typeTemplateMapper.selectByExample(example);
		PageInfo<TbTypeTemplate> pageInfo = new PageInfo<TbTypeTemplate>(list);
		// 执行缓存
		saveRedis();
		return new PageResult((int) pageInfo.getTotal(), pageInfo.getList());
	}

	/**
	 * 把规格的所有选项关联到规格map里
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public List<Map> findSpecOptionList(Long id) {
		TbTypeTemplate template = typeTemplateMapper.selectByPrimaryKey(id);
		// 把模板对象的规格json数据转为map
		List<Map> list = JSONArray.parseArray(template.getSpecIds(), Map.class);
		for (Map map : list) {
			// 遍历查询每个规格的选项
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			com.jd.pojo.TbSpecificationOptionExample.Criteria createCriteria = example.createCriteria();
			createCriteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
			List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
			map.put("options", options);
		}
		return list;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 缓存品牌和规格数据到redis---->执行后台查询执行这个缓存
	 */
	private void saveRedis() {
		// 1.查询出所有模板
		List<TbTypeTemplate> list = findAll();
		// 2.遍历取出模板的品牌和规格
		try {
			for (TbTypeTemplate temp : list) {
				String brandIds = temp.getBrandIds();
				List<Map> brand = JSON.parseArray(brandIds, Map.class);
				redisTemplate.boundHashOps("brandList").put(temp.getId(), brand);
				// 取出模板id对应的规格选项
				List<Map> spec = findSpecOptionList(temp.getId());
				redisTemplate.boundHashOps("specList").put(temp.getId(), spec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
