package com.dhr.jd.cart.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dhr.jd.order.service.OrderService;
import com.dhr.jd.pay.service.WeiXinPayService;
import com.jd.pojo.TbPayLog;
import com.util.IdWorker;

import entity.Result;

/**
 * @author ali 支付控制
 */
@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference
	private WeiXinPayService weiXinPayService;

	@Autowired
	private IdWorker idWorker;

	@Reference
	private OrderService orderService;

	/**
	 * 生成订单
	 * 
	 * @return
	 */
	@RequestMapping("/createNative")
	public Map createNative() {
		// 获取当前用户
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		// 到redis查询支付日志
		TbPayLog payLog = orderService.findPayLogByRedis(userId);
		// 判断支付日志存在
		if (payLog != null) {
			return weiXinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
		} else {
			return new HashMap();
		}

	}

	/**
	 * 模拟支付成功
	 * 
	 * @return
	 */
	@RequestMapping("/paySuccess")
	public Result paySuccess(String out_trade_no) {
		orderService.updateOrderStatus(out_trade_no, "1");
		return new Result(true, "支付成功!");
	}

	/**
	 * 模拟支付失败
	 * 
	 * @return
	 */
	@RequestMapping("/payError")
	public Result payError(String out_trade_no) {
		orderService.updateOrderStatus(out_trade_no, "0");
		return new Result(true, "支付失败!");
	}
}
