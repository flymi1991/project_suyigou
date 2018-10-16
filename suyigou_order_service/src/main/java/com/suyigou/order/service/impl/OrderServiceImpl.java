package com.suyigou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.suyigou.dao.TbOrderItemMapper;
import com.suyigou.dao.TbOrderMapper;
import com.suyigou.dao.TbPayLogMapper;
import com.suyigou.order.service.OrderService;
import com.suyigou.pojo.*;
import com.suyigou.pojo.TbOrderExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import utils.IdWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        ArrayList<Object> orderIdList = new ArrayList<>();//订单编号列表
        double totalMoney = 0;//支付总金额
        for (Cart cart : cartList) {
            TbOrder order2Save = new TbOrder();
            long id = idWorker.nextId();
            System.out.println("用户名" + order.getSellerId());
            order2Save.setOrderId(id);
            order2Save.setCreateTime(new Date());
            order2Save.setPaymentType(order.getPaymentType());
            order2Save.setStatus("0");
            order2Save.setUserId(order.getUserId());
            order2Save.setReceiverAreaName(order.getReceiverAreaName());
            order2Save.setReceiverMobile(order.getReceiverMobile());
            order2Save.setReceiver(order.getReceiver());
            order2Save.setSellerId(order.getSellerId());
            double sum = 0;

            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(id);
                orderItem.setSellerId(orderItem.getSellerId());
                sum += orderItem.getTotalFee().doubleValue();
                orderItemMapper.insert(orderItem);
            }
            order2Save.setPayment(new BigDecimal(sum));
            totalMoney += sum;
            orderIdList.add(id + "");//添加到订单编号列表

            if ("1".equals(order.getPaymentType())) {
                TbPayLog payLog = new TbPayLog();
                payLog.setCreateTime(new Date());
                payLog.setUserId(order.getUserId());
                payLog.setOrderList(orderIdList.toString().replace("[", "").replace("]", ""));
                payLog.setTotalFee((long) (totalMoney * 100));
                long tradeId = new IdWorker().nextId();
                payLog.setOutTradeNo(tradeId + "");//支付订单号
                payLog.setTradeState("0");
                payLogMapper.insert(payLog);
                redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
            }

            orderMapper.insert(order2Save);
        }
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 提交订单
     */
    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
        return payLog;
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //1. 修改支付日志状态
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());//支付时间
        payLog.setTradeState("1");//支付状态
        payLog.setTransactionId(transaction_id);//交易号
        payLogMapper.updateByPrimaryKey(payLog);//更新数据库
        //2. 修改关联的订单的状态
        String orderList = payLog.getOrderList();//获取订单号列表
        String[] orderIds = orderList.split(",");//获取订单号数组
        for(String orderId:orderIds){
            TbOrder order =
                    orderMapper.selectByPrimaryKey( Long.parseLong(orderId) );
            if(order!=null){
                order.setStatus("2");//已付款
                orderMapper.updateByPrimaryKey(order);
            }
        }
        //3. 清除缓存中的支付日志对象
        //清除 redis 缓存数据
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
