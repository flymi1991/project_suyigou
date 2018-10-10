package com.suyigou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suyigou.pojo.TbUser;
import com.suyigou.user.service.UserService;
import entity.ResultInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.PhoneFormatCheckUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;


    /**
     * 增加
     *
     * @param user
     * @return
     */
    @RequestMapping("/add")
    public ResultInfo add(@RequestBody TbUser user, String smscode) {
        boolean flag = userService.checkCode(user.getPhone(), smscode);
        if (!flag) {
            return new ResultInfo(false, "验证码错误");
        }
        try {
            user.setCreated(new Date());
            user.setUpdated(new Date());
            String psd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
            user.setPassword(psd);
            userService.add(user);
            return new ResultInfo(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param user
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return new ResultInfo(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public ResultInfo delete(Long[] ids) {
        try {
            userService.delete(ids);
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    @RequestMapping("/sendCode")
    public ResultInfo sendCode(String phone) {
        boolean flag = PhoneFormatCheckUtils.isPhoneLegal(phone);
        if (!flag) {
            return new ResultInfo(false, "手机号格式错误");
        }
        try {
            userService.createSmsCode(phone);
            return new ResultInfo(true, "验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "验证码发送失败");
        }
    }

    @RequestMapping("/showName")
    public Map showName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("name = " + name);
        Map map=new HashMap<>();
        map.put("loginName", name);
        return map;
    }
}
