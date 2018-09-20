package com.suyigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.suyigou.dao.*;
import com.suyigou.pojo.*;
import com.suyigou.pojo.TbGoodsExample.Criteria;
import com.suyigou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        goods.getGoods().setAuditStatus("0");//设置未申请状态:如果是经过修改的商品，需要重新设置状态
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //删除原有的 sku 列表数据
        TbItemExample example=new TbItemExample();
        com.suyigou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);
        //插入新的数据
        List<TbItem> itemList = goods.getItemList();
        for (TbItem item : itemList) {
            itemMapper.insert(item);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        goods.setGoods(goodsMapper.selectByPrimaryKey(id));
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(goods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNotNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(Goods goods) {
        //插入TbGoods表
        goods.getGoods().setAuditStatus("0");
        goodsMapper.insert(goods.getGoods());
        //插入TbGoodsDesc表
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insert(goods.getGoodsDesc());
        //插入TbItem表
        List<TbItem> itemList = goods.getItemList();
        if (itemList != null && itemList.size() != 0) {
            for (TbItem item : itemList) {
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());
                item.setGoodsId(goods.getGoods().getId());
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                for (String key : map.keySet()) {
                    Object value = map.get(key);
                    title += " " + value;
                }
                item.setTitle(title);
                String sellerId = goods.getGoods().getSellerId();
                TbSeller seller = sellerMapper.selectByPrimaryKey(sellerId);
                item.setSeller(seller.getNickName());
                item.setCategoryid(goods.getGoods().getCategory3Id());
                Long brandId = goods.getGoods().getBrandId();
                TbBrand brand = brandMapper.selectByPrimaryKey(brandId);
                item.setBrand(brand.getName());
                String itemImages = goods.getGoodsDesc().getItemImages();
                List<Map> imgList = JSON.parseArray(itemImages, Map.class);
                if (imgList.size() > 0) {
                    String url = (String)(imgList.get(0).get("url"));
                    item.setImage(url);
                }
                itemMapper.insert(item);
            }
        }
    }
}
