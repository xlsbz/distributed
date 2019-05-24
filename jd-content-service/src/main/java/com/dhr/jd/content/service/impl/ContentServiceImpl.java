package com.dhr.jd.content.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.content.service.ContentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.mapper.TbContentCategoryMapper;
import com.jd.mapper.TbContentMapper;
import com.jd.pojo.TbContent;
import com.jd.pojo.TbContentExample;
import com.jd.pojo.TbContentExample.Criteria;
import com.util.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content) {
		// 清楚缓存
		if (content.getCategoryId() != null) {
			redisTemplate.boundHashOps("contentJD").delete(content.getCategoryId());
		}
		contentMapper.updateByPrimaryKey(content);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id) {
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			// 清楚缓存
			// System.out.println(category);
			redisTemplate.delete("contentJD");
			contentMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();

		if (content != null) {
			if (content.getTitle() != null && content.getTitle().length() > 0) {
				criteria.andTitleLike("%" + content.getTitle() + "%");
			}
			if (content.getUrl() != null && content.getUrl().length() > 0) {
				criteria.andUrlLike("%" + content.getUrl() + "%");
			}
			if (content.getPic() != null && content.getPic().length() > 0) {
				criteria.andPicLike("%" + content.getPic() + "%");
			}
			if (content.getStatus() != null && content.getStatus().length() > 0) {
				criteria.andStatusLike("%" + content.getStatus() + "%");
			}

		}

		Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TbContent> findCategoryById(Long id) {
		// 查询缓存
		List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("contentJD").get(id);
		if (list == null) {
			// System.out.println("去找数据库了");
			// 缓存没有数据从数据库查
			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();
			// 设置状态和ID
			criteria.andCategoryIdEqualTo(id);
			criteria.andStatusEqualTo("1");
			list = contentMapper.selectByExample(example);
			// 设置到缓存
			redisTemplate.boundHashOps("contentJD").put(id, list);
		} else {

			// System.out.println("缓存有数据了");
		}
		return list;
	}

}
