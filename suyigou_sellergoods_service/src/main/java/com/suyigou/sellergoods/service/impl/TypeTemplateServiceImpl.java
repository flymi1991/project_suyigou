package com.suyigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.suyigou.dao.TbSpecificationOptionMapper;
import com.suyigou.dao.TbTypeTemplateMapper;
import com.suyigou.pojo.TbSpecificationOption;
import com.suyigou.pojo.TbSpecificationOptionExample;
import com.suyigou.pojo.TbTypeTemplate;
import com.suyigou.pojo.TbTypeTemplateExample;
import com.suyigou.pojo.TbTypeTemplateExample.Criteria;
import com.suyigou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        saveToRedis();//调用方法存入数据到缓存
        return new PageResult(page.getTotal(), page.getResult());
    }


    private void saveToRedis() {
        //获取模板数据
        List<TbTypeTemplate> typeTemplateList = findAll();
        for (TbTypeTemplate typeTemplate : typeTemplateList) {
            Long typeTemplateId = typeTemplate.getId();
            //缓存brand
            String brandIds = typeTemplate.getBrandIds();
            List<Map> brandList = JSON.parseArray(brandIds, Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplateId + "", brandList);
            System.out.println("保存了typeTemplate" + typeTemplateId + "的brand缓存数据，总条数：" + brandList.size());
            //缓存spec
            String specIds = typeTemplate.getSpecIds();
            List<Map> specList = JSON.parseArray(specIds, Map.class);
            specList = findSpecAndOptionList(specIds);
            redisTemplate.boundHashOps("specList").put(typeTemplateId + "", specList);
            System.out.println("保存了typeTemplate" + typeTemplateId + "的spec缓存数据，总条数：" + specList.size());
        }

    }

    private List<Map> findSpecAndOptionList(String specIds) {
        List<Map> specList = JSON.parseArray(specIds, Map.class);
        for (int i = 0; i < specList.size(); i++) {
            Map map = specList.get(i);
            Integer id = (Integer) map.get("id");
            Long specId = id.longValue();
            TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria optionExampleCriteria = optionExample.createCriteria();
            optionExampleCriteria.andSpecIdEqualTo(specId);
            List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(optionExample);
            map.put("options", specificationOptions);
        }
        return specList;
    }

    @Override
    public List<Map> findSpecList(Long id) {
        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> specList = JSON.parseArray(specIds, Map.class);
        for (Map map : specList) {
            Long specId = new Long((Integer) map.get("id"));
            TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = specificationOptionExample.createCriteria();
            //获取商品规格表
            criteria.andSpecIdEqualTo(specId);
            List<TbSpecificationOption> optionList = specificationOptionMapper.selectByExample(specificationOptionExample);
            specificationOptionExample = null;
            map.put("optionList", optionList);
        }
        return specList;
    }
}
