package com.suyigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.suyigou.dao.TbBrandMapper;
import com.suyigou.pojo.TbBrand;
import com.suyigou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service(timeout = 8000)
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }
}
