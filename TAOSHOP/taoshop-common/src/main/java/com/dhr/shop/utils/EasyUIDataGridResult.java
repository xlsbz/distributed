/**
 * 
 */
package com.dhr.shop.utils;

import java.io.Serializable;
import java.util.List;

/**  
 * @ClassName: EasyUIDataGridResult  
 * @Description: TODO(商品查询返回的数据类)  
 * @author Mr DU  
 * @date 2019年3月27日  
*    
*/
public class EasyUIDataGridResult implements Serializable{
	private static final long serialVersionUID = 1L;
	//记录数
	private Integer total;
	//数据
	private List rows;
	/**
	 * @return the total
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * @return the rows
	 */
	public List getRows() {
		return rows;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(List rows) {
		this.rows = rows;
	}
	
	

}
