package com.dhr.jd.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.dhr.jd.sellergoods.service.GoodsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.mapper.TbBrandMapper;
import com.jd.mapper.TbGoodsDescMapper;
import com.jd.mapper.TbGoodsMapper;
import com.jd.mapper.TbItemCatMapper;
import com.jd.mapper.TbItemMapper;
import com.jd.mapper.TbSellerMapper;
import com.jd.pojo.TbBrand;
import com.jd.pojo.TbGoods;
import com.jd.pojo.TbGoodsDesc;
import com.jd.pojo.TbGoodsExample;
import com.jd.pojo.TbGoodsExample.Criteria;
import com.jd.pojo.TbItem;
import com.jd.pojo.TbItemCat;
import com.jd.pojo.TbItemExample;
import com.jd.pojo.TbSeller;
import com.pojo.group.Goods;
import com.util.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		// 添加商品
		// 设置商品状态
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getGoods());
		// 设置sku数据
		setSkuItem(goods);
		// 获取goods的ID设置到goodsDesc
		Long id = goods.getGoods().getId();
		// 添加商品详情
		goods.getGoodsDesc().setGoodsId(id);
		goodsDescMapper.insert(goods.getGoodsDesc());
	}

	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods) {
		// 设置商品状态
		goods.getGoods().setAuditStatus("0");
		// 修改商品
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		// 修改商品详情
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		// 先删除原来的sku
		TbItemExample example = new TbItemExample();
		com.jd.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		// 设置sku数据
		setSkuItem(goods);
	}

	/**
	 * 设置sku数据
	 */
	private void setSkuItem(Goods goods) {
		// 保存sku
		if ("1".equals(goods.getGoods().getIsEnableSpec())) {
			// 启用了规格
			for (TbItem item : goods.getItemList()) {
				// 保存标题
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> object = JSONArray.parseObject(item.getSpec());
				Set<Entry<String, Object>> entrySet = object.entrySet();
				// 组合商品名
				for (Entry<String, Object> entry : entrySet) {
					title += " " + entry.getValue();
				}
				item.setTitle(title);
				// 设置公共属性
				setItem(item, goods);
				itemMapper.insert(item);
			}
		} else {
			// 没启用规格
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());// 商品KPU+规格描述串作为SKU名称
			item.setPrice(goods.getGoods().getPrice());// 价格
			item.setStatus("1");// 状态
			item.setIsDefault("1");// 是否默认
			item.setNum(99999);// 库存数量
			item.setSpec("{}");
			// 设置公共属性
			setItem(item, goods);
			itemMapper.insert(item);

		}
	}

	/**
	 * 设置共有属性
	 * 
	 * @param item
	 * @param goods
	 */
	private void setItem(TbItem item, Goods goods) {
		item.setGoodsId(goods.getGoods().getId());
		item.setSellerId(goods.getGoods().getSellerId());
		item.setCategoryid(goods.getGoods().getCategory3Id());
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		// 设置品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		// 设置分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		// 设置商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		// 去第一张图片设置
		List<Map> parseObject = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (parseObject.size() > 0) {
			item.setImage(parseObject.get(0).get("url").toString());
		}
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id) {
		Goods goods = new Goods();
		// 封装商品数据
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		// 封装详情数据
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(goodsDesc);
		// 封装sku数据
		TbItemExample example = new TbItemExample();
		com.jd.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> list = itemMapper.selectByExample(example);
		goods.setItemList(list);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			goodsMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		// 过滤已删除的
		if (!(goods.getSellerId() != null) && (!"".equals(goods.getSellerId()))) {
			criteria.andIsDeleteIsNull();
		}
		if (goods != null) {
			if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}
			if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}
			if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}
			if (goods.getCaption() != null && goods.getCaption().length() > 0) {
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}
			if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}
			if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
				criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
			}

		}

		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 审核商品
	 * 
	 * @param id
	 */
	@Override
	public void updateStatus(List<Long> id, String status) {
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(id);

		// 得到列表修改属性
		List<TbGoods> list = goodsMapper.selectByExample(example);
		for (TbGoods tbGoods : list) {
			tbGoods.setAuditStatus(status);
			// 如果有商家ID，则是在重新提交审核，需要清除删除字段
			tbGoods.setIsDelete(null);

			// 如果传来的是通过 1：设置商品默认上架
			tbGoods.setIsMarketable("1");
			// 保存回去
			goodsMapper.updateByPrimaryKey(tbGoods);
		}

	}

	/**
	 * @param id
	 */
	@Override
	public void deleteGoods(List<Long> id) {
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(id);

		// 得到列表修改删除属性
		List<TbGoods> list = goodsMapper.selectByExample(example);
		for (TbGoods tbGoods : list) {
			tbGoods.setIsDelete("1");
			tbGoods.setAuditStatus("3");
			// 保存回去
			goodsMapper.updateByPrimaryKey(tbGoods);
		}

	}

	/**
	 * @param id
	 * @param status
	 */
	@Override
	public void updateMarketable(List<Long> id, String status) {
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIn(id);

		// 得到列表修改删除属性
		List<TbGoods> list = goodsMapper.selectByExample(example);
		for (TbGoods tbGoods : list) {
			tbGoods.setIsMarketable(status);
			// 保存回去
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	/**
	 * @param id
	 * @param status
	 * @return
	 */
	@Override
	public List findAllIdAndStausItem(List<Long> ids, String status) {
		TbItemExample example = new TbItemExample();
		com.jd.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(ids);

		List<TbItem> list = itemMapper.selectByExample(example);
		return list;
	}

}
