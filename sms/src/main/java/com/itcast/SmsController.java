package com.itcast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
public class SmsController {
    @Autowired
    private JmsMessagingTemplate template;
    @RequestMapping("/reg")
    public String info(String msg) {
        try {
            Map<String, String> map = new HashMap<String,String>();
            map.put("mobile", "18323108686");
            map.put("templateCode", "SMS_147201202");
            map.put("signName", "品优购");
            map.put("param", "{\"code\":\"654321\"}");
            template.convertAndSend("reg", map);
            return "发送成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "发送失败";
        }
    }
}
