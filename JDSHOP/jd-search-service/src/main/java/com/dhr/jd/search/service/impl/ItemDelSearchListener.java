package com.dhr.jd.search.service.impl;

import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.dhr.jd.search.service.ImportAllItemService;

/**
 * @author ali 监听导库操作
 */
public class ItemDelSearchListener implements MessageListener {

	@Autowired
	private ImportAllItemService importAllItemService;

	/**
	 * @param message
	 */
	@Override
	public void onMessage(Message message) {
		// 1.获得到监听的消息
		if (message instanceof ObjectMessage) {
			ObjectMessage msg = (ObjectMessage) message;
			try {
				Long[] ids = (Long[]) msg.getObject();
				// 调用服务执行删除索引
				importAllItemService.deleteSolrItem(Arrays.asList(ids));
				System.out.println("删除索引库成功");
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
