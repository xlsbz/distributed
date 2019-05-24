package com.dhr.shop.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.dhr.shop.search.service.SearchItemService;

/**
 * @author Mr DU 一旦商品增加 会发送消息 在这里就可以监听接收到 从而调用service查询商品是否添加成功 再导入到索引库
 */
public class ItemChangeListener implements MessageListener {

	@Autowired
	private SearchItemService searchService;

	/**
	 * @param message
	 */
	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			try {
				long itemId = Long.parseLong(textMessage.getText());
				// 调用service
				searchService.updateItemById(itemId);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
