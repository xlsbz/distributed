package com.dhr.jd.pages.service.impl;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.dhr.jd.pages.service.PageGenService;

/**
 * @author ali 监听页面生成
 */
public class GenPageListener implements MessageListener {

	@Autowired
	private PageGenService pageGenService;

	/**
	 * @param message
	 */
	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage msg = (TextMessage) message;
			try {
				String text = msg.getText();
				boolean b = pageGenService.genHtmlPages(Long.valueOf(text));
				System.out.println(b);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
