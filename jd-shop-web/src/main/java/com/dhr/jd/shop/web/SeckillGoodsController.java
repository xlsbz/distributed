package com.dhr.jd.shop.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dhr.jd.seckill.service.SeckillGoodsService;
import com.jd.pojo.TbSeckillGoods;
import com.util.PageResult;

import entity.Result;

/**
 * controller
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

	@Reference
	private SeckillGoodsService seckillGoodsService;

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillGoods> findAll() {
		return seckillGoodsService.findAll();
	}

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows) {
		return seckillGoodsService.findPage(page, rows);
	}

	/**
	 * 处理页面传递的日期
	 * 
	 * @param request
	 * @param binder
	 */
	@InitBinder
	private void initbinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, false));
	}

	/**
	 * 增加
	 * 
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeckillGoods seckillGoods) {
		try {
			// 获取当前登陆商家
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			seckillGoods.setSellerId(name);
			seckillGoodsService.add(seckillGoods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 * 
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeckillGoods seckillGoods) {
		try {
			seckillGoodsService.update(seckillGoods);
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
	public TbSeckillGoods findOne(Long id) {
		return seckillGoodsService.findOne(id);
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
			seckillGoodsService.delete(ids);
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
	public PageResult search(@RequestBody TbSeckillGoods seckillGoods, int page, int rows) {
		// 只查询自己的
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		seckillGoods.setSellerId(name);
		return seckillGoodsService.findPage(seckillGoods, page, rows);
	}

}
