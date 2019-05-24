package com.dhr.jd.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.search.service.SearchService;
import com.jd.pojo.TbItem;

/**
 * @author ali
 *
 */
@Service(timeout = 3000)
public class SearchServiceImpl implements SearchService {

	// 注入模板
	@Autowired
	private SolrTemplate solrTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * @return
	 */
	@Override
	public Map<String, Object> search(Map<String, Object> searchMap) {
		Map<String, Object> map = new HashMap<>();
		// 把查出来的map追加到这个空map里
		map.putAll(result(searchMap));
		// 查询所有分类
		List list = searchCategory(searchMap);
		map.put("categoryList", list);
		// 查询出所有分类的品牌列表和规格
		// 如果有分类名称按名称查没有就按所有关联分类的第一个查询
		if (searchMap.get("category") != null && !"".equals(searchMap.get("category"))) {
			map.putAll(searchBrandAndSpec((String) searchMap.get("category")));
		} else if (list.size() > 0) {
			map.putAll(searchBrandAndSpec(list.get(0).toString()));
		}
		return map;
	}

	/**
	 * 构建搜索结果高亮
	 * 
	 * @return
	 */
	private Map<String, Object> result(Map<String, Object> searchMap) {
		// Query query = new SimpleQuery("*:*");
		HighlightQuery query = new SimpleHighlightQuery();
		// 1.创建solr搜索条件
		Criteria criteria = new Criteria();
		// 去除关键字的空格
		String keywords = "";
		keywords = (String) searchMap.get("keywords");
		if (keywords != null && !"".equals(keywords)) {
			if (keywords.startsWith("请输入您要的")) {
				criteria = new Criteria("item_keywords").isNotNull();
				query.addCriteria(criteria);
			} else {
				criteria = new Criteria("item_keywords").is(keywords.replace(" ", ""));
				query.addCriteria(criteria);
			}
		} else {
			criteria = new Criteria("item_keywords").isNotNull();
			query.addCriteria(criteria);
		}

		// 分类过滤
		if (searchMap.get("category") != null && !"".equals(searchMap.get("category"))) {
			// 添加条件
			criteria = criteria.and("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleQuery(criteria);
			query.addFilterQuery(filterQuery);
		}

		// 品牌过滤
		if (searchMap.get("brand") != null && !"".equals(searchMap.get("brand"))) {
			// 添加过滤条件
			criteria = criteria.and("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleQuery(criteria);
			query.addFilterQuery(filterQuery);
		}

		// 规格过滤
		if (searchMap.get("spec") != null) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			if (specMap != null) {
				for (Entry<String, String> spec : specMap.entrySet()) {
					criteria = criteria.and("item_spec_" + spec.getKey()).is(spec.getValue());
					FilterQuery filterQuery = new SimpleQuery(criteria);
					query.addFilterQuery(filterQuery);
				}
			}

		}

		// 价格过滤
		if (searchMap.get("price") != null && !"".equals(searchMap.get("price"))) {
			String s = (String) searchMap.get("price");
			String[] price = s.split("-");
			// 如果下限不是0 就设置下线
			if (!"0".equals(price[0])) {
				Criteria c = new Criteria("item_price").greaterThanEqual(price[0]);
				FilterQuery filterQuery = new SimpleQuery(c);
				query.addFilterQuery(filterQuery);
			}
			// 如果上线不是* 就设置上线
			if (!"*".equals(price[1])) {
				Criteria c = new Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery = new SimpleQuery(c);
				query.addFilterQuery(filterQuery);
			}
		}

		// 排序处理 得到排序顺序和排序字段
		String sort = (String) searchMap.get("sort");
		String sortField = (String) searchMap.get("sortField");
		if (!("".equals(sort) || "".equals(sortField))) {
			if (sort.equalsIgnoreCase("asc")) {
				Sort s = new Sort(Sort.Direction.ASC, "item_" + sortField);
				query.addSort(s);
			}
			if (sort.equalsIgnoreCase("desc")) {
				Sort s = new Sort(Sort.Direction.DESC, "item_" + sortField);
				query.addSort(s);
			}
		}

		// 构建分页
		Integer pageNumber = (Integer) (searchMap.get("pageNumber"));
		Integer pageSize = (Integer) (searchMap.get("pageSize"));
		if (searchMap.get("pageNumber") == null) {
			pageNumber = 1;
		}
		if (searchMap.get("pageSize") == null) {
			pageSize = 30;
		}
		// 得到起始地址
		int startPage = (pageNumber - 1) * pageSize;
		query.setOffset(startPage);
		query.setRows(pageSize);

		// 高亮显示设置
		HighlightOptions options = new HighlightOptions();
		options.addField("item_title");
		options.setSimplePrefix("<em style='color:red'>");
		options.setSimplePostfix("</em>");

		// 设置高亮显示
		query.setHighlightOptions(options);

		// 2.搜索
		// ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query,
		// TbItem.class);
		HighlightPage<TbItem> queryForHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
		// 循环高亮
		for (HighlightEntry<TbItem> h : queryForHighlightPage.getHighlighted()) {
			TbItem item = h.getEntity();// 获得实体
			if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
				// 设置该字段值高亮
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
			}
		}
		// 3.封装数据
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 把记录带回去
		resultMap.put("rows", queryForHighlightPage.getContent());
		resultMap.put("totalCount", queryForHighlightPage.getTotalElements());
		resultMap.put("totalPages", queryForHighlightPage.getTotalPages());
		return resultMap;
	}

	/**
	 * 搜索商品分类列表
	 * 
	 * @param searchMap
	 * @return
	 */
	private List searchCategory(Map searchMap) {
		// 创建集合保存分类
		List categoryList = new ArrayList();
		// 1.创建查询条件
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		// 2.查询
		GroupOptions groupOptions = new GroupOptions();
		groupOptions.addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 3.调用模板得到分组页
		// 查询结果集得到分组页
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
		// 得到分组结果入口页
		GroupResult<TbItem> result = groupPage.getGroupResult("item_category");
		// 得到实体结果集
		Page<GroupEntry<TbItem>> page = result.getGroupEntries();
		// 得到集合
		List<GroupEntry<TbItem>> content = page.getContent();
		for (GroupEntry<TbItem> groupEntry : content) {
			// 取出分类属性值
			categoryList.add(groupEntry.getGroupValue());
		}
		// System.out.println(categoryList);
		return categoryList;

	}

	/**
	 * 通过分类ID查询出模板绑定的品牌和规格
	 * 
	 * @param categoryId
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private Map searchBrandAndSpec(String category) {
		Map map = new HashMap<>();
		if (category != null && !"".equals(category)) {
			Long typeId = (Long) redisTemplate.boundHashOps("JD_CATEGORY").get(category);
			if (typeId != null) {
				// 查询出品牌列表
				List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
				List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
				map.put("brandList", brandList);
				map.put("specList", specList);
			}
		}

		return map;
	}

}
