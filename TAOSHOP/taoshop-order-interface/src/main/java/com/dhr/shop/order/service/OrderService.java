package com.dhr.shop.order.service;

import com.dhr.shop.order.pojo.OrderInfo;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU 创建订单
 */
public interface OrderService {
	// 创建订单
	TaoShopResult creatOrder(OrderInfo orderInfo);

}
