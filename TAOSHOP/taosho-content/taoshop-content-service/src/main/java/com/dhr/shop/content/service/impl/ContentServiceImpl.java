/**
 * 
 */
package com.dhr.shop.content.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dhr.shop.content.jedis.impl.JedisClient;
import com.dhr.shop.content.service.ContentService;
import com.dhr.shop.dao.TbContentMapper;
import com.dhr.shop.pojo.TbContent;
import com.dhr.shop.pojo.TbContentExample;
import com.dhr.shop.pojo.TbContentExample.Criteria;
import com.dhr.shop.utils.EasyUIDataGridResult;
import com.dhr.shop.utils.TaoShopResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @ClassName: ContentServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Mr DU
 * @date 2019年4月2日
 * 
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper tbContentMapper;

	@Autowired
	private JedisClient jedis;
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;

	@Override
	public EasyUIDataGridResult getContentList(long id, Integer page, Integer rows) {
		// 1.注入mapper
		// 2.调用分页插件
		PageHelper.startPage(page, rows);// 第几页 显示多少条数据
		// 3.创建查询条件
		TbContentExample contentExample = new TbContentExample();
		Criteria criteria = contentExample.createCriteria();
		criteria.andCategoryIdEqualTo(id);
		// 4.查询数据
		List<TbContent> list = tbContentMapper.selectByExample(contentExample);
		// 5.封装到pageInfo插件
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		// 6.创建包装对象
		EasyUIDataGridResult dataGridResults = new EasyUIDataGridResult();
		// 7.封装到包装类
		dataGridResults.setTotal((int) pageInfo.getTotal());// 设置总记录数
		dataGridResults.setRows(list);// 设置记录
		// 8.返回
		return dataGridResults;
	}

	@Override
	public TaoShopResult saveContent(TbContent content) {
		// 1.注入mapper
		// 2.补全对象属性
		content.setCreated(new Date());
		content.setUpdated(new Date());
		// 3.执行增加操作
		tbContentMapper.insertSelective(content);
		// 同步redis缓存
		jedis.del(CONTENT_KEY, content.getCategoryId().toString());
		// 4.返回
		return TaoShopResult.ok();
	}

	@Override
	public TaoShopResult updateContent(TbContent content) {
		// 1.注入服务
		// 2.补全对象
		content.setUpdated(new Date());
		// 3.修改表
		tbContentMapper.updateByPrimaryKey(content);
		// 同步redis缓存
		jedis.del(CONTENT_KEY, content.getCategoryId().toString());
		return TaoShopResult.ok();
	}

	@Override
	public TaoShopResult deleteContent(List<Long> idList) {
		// 1.注入mapper
		// 2.创建查询条件
		TbContentExample contentExample = new TbContentExample();
		Criteria criteria = contentExample.createCriteria();
		criteria.andIdIn(idList);
		// 3.删除数据
		tbContentMapper.deleteByExample(contentExample);
		// 4.返回
		return TaoShopResult.ok();
	}

}
