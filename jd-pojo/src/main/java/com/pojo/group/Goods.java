package com.pojo.group;

import java.io.Serializable;
import java.util.List;

import com.jd.pojo.TbGoods;
import com.jd.pojo.TbGoodsDesc;
import com.jd.pojo.TbItem;

/**
 * @author ali 组合实体 ：完成商品的组合录入
 */
public class Goods implements Serializable {

	private TbGoods goods;// 商品的spu 如：ihone7
	private TbGoodsDesc goodsDesc;// 商品的扩展属性
	private List<TbItem> itemList;// 商品的sku 如：iphoe7 /金色 32G iphone7 /银色 32G

	public TbGoods getGoods() {
		return goods;
	}

	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}

	public TbGoodsDesc getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(TbGoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public List<TbItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}

}
