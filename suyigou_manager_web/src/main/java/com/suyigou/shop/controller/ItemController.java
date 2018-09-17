package com.suyigou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suyigou.pojo.TbItem;
import com.suyigou.sellergoods.service.ItemService;
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
@RequestMapping("/item")
public class ItemController {

    @Reference
    private ItemService itemService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbItem> findAll() {
        return itemService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return itemService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param item
     * @return
     */
    @RequestMapping("/add")
    public ResultInfo add(@RequestBody TbItem item) {
        try {
            itemService.add(item);
            return new ResultInfo(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param item
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody TbItem item) {
        try {
            itemService.update(item);
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
    public TbItem findOne(Long id) {
        return itemService.findOne(id);
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
            itemService.delete(ids);
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
    public PageResult search(@RequestBody TbItem item, int page, int rows) {
        return itemService.findPage(item, page, rows);
    }

}
