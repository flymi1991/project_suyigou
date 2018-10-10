package com.suyigou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.suyigou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class ItemSolrAddListener implements MessageListener {

    @Autowired
    private ItemServiceSearchImpl itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("点模式监听器接收到消息");
        System.out.println("消息类型：" + message.getClass());
        if (message != null && TextMessage.class.isInstance(message)) {
            TextMessage textMessage = (TextMessage) message;
            try {
                String text = textMessage.getText();
                List<TbItem> itemList = JSON.parseArray(text,TbItem.class);
                for (TbItem item : itemList) {
                    String spec = item.getSpec();
                    Map specMap = JSON.parseObject(spec);
                    item.setSpecMap(specMap);
                }
                itemSearchService.importList(itemList);
            } catch (JMSException e) {
                e.printStackTrace();
                System.out.println("导入点消息失败");
            }
        }
    }
}
