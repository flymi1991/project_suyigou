package com.suyigou.sellergoods.service;

import com.suyigou.pojo.TbBrand;

import java.util.List;

public interface BrandService {
    //查找所有的品牌
    public List<TbBrand> findAll();
}
