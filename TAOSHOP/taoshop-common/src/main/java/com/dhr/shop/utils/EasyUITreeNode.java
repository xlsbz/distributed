/**
 * 
 */
package com.dhr.shop.utils;

import java.io.Serializable;

/**
 * @ClassName: EasyUITreeNode
 * @Description: TODO(包装类-->存放节点ID,节点名称,节点状态--->是否有子节点)
 * @author Mr DU
 * @date 2019年3月29日
 * 
 */
public class EasyUITreeNode implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String text;
	private String state;// 如果点击还有子节点-->则设为close 如果没有子节点--->设置为open

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
