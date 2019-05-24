package com.dhr.shop.order.pojo;

import java.io.Serializable;
import java.util.List;

import com.dhr.shop.pojo.TbOrder;
import com.dhr.shop.pojo.TbOrderItem;
import com.dhr.shop.pojo.TbOrderShipping;

/**
 * @author Mr DU 封装order 订单明细 配送方式
 */
public class OrderInfo extends TbOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	// 订单明细
	private List<TbOrderItem> orderItems;

	// 配送信息
	private TbOrderShipping orderShipping;

	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}

	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}

}
