package com.suyigou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suyigou.pojo.TbGoods;
import com.suyigou.pojo.TbItem;
import com.suyigou.search.service.ItemSearchService;
import com.suyigou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
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
    @Reference
    private ItemSearchService itemSearchService;

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
            goodsService.delete(ids);
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
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
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
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
            //按照 SPU ID 查询 SKU 列表(状态为 1)
            if (status.equals("1")) {//审核通过
                List<TbItem> itemList =
                        goodsService.findItemListByGoodsIdandStatus(ids, status);
                //调用搜索接口实现数据批量导入
                if (itemList.size() > 0) {
                    itemSearchService.importList(itemList);
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
