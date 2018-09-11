package com.suyigou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suyigou.pojo.TbBrand;
import com.suyigou.sellergoods.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    @ResponseBody
    public List<TbBrand> findAllBrand(){
        System.out.println(brandService);
        List<TbBrand> brandList = brandService.findAll();
        return brandList;
    }
}
