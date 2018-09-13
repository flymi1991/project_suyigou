package com.suyigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.suyigou.dao.TbBrandMapper;
import com.suyigou.pojo.TbBrand;
import com.suyigou.pojo.TbBrandExample;
import com.suyigou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findByPage(int curPage, int size) {
        PageHelper.startPage(curPage, size);
        Page<TbBrand> brandList = (Page<TbBrand>) brandMapper.selectByExample(null);
        PageResult<TbBrand> pageResult = new PageResult<>();
        pageResult.setTotal(brandList.getTotal());
        pageResult.setRows(brandList.getResult());
        return pageResult;
    }

    @Override
    public void save(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public TbBrand findById(Long id) {
        TbBrand brand = brandMapper.selectByPrimaryKey(id);
        return brand;
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public PageResult findByPage(TbBrand brand, int page, int rows) {
        PageHelper.startPage(page, rows);
        TbBrandExample brandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = brandExample.createCriteria();
        if (brand != null) {
            if (brand.getName() != null && brand.getName().length() > 0) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<TbBrand> pageList= (Page<TbBrand>)brandMapper.selectByExample(brandExample);
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }
}
