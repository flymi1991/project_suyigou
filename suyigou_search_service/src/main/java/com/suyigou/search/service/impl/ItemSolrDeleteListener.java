package com.suyigou.search.service.impl;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemSolrDeleteListener implements MessageListener {
    @Autowired
    private ItemServiceSearchImpl itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            if (message != null && TextMessage.class.isInstance(message)) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                List array = JSON.parseArray(text);
                System.out.println("删除的goods有" + array.toArray());
                itemSearchService.deleteByGoodsIds(array);
                System.out.println("删除成功");
            }
        } catch (JMSException e) {
            e.printStackTrace();
            System.out.println("删除错误");
        }
    }
}
