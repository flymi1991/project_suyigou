package com.suyigou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.suyigou.pojo.TbGoods;
import com.suyigou.pojo.TbItem;
import com.suyigou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;
    /*@Reference
    private ItemSearchService itemSearchService;*/

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination queueSolrAddDestination;

    @Autowired
    private Destination queueSolrDeleteDestination;

    @Autowired
    private Destination topicsPageAddDestination;

    @Autowired
    private Destination topicsPageDeleteDestination;


    /**
     * 生成静态页面
     *
     * @param goodsId
     */
    @RequestMapping("/genHtml")
    public void genItemHtml(Long goodsId) {
        Long[] ids = {goodsId};
        //按照 SPU ID 查询 SKU 列表(状态为 1)
        List<TbItem> itemList =
                goodsService.findItemListByGoodsIdandStatus(ids, "1");
        //调用搜索接口实现数据批量导入
        if (itemList != null && itemList.size() > 0) {
            final String jsonString = JSON.toJSONString(itemList);
            //生成静态页面
            jmsTemplate.send(topicsPageAddDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage message = session.createTextMessage(jsonString);
                    return message;
                }
            });
        }
    }

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }


    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
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
            //从表中逻辑删除
            goodsService.delete(ids);
            //从solr索引库删除
            //itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            final String jsonString = JSON.toJSONString(ids);
            jmsTemplate.send(topicsPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage message = session.createTextMessage(jsonString);
                    return message;
                }
            });
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage message = session.createTextMessage(jsonString);
                    return message;
                }
            });
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        PageResult pageResult = goodsService.findPage(goods, page, rows);
        return pageResult;
    }

    /**
     * 更新状态
     *
     * @param ids
     * @param status
     */
    @RequestMapping("/updateStatus")
    public ResultInfo updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            //更新 SKU 状态
            goodsService.updateItemStatus(ids, status);

            if (status.equals("1")) {
                //审核通过

                //发送生成静态页面指令
                //将itemList转为json字符串
                final String idsJsonStr = JSON.toJSONString(ids);
                //生成静态页面
                jmsTemplate.send(topicsPageAddDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage message = session.createTextMessage(idsJsonStr);
                        return message;
                    }
                });

                //按照 SPU ID 查询 SKU 列表(状态为 1)
                List<TbItem> itemList =
                        goodsService.findItemListByGoodsIdandStatus(ids, status);
                final String itemListJsonStr = JSON.toJSONString(itemList);
                //调用搜索接口实现数据批量导入
                if (itemList != null && itemList.size() > 0) {
                    // 将审核通过的SKU导入solr索引库
                    jmsTemplate.send(queueSolrAddDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            TextMessage message = session.createTextMessage(itemListJsonStr);
                            return message;
                        }
                    });
                    System.out.println("itemList = " + itemList.toArray());
                } else {
                    System.out.println("没有明细数据");
                }
            }
            return new ResultInfo(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "失败");
        }
    }

}
