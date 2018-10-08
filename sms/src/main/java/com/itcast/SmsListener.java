package com.itcast;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {
    @Autowired
    private SmsUtils smsUtil;

    @JmsListener(destination = "reg")
    public void readMsg(Map<String, String> map) {
        try {
            SendSmsResponse response = smsUtil.sendSms(map.get("mobile"), map.get("templateCode"), map.get("signName"), map.get("param"));
            System.out.println("短信发送成功");
            System.out.println("response.code = " + response.getCode());
            System.out.println("response.getMessage() = " + response.getMessage());
            System.out.println("response.getBizId() = " + response.getBizId());
            System.out.println("response.getRequestId() = " + response.getRequestId());
        } catch (ClientException e) {
            System.out.println("短信发送失败");
            e.printStackTrace();
        }
    }
}
