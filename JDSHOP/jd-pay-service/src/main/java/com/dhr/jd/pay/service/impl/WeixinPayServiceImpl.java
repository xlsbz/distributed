package com.dhr.jd.pay.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Service;
import com.dhr.jd.pay.service.WeiXinPayService;

@Service
public class WeixinPayServiceImpl implements WeiXinPayService {

	/**
	 * 生成二维码
	 * 
	 * @return
	 */
	@Override
	public Map createNative(String out_trade_no, String total_fee) {

		Map<String, String> map = new HashMap<>();
		map.put("code_url", "http://www.91xiaoyoubang.com");// 支付地址
		map.put("total_fee", total_fee);// 总金额
		map.put("out_trade_no", out_trade_no);// 订单号
		return map;
	}
}
