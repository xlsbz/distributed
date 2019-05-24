package com.util;

import java.io.Serializable;

/**
 * @author ali 返回常用接口
 */
public class JDResult implements Serializable {

	private int status = 200;// 状态吗
	private String msg;// 消息
	private Object object;// 对象

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * @param status
	 * @param msg
	 * @param object
	 */
	public JDResult(int status, String msg, Object object) {
		super();
		this.status = 200;
		this.msg = msg;
		this.object = object;
	}

	/**
	 * 
	 */
	public JDResult() {
		super();
		this.status = 200;
		this.object = null;
	}

	/**
	 * @param status
	 * @param msg
	 */
	public JDResult(int status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	/**
	 * @param msg
	 */
	public JDResult(String msg) {
		super();
		this.msg = msg;
	}

	public static JDResult ok() {
		return new JDResult();
	}
}
