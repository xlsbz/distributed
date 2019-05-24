/**
 * 
 */
package com.dhr.shop.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhr.shop.content.service.ContentCategoryService;
import com.dhr.shop.dao.TbContentCategoryMapper;
import com.dhr.shop.pojo.TbContentCategory;
import com.dhr.shop.pojo.TbContentCategoryExample;
import com.dhr.shop.pojo.TbContentCategoryExample.Criteria;
import com.dhr.shop.utils.EasyUITreeNode;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @ClassName: ContentCategoryServiceImpl
 * @Description: TODO(内容业务层)
 * @author Mr DU
 * @date 2019年4月1日
 * 
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	// 注入mapper
	@Autowired
	private TbContentCategoryMapper tbCategoryMapper;

	@Override
	public List<EasyUITreeNode> getContentCategory(long parentId) {
		// 1.注入mapper
		// 2.创建查询条件
		TbContentCategoryExample categoryExample = new TbContentCategoryExample();
		Criteria criteria = categoryExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		// 3.查询
		List<TbContentCategory> selectByExample = tbCategoryMapper.selectByExample(categoryExample);
		// 4.封装到EasyUITreeNode结果集
		EasyUITreeNode easyUITreeNode = null;
		List<EasyUITreeNode> easyUITreeNodes = new ArrayList<>();
		for (TbContentCategory cate : selectByExample) {
			easyUITreeNode = new EasyUITreeNode();
			easyUITreeNode.setId(cate.getId());
			easyUITreeNode.setText(cate.getName());
			easyUITreeNode.setState(cate.getIsParent() ? "closed" : "open");
			easyUITreeNodes.add(easyUITreeNode);
		}
		// 5.返回
		return easyUITreeNodes;
	}

	@Override
	public TaoShopResult insertCategory(long parentId, String name) {
		// 1.注入mapper
		// 2.创建分类对象
		TbContentCategory category = new TbContentCategory();
		// 3.补全对象属性
		category.setName(name);
		category.setIsParent(false);
		category.setParentId(parentId);
		category.setSortOrder(1);// 同级类目展现次序 排序
		category.setStatus(1);// 1 正常 2 删除
		category.setUpdated(new Date());
		category.setCreated(new Date());
		// 4.执行插入表操作
		tbCategoryMapper.insertSelective(category);
		// 5.判断点击的是不是父节点，不是需要将isparent改为true
		TbContentCategory selectByPrimaryKey = tbCategoryMapper.selectByPrimaryKey(parentId);
		if (!selectByPrimaryKey.getIsParent()) {
			selectByPrimaryKey.setIsParent(true);
			// 6.更新表
			tbCategoryMapper.updateByPrimaryKey(selectByPrimaryKey);
		}
		// 7.返回
		return TaoShopResult.ok(category);
	}

	@Override
	public TaoShopResult updateCategory(long id, String name) {
		// 1.注入mapper
		// 2.查询该分类
		TbContentCategory selectByPrimaryKey = tbCategoryMapper.selectByPrimaryKey(id);
		// 3.修改分类
		selectByPrimaryKey.setName(name);
		selectByPrimaryKey.setUpdated(new Date());
		// 4.更新表
		tbCategoryMapper.updateByPrimaryKeySelective(selectByPrimaryKey);
		// 5.返回
		return TaoShopResult.ok();
	}

	@SuppressWarnings("unused")
	@Override
	public TaoShopResult deleteCategory(long id) {
		// 1.注入mapper
		// 2.根据id查询分类
		TbContentCategory selectByPrimaryKey = tbCategoryMapper.selectByPrimaryKey(id);
		// 3.判断分类是否为父节点 是的话，不允许删除
		if (selectByPrimaryKey.getIsParent()) {
			System.out.println("这是父节点");
			return TaoShopResult.build(500, "该节点是还有子节点，不允许删除！");
		}
		// 删除节点
		tbCategoryMapper.deleteByPrimaryKey(id);
		// 4.如果删除这个节点之后，父节点下面没有子节点了，将这个父节点的isParent改为false
		Long parentId = selectByPrimaryKey.getParentId();
		// 创建查询条件 查询所有分类
		TbContentCategoryExample categoryExample = new TbContentCategoryExample();
		Criteria criteria = categoryExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = tbCategoryMapper.selectByExample(categoryExample);
		// 查询有没有这个父节点的儿子
		if (list.size() == 0 || list == null) {
			TbContentCategory parentCate = tbCategoryMapper.selectByPrimaryKey(parentId);
			if (parentCate.getIsParent()) {
				parentCate.setIsParent(false);
				parentCate.setUpdated(new Date());
			}
			tbCategoryMapper.updateByPrimaryKeySelective(parentCate);
		}
		return TaoShopResult.ok();
	}

}
