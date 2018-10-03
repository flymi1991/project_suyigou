package com.suyigou.page.service.impl;

import com.alibaba.fastjson.JSON;
import com.suyigou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class TopicsPageAddListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        if (message != null && TextMessage.class.isInstance(message)) {
            TextMessage textMessage = (TextMessage) message;
            try {
                String text = textMessage.getText();
                List idsObj = JSON.parseArray(text);
                for (Object id : idsObj) {
                    itemPageService.genItemHtml((Long) id);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
