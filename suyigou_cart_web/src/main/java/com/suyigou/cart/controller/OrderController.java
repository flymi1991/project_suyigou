package com.suyigou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suyigou.order.service.OrderService;
import com.suyigou.pay.service.WeiXinPayService;
import com.suyigou.pojo.TbOrder;
import com.suyigou.pojo.TbPayLog;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @Reference
    private WeiXinPayService payService;

    @Reference
    private WeiXinPayService weixinPayService;


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbOrder> findAll() {
        return orderService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return orderService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param order
     * @return
     */
    @RequestMapping("/add")
    public ResultInfo add(@RequestBody TbOrder order) {
        try {
            //获取当前用户账号
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(username);
            order.setSourceType("2");//订单来源 PC
            orderService.add(order);
            return new ResultInfo(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param order
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody TbOrder order) {
        try {
            orderService.update(order);
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
    public TbOrder findOne(Long id) {
        return orderService.findOne(id);
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
            orderService.delete(ids);
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param order
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbOrder order, int page, int rows) {
        return orderService.findPage(order, page, rows);
    }

    @RequestMapping("/createNative")
    public Map createNative() {
        //在缓存中查询支付日志
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
        if (payLog != null) {
            //如果存在支付日志
            Map map = payService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
            return map;
        } else {
            return new HashMap();
        }

    }

    @RequestMapping("/queryPayStatus")
    public ResultInfo queryPayStatus(String out_trade_no) {
        ResultInfo result = null;
        int x = 1;
        while (true) {
            //调用查询接口
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {//出错
                System.out.println("支付出错");
                result = new ResultInfo(false, "支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {//如果成功
                System.out.println("支付成功");
                result = new ResultInfo(true, "支付成功");
                //修改订单状态
                orderService.updateOrderStatus(out_trade_no,
                        map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(2000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            System.out.println("x = " + x);
            if (x >= 20) {
                //1分钟超时
                result = new ResultInfo(false, "支付超时");
                break;
            }
        }
        return result;
    }
}
