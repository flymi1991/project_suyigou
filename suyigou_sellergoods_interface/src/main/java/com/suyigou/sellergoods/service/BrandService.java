package com.suyigou.sellergoods.service;

import com.suyigou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;

public interface BrandService {
    //查找所有的品牌
    public List<TbBrand> findAll();

    PageResult findByPage(int curPage, int size);

    void save(TbBrand brand);

    TbBrand findById(Long id);

    void update(TbBrand brand);

    void delete(List<Long> ids);

    PageResult findByPage(TbBrand brand, int page, int rows);
}
