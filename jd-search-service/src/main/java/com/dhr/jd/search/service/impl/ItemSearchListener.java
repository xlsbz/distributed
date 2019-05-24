package com.dhr.jd.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.dhr.jd.search.service.ImportAllItemService;
import com.jd.pojo.TbItem;

/**
 * @author ali 监听导库操作
 */
public class ItemSearchListener implements MessageListener {

	@Autowired
	private ImportAllItemService importAllItemService;

	/**
	 * @param message
	 */
	@Override
	public void onMessage(Message message) {
		// 1.获得到监听的消息
		if (message instanceof TextMessage) {
			TextMessage msg = (TextMessage) message;
			try {
				String text = msg.getText();
				// 2.转换为json集合
				List<TbItem> list = JSON.parseArray(text, TbItem.class);
				// 3.遍历集合
				for (TbItem tbItem : list) {
					// 4.将动态域的spec转化
					Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
					tbItem.setSpecMap(map);
				}
				// 5.调用服务执行导入集合
				importAllItemService.updateSolrItem(list);
				System.out.println("新增索引库成功");
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
