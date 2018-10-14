package com.suyigou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.suyigou.cart.service.CartService;
import com.suyigou.dao.TbItemMapper;
import com.suyigou.pojo.Cart;
import com.suyigou.pojo.TbItem;
import com.suyigou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<Cart> addItem2CartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据itemId得到item，并进行健壮性判断
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品状态无效");
        }
        //2.查找item对应的商家是否在cartList中存在
        Cart cart = searchCartListBySellerId(cartList, item.getSellerId());
        if (cart == null) {
            //3.1.如果不存在，需要新建
            cart = new Cart();
            cart.setSellerName(item.getSeller());
            cart.setSellerId(item.getSellerId());
            ArrayList<TbOrderItem> orderItems = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItems.add(orderItem);
            cart.setOrderItemList(orderItems);
            cartList.add(cart);
        } else {
            //3.2.如果存在，查找orderItemList中是否有对应的item存在
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = searchOrderItemListByItemId(orderItemList, itemId);
            if (orderItem == null) {
                //如果不存在，需要创建
                orderItem = createOrderItem(item, num);
                orderItemList.add(orderItem);
            } else {
                //如果存在，需要更新价格小计和数量
                num += orderItem.getNum();
                orderItem.setNum(num);
                orderItem.setTotalFee(item.getPrice().multiply(BigDecimal.valueOf(num)));
                //如果数量为0，需要更新orderItemList
                if (num <= 0) {
                    cart.getOrderItemList().remove(orderItem);//移除购物车明细
                }
                //如果移除后 cart 的明细数量为 0，则将 cart 移除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    /**
     * 查找列表中是否有某个元素存在
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemListByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            //将包装类转化为基础类型，否则比较的是地址值
            if (tbOrderItem.getItemId().longValue() == itemId.longValue()) {
                return tbOrderItem;
            }
        }
        return null;
    }

    /**
     * 根据item创建orderItem
     *
     * @param item
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        //计算价格
        orderItem.setTotalFee(item.getPrice().multiply(BigDecimal.valueOf(num)));

        return orderItem;
    }

    /**
     * 查找列表中是否有某个元素存在
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartListBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null || cartList.size() > 0) {
            for (Cart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }
        return null;
    }


    /**
     * 保存Item到购物车
     *
     * @param cart
     * @param item
     * @param num
     * @return
     */
    public Cart addItem2Cart(Cart cart, TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPrice(item.getPrice());
        cart.getOrderItemList().add(orderItem);
        return cart;
    }

    /**
     * 保存CartList到Redis
     *
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据............." + username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null) {
            cartList = new ArrayList<Cart>();
        }
        return cartList;
    }

    /**
     * 从redis中查询CartList
     *
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartList2Redis(String username, List<Cart> cartList) {
        System.out.println("向redis中存入购物车数据...." + username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                cartList1 =
                        addItem2CartList(cartList1, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList1;
    }
}

