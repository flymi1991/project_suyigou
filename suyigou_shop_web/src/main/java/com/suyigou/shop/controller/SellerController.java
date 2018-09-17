package com.suyigou.shop.controller;


import com.suyigou.pojo.TbSeller;
import com.suyigou.sellergoods.service.SellerService;
import entity.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Autowired
    private SellerService sellerService;

    //添加注册用户
    @RequestMapping("/add")
    public ResultInfo addSeller(TbSeller seller) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        seller.setPassword(passwordEncoder.encode(seller.getPassword()));
        try {
            sellerService.add(seller);
            return new ResultInfo(true, "申请成功");
        } catch (Exception e) {
            return new ResultInfo(false, "shop申请失败");
        }
    }

    @RequestMapping("/name")
    public Map<String, String> showName() {
        HashMap<String, String> map = new HashMap<>();
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName", loginName);
        return map;
    }
}
