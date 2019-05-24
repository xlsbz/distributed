package com.dhr.jd.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.dhr.jd.sellergoods.service.SellerService;
import com.jd.pojo.TbSeller;

/**
 * @author ali 认证类
 */
public class UserDetailServiceImpl implements UserDetailsService {

	private SellerService sellerService;

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	/**
	 * 完成用户认证
	 * 
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		// 调用服务查询密码
		TbSeller seller = sellerService.findOne(username);
		// 判断是否为空
		if (seller != null) {
			// 判断是否通过
			if (seller.getStatus().equals("1")) {
				return new User(username, seller.getPassword(), authorities);
			}
		}
		return null;
	}

}
