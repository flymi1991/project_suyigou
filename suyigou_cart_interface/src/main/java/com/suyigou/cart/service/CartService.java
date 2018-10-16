package com.suyigou.cart.service;

import com.suyigou.pojo.Cart;

import java.util.List;

public interface CartService {
    /**
     * 向购物车列表添加item
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addItem2CartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 根据username查询redis中的CartList
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将CartList保存到缓存
     * @param username
     * @param cartList
     */
    public void saveCartList2Redis(String username, List<Cart> cartList);

    /**
     * 合并购物车列表
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);


    public void delete(String username);
}
