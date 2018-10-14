package com.suyigou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.suyigou.cart.service.CartService;
import com.suyigou.pojo.Cart;
import entity.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 6000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    //向购物车添加item
    @RequestMapping("/add")
    public ResultInfo addGoods2CartList(Long itemId, Integer num) {
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户为 = " + username);
        try {
            //从cookie或缓存中取出原有cartList
            List<Cart> cartList = findCartList();
            //调用cartService向cartList中添加item
            List<Cart> carts = cartService.addItem2CartList(cartList, itemId, num);
            if (!"anonymousUser".equals(username)) {
                //用户未登录，保存到cookie
                //将新的cartList存入cookie中
                saveCartList2Cookie(carts);
            } else {
                //用户已经登录，保存到缓存
                cartService.saveCartList2Redis(username, carts);
            }
            return new ResultInfo(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "添加失败");
        }
    }

    //在cookie中查询cartList
    @RequestMapping("/findCartList")
    private List<Cart> findCartList() {
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户为 = " + username);
        String cartsStr = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        List<Cart> cartListFromCookie = JSON.parseArray(cartsStr, Cart.class);
        //如果未登录，读取本地购物车数据
        if (cartsStr == null || cartsStr.length() == 0) {
            cartsStr = "[]";
        }
        if ("anonymousUser".equals(username)) {
            //如果未登录
            return cartListFromCookie;
        } else {
            //如果已经登录，读取缓存数据
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);
            if (cartListFromCookie != null && cartListFromCookie.size() > 0) {
                //合并购物车
                List<Cart> cartListByMerge = cartService.mergeCartList(cartListFromRedis, cartListFromCookie);
                //清楚本地缓存
                CookieUtil.deleteCookie(request, response, "cartList");
                //将合并后的购物车放入缓存
                cartService.saveCartList2Redis(username, cartListByMerge);
                //重新获取新的缓存数据
                cartListFromRedis = cartService.findCartListFromRedis(username);
            }
            return cartListFromRedis;
        }
    }

    //向cookie中保存cartList
    private void saveCartList2Cookie(List<Cart> cartList) {
        String cartStr = JSON.toJSONString(cartList, true);
        CookieUtil.setCookie(request, response, "cartList", cartStr, 3600 * 24, "utf-8");
    }
}
