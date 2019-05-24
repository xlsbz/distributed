package com.dhr.shop.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhr.shop.dao.TbUserMapper;
import com.dhr.shop.pojo.TbUser;
import com.dhr.shop.pojo.TbUserExample;
import com.dhr.shop.pojo.TbUserExample.Criteria;
import com.dhr.shop.sso.jedis.impl.JedisClient;
import com.dhr.shop.sso.service.UserService;
import com.dhr.shop.utils.JsonUtils;
import com.dhr.shop.utils.TaoShopResult;

/**
 * @author Mr DU
 *
 */
@Service
public class UserServiceImpl implements UserService {

	// 注入redis客户端
	@Autowired
	private JedisClient jedisClient;

	@Autowired
	private TbUserMapper tbUsermapper;

	/**
	 * 检查用户登录名是否可以
	 * 
	 * @param params2.手机/1.登录名/3.邮箱
	 * @param type登录类型
	 * @return
	 */
	@Override
	public TaoShopResult userCheck(String params, Integer type) {
		// 1.注入mapper
		// 2.创建查询条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		// 3.判断用户登录类型
		if (type == 1) {
			// 用户名
			criteria.andUsernameEqualTo(params);
		} else if (type == 2) {
			// 手机
			criteria.andPhoneEqualTo(params);
		} else if (type == 3) {
			// 邮箱
			criteria.andEmailEqualTo(params);
		} else {
			TaoShopResult.build(400, "请求参数类型错误!");
		}
		// 4.调用dao查询数据
		List<TbUser> users = tbUsermapper.selectByExample(example);
		if (users != null && users.size() > 0) {
			return TaoShopResult.ok(false);

		}
		// 5.返回
		return TaoShopResult.ok(true);
	}

	/**
	 * 注册用户
	 * 
	 * @param user
	 * @return
	 */
	@Override
	public TaoShopResult registerUser(TbUser user) {
		// 1.检查用户名密码是否为空
		if (!StringUtils.isNotBlank(user.getUsername())) {
			return TaoShopResult.build(400, "用户名不能为空");
		}
		if (!StringUtils.isNotBlank(user.getPassword())) {
			return TaoShopResult.build(400, "密码不能为空!");
		}
		// 2.校验数据合法性
		TaoShopResult result = userCheck(user.getUsername(), 1);
		if (!(boolean) result.getData()) {
			return TaoShopResult.build(400, "用户名已被占用！！！");
		}
		if (StringUtils.isNotBlank(user.getPhone())) {
			TaoShopResult result1 = userCheck(user.getPhone(), 2);
			if (!(boolean) result1.getData()) {
				return TaoShopResult.build(400, "电话号码已被占用！！！");
			}
		}
		if (StringUtils.isNotBlank(user.getEmail())) {
			TaoShopResult result2 = userCheck(user.getEmail(), 3);
			if (!(boolean) result2.getData()) {
				return TaoShopResult.build(400, "邮箱已被占用！！！");
			}
		}
		// 3.用户名密码加密
		String password = user.getPassword();
		String mdPasswd = DigestUtils.md5Hex(password.getBytes());
		user.setPassword(mdPasswd);
		// 4.不全其他属性
		user.setCreated(new Date());
		user.setUpdated(new Date());
		// 5.插入数据
		tbUsermapper.insertSelective(user);
		return TaoShopResult.ok();
	}

	/**
	 * 用户登录
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public TaoShopResult loginUser(String username, String password) {
		// 1.根据用户名查看用户是否存在
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = tbUsermapper.selectByExample(example);
		TbUser user = null;
		if (list != null && list.size() > 0) {
			user = list.get(0);
		} else {
			return TaoShopResult.build(400, "用户不存在！");
		}
		// 2.如果查到用户则判断密码是否正确
		String md5Hex = DigestUtils.md5Hex(password.getBytes());
		if (!(user.getPassword().equals(md5Hex))) {
			return TaoShopResult.build(400, "用户密码错误!");
		}
		// 登录成功返回token 相当于sessionId
		String token = UUID.randomUUID().toString().replace("-", "");
		// 3.将用户信息设置到redis
		user.setPassword(null);
		jedisClient.set("USER_INFO" + ":" + token, JsonUtils.objectToJson(user));
		// 4.设置过期时间
		jedisClient.expire("USER_INFO" + ":" + token, 1800);
		// 5.返回
		return TaoShopResult.ok(token);
	}

	/**
	 * 查询用户是否登录
	 * 
	 * @param token
	 * @return
	 */
	@Override
	public TaoShopResult getUserByToken(String token) {
		// 1.根据token查询redis是否存在这个key
		String json = jedisClient.get("USER_INFO" + ":" + token);
		TbUser user = null;
		if (StringUtils.isNotBlank(json)) {
			// 3.查到了，重新设置过期时间返回user
			jedisClient.expire("USER_INFO" + ":" + token, 1800);
			user = JsonUtils.jsonToPojo(json, TbUser.class);
		} else {
			// 2.查不到，用户已过期
			return TaoShopResult.build(400, "用户已过期，请重新登录！");
		}

		return TaoShopResult.ok(user);
	}

	/**
	 * @param token
	 * @return
	 */
	@Override
	public TaoShopResult quitUserByToken(String token) {
		// 根据token删除key
		jedisClient.expire("USER_INFO" + ":" + token, 0);
		return TaoShopResult.ok();
	}

}
