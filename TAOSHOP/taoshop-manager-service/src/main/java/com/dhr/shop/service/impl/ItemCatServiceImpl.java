package com.dhr.shop.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhr.shop.dao.TbItemCatMapper;
import com.dhr.shop.pojo.TbItemCat;
import com.dhr.shop.pojo.TbItemCatExample;
import com.dhr.shop.pojo.TbItemCatExample.Criteria;
import com.dhr.shop.service.ItemCatService;
import com.dhr.shop.utils.EasyUITreeNode;

/**
 * @ClassName: ItemCatServiceImpl
 * @Description: TODO(商品类别实现)
 * @author Mr DU
 * @date 2019年3月29日
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatmapper;

	@Override
	public List<EasyUITreeNode> getItemCatList(Long parentId) {
		// 1.注入mapper
		// 2.根据点击的节点查询出节点列表
		// 2.1创建example
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = itemCatmapper.selectByExample(example);
		// 3.遍历查询的结果封装到包装类对象
		// 3.1创建包装类对象集合
		List<EasyUITreeNode> easyUITreeNodes = new ArrayList<>();
		for (TbItemCat tbItemCat : list) {
			EasyUITreeNode easyUITreeNode = new EasyUITreeNode();
			easyUITreeNode.setId(tbItemCat.getId());
			easyUITreeNode.setText(tbItemCat.getName());
			easyUITreeNode.setState(tbItemCat.getIsParent() ? "closed" : "open");
			// 把单个包装对象放到包装集合
			easyUITreeNodes.add(easyUITreeNode);
		}
		// 4.返回
		return easyUITreeNodes;
	}

}
