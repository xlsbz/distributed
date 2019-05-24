package com.pojo.group;

import java.io.Serializable;
import java.util.List;

import com.jd.pojo.TbOrderItem;

/**
 * @author ali 购物车组合实体
 */
public class Cart implements Serializable {

	private String sellerId;// 商家ID
	private String sellerName;// 商家名称
	private List<TbOrderItem> orderItemList;// 具体的商品项

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public List<TbOrderItem> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(List<TbOrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}

}
