package com.suyigou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suyigou.pojo.TbBrand;
import com.suyigou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    //查询所有品牌
    @RequestMapping("/findAll")
    public List<TbBrand> findAllBrand() {
        System.out.println(brandService);
        List<TbBrand> brandList = brandService.findAll();
        return brandList;
    }

    //分页查询
    @RequestMapping("/findByPage")
    public PageResult findByPage(@RequestParam(name = "curPage", required = true) int curPage,
                                 @RequestParam(name = "size", required = true) int size) {
        PageResult pageResult = brandService.findByPage(curPage, size);
        System.out.println("pageResult = " + pageResult);
        return pageResult;
    }

    //保存品牌
    @RequestMapping("/save")
    public ResultInfo save(@RequestBody TbBrand brand) {
        ResultInfo result = new ResultInfo();
        try {
            brandService.save(brand);
            result.setSuccess(true);
            result.setMsg("添加成功");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMsg("添加失败");
        }
        return result;
    }

    @RequestMapping("/findById")
    public TbBrand findById(@RequestParam(name = "id", required = true) Long id) {
        return brandService.findById(id);
    }

    //修改
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody TbBrand brand) {
        ResultInfo result = new ResultInfo();
        try {
            brandService.update(brand);
            result.setSuccess(true);
            result.setMsg("更改成功");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMsg("更改失败");
        }
        return result;
    }

    //删除
    @RequestMapping("/delete")
    public ResultInfo delete(@RequestBody List<Long> ids) {
        ResultInfo result = new ResultInfo();
        try {
            brandService.delete(ids);
            result.setSuccess(true);
            result.setMsg("删除成功");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMsg("删除失败");
        }
        return result;
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, int curPage, int size) {
        PageResult pageResult = brandService.findByPage(brand, curPage, size);
        return pageResult;
    }

}
