package com.dhr.jd.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.sellergoods.service.ItemCatService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.mapper.TbItemCatMapper;
import com.jd.mapper.TbTypeTemplateMapper;
import com.jd.pojo.TbItemCat;
import com.jd.pojo.TbItemCatExample;
import com.jd.pojo.TbItemCatExample.Criteria;
import com.util.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);
		// 删除对应的redis缓存
		try {
			System.out.println("清除缓存");
			redisTemplate.delete("JD_CATEGORY");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat) {
		itemCatMapper.updateByPrimaryKey(itemCat);
		// 删除对应的redis缓存
		try {
			System.out.println("清除缓存");
			redisTemplate.delete("JD_CATEGORY");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id) {
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			itemCatMapper.deleteByPrimaryKey(id);
			// 删除对应的redis缓存
			try {
				redisTemplate.delete("JD_CATEGORY");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();

		if (itemCat != null) {
			if (itemCat.getName() != null && itemCat.getName().length() > 0) {
				criteria.andNameLike("%" + itemCat.getName() + "%");
			}

		}

		Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(example);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 运营商执行查询分类时把数据存到redis
	 * 
	 * @return
	 */
	@Override
	public List<TbItemCat> findNextCat(Integer id) {
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id.longValue());
		// 查询出所有分类数据存到redis
		// 判断是否存在
		if (!redisTemplate.hasKey("JD_CATEGORY")) {
			System.out.println("加入缓存");
			saveRedis();
		}
		// 查询
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		return list;
	}

	private void saveRedis() {
		// 查询出所有分类数据存到redis
		List<TbItemCat> findAll = findAll();
		try {
			if (findAll != null) {
				for (TbItemCat itemCat : findAll) {
					redisTemplate.boundHashOps("JD_CATEGORY").put(itemCat.getName(), itemCat.getTypeId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	@Override
	public List<Map> findTemplate() {
		return typeTemplateMapper.findTemplate();
	}

}
