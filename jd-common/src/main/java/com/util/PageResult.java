package com.util;

import java.io.Serializable;
import java.util.List;

/**
 * @author ali 封装分页参数
 */
public class PageResult implements Serializable {

	private int total;// 总记录数
	private List rows;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

	/**
	 * @param total
	 * @param list
	 */
	public PageResult(int total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}

}
