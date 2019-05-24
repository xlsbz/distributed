package com.dhr.jd.user.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dhr.jd.user.service.UserService;
import com.jd.pojo.TbUser;
import com.util.PageResult;
import com.util.PhoneFormatCheckUtils;
import com.util.SendSms;

import entity.Result;

/**
 * controller
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll() {
		return userService.findAll();
	}

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows) {
		return userService.findPage(page, rows);
	}

	@Autowired
	private RedisTemplate<String, ?> template;

	/**
	 * 增加
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user, String code) {
		try {
			// 判断验证码是否正确
			String phoneCode = (String) template.boundHashOps("JDCODE").get(user.getPhone());
			if (!code.equals(phoneCode)) {
				return new Result(false, "验证码不正确!");
			}
			// 补全对象属性
			user.setCreated(new Date());
			user.setUpdated(new Date());
			// 加密
			String md5Hex = DigestUtils.md5Hex(user.getPassword());
			user.setPassword(md5Hex);
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		} finally {
			template.boundHashOps("JDCODE").delete(user.getPhone());
		}
	}

	private String SignName = "校云";
	@Value("${TemplateCode}")
	private String TemplateCode;

	/**
	 * 发送短信验证码
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/sendSms")
	public Result sendSms(String phone) {
		Map<String, String> map = new HashMap<String, String>();
		// 判断手机号是否正确
		boolean b = PhoneFormatCheckUtils.isChinaPhoneLegal(phone);
		if (!b) {
			return new Result(false, "手机号格式不正确");
		}
		map.put("phone", phone);
		map.put("signName", SignName);
		map.put("templateCode", TemplateCode);
		// 生成随机码
		String code = (long) (Math.random() * 1000000) + "";
		map.put("code", code);
		System.out.println(code);
		// 把验证码放到缓存
		template.boundHashOps("JDCODE").put(phone, code);

		// 发送短信
		SendSms.sendSms(map);

		return new Result(true, "发送成功!");
	}

	/**
	 * 修改
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user) {
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id) {
		return userService.findOne(id);
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		try {
			userService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 查询+分页
	 * 
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows) {
		return userService.findPage(user, page, rows);
	}

}
