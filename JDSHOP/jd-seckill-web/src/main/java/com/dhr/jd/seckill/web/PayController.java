package com.dhr.jd.seckill.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dhr.jd.pay.service.WeiXinPayService;
import com.dhr.jd.seckill.service.SeckillOrderService;
import com.jd.pojo.TbSeckillOrder;
import com.util.IdWorker;

import entity.Result;

/**
 * @author ali 支付控制
 */
@RestController
@RequestMapping("/paySeckill")
public class PayController {

	@Reference
	private WeiXinPayService weiXinPayService;

	@Autowired
	private IdWorker idWorker;

	@Reference
	private SeckillOrderService seckillOrderService;

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
		TbSeckillOrder seckillOrder = seckillOrderService.findPayLogByRedis(userId);
		// 判断支付日志存在
		if (seckillOrder != null) {
			return weiXinPayService.createNative(seckillOrder.getId() + "", seckillOrder.getMoney() + "");
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
	public Result paySuccess(Long id) {
		seckillOrderService.updateSeckillOrderStatus(id);
		return new Result(true, "支付成功!");
	}

}
