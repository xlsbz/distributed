package com.dhr.shop.sso.service;

import com.dhr.shop.pojo.TbUser;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU 用户接口
 */
public interface UserService {
	/**
	 * 检查用户登录名是否可以
	 * 
	 * @param params手机/登录名/邮箱
	 * @param type登录类型
	 * @return
	 */
	TaoShopResult userCheck(String params, Integer type);

	/**
	 * 注册用户
	 * 
	 * @param user
	 * @return
	 */
	TaoShopResult registerUser(TbUser user);

	/**
	 * 用户登录
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	TaoShopResult loginUser(String username, String password);

	/**
	 * 根据token查询用户是否登录
	 * 
	 * @param token
	 * @return
	 */
	TaoShopResult getUserByToken(String token);

	/**
	 * 退出登录
	 * 
	 * @param token
	 * @return
	 */
	TaoShopResult quitUserByToken(String token);
}
