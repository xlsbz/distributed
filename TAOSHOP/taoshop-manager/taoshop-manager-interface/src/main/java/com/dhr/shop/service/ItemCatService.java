package com.dhr.shop.service;

import java.util.List;

import com.dhr.shop.utils.EasyUITreeNode;

/**
 * @ClassName: ItemCatService
 * @Description: TODO(商品类别服务)
 * @author Mr DU
 * @date 2019年3月29日
 *
 */
public interface ItemCatService {
	List<EasyUITreeNode> getItemCatList(Long parentId);
}
