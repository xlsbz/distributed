package com.dhr.jd.pay.service;

import java.util.Map;

/**
 * @author ali 微信支付接口
 */
public interface WeiXinPayService {

	/**
	 * 生成微信支付二维码
	 * 
	 * @param out_trade_no
	 * @param total_fee
	 * @return
	 */
	Map createNative(String out_trade_no, String total_fee);
}
