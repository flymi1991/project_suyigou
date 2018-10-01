package com.suyigou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suyigou.page.service.ItemPageService;
import com.suyigou.pojo.TbGoods;
import com.suyigou.pojo.TbItem;
import com.suyigou.search.service.ItemSearchService;
import com.suyigou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Reference(timeout = 300000)
    private ItemPageService itemPageService;

    /**
     * 生成静态页面
     *
     * @param goodsId
     */
    @RequestMapping("/genHtml")
    public void genItemHtml(Long goodsId) {
        boolean success = itemPageService.genItemHtml(goodsId);
        if (success) {
            System.out.println("静态页面生成成功,goodsId=" + goodsId);
        } else {
            System.out.println("静态页面生成失败,goodsId=" + goodsId);
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
                //生成静态页面
                for (Long goodsId : ids) {
                    itemPageService.genItemHtml(goodsId);
                }
                //按照 SPU ID 查询 SKU 列表(状态为 1)
                List<TbItem> itemList =
                        goodsService.findItemListByGoodsIdandStatus(ids, status);
                //调用搜索接口实现数据批量导入
                if (itemList != null && itemList.size() > 0) {
                    // TODO: 2018/9/30 17:52 将审核通过的SKU导入solr索引库
                    itemSearchService.importList(itemList);
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
