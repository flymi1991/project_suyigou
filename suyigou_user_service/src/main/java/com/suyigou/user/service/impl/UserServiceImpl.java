package com.suyigou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.suyigou.dao.TbUserMapper;
import com.suyigou.pojo.TbUser;
import com.suyigou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private RedisTemplate template;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination queueSmsSendDestination;

    @Value("SMS_147201202")
    private String templateCode;
    @Value("品优购")
    private String signName;

    /**
     * 查询全部
     */
    @Override
    public List<TbUser> findAll() {
        return userMapper.selectByExample(null);
    }


    /**
     * 增加
     */
    @Override
    public void add(TbUser user) {
        userMapper.insert(user);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbUser user) {
        userMapper.updateByPrimaryKey(user);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbUser findOne(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            userMapper.deleteByPrimaryKey(id);
        }
    }



    @Override
    public void createSmsCode(final String phone) {
        final String code = (long) (Math.floor(Math.random() * 1000000)) + "";
        System.out.println("验证码是" + code);
        //发送到 activeMQ
        jmsTemplate.send(queueSmsSendDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage message = session.createMapMessage();
                message.setString("mobile", phone);//手机号
                message.setString("templateCode", templateCode);//模板编号
                message.setString("signName", signName);//签名
                Map m=new HashMap<>();
                m.put("code", code);
                String codeMap = JSON.toJSONString(m);
                message.setString("param", codeMap);
                return message;
            }
        });
        template.boundHashOps("smsCode").put(phone, code);
    }

    @Override
    public boolean checkCode(String phone, String smscode) {
        String orgcode = (String) template.boundHashOps("smsCode").get(phone);
        return orgcode != null && smscode.equalsIgnoreCase(orgcode);
    }
}
