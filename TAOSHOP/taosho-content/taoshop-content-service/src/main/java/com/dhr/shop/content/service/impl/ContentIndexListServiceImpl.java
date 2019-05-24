package com.dhr.shop.content.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dhr.shop.content.jedis.impl.JedisClient;
import com.dhr.shop.content.service.ContentIndexListService;
import com.dhr.shop.dao.TbContentMapper;
import com.dhr.shop.pojo.TbContent;
import com.dhr.shop.pojo.TbContentExample;
import com.dhr.shop.pojo.TbContentExample.Criteria;
import com.dhr.shop.utils.JsonUtils;

@Service
public class ContentIndexListServiceImpl implements ContentIndexListService {

	@Autowired
	private TbContentMapper tbContentMapper;

	// 注入jedisCluster
	@Autowired
	private JedisClient jedisClient;

	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;

	@Override
	public List<TbContent> getContentIndexList(long cid) {
		// 1.注入mapper
		// 先查下jedis，如果有，就从redis返回
		try {
			String json = jedisClient.hget(CONTENT_KEY, cid + "");
			if (StringUtils.isNoneBlank(json)) {
				// 把json转换为list返回
				List<TbContent> toList = JsonUtils.jsonToList(json, TbContent.class);
				return toList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 2.创建查询条件
		TbContentExample contentExample = new TbContentExample();
		Criteria criteria = contentExample.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		// 3.查询
		List<TbContent> contentLists = tbContentMapper.selectByExample(contentExample);

		// 查询到了数据，放到redis
		try {
			jedisClient.hset(CONTENT_KEY, cid + "", JsonUtils.objectToJson(contentLists));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 4.返回
		return contentLists;
	}

}
