package com.suyigou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.suyigou.dao.TbGoodsDescMapper;
import com.suyigou.dao.TbGoodsMapper;
import com.suyigou.dao.TbItemCatMapper;
import com.suyigou.dao.TbItemMapper;
import com.suyigou.page.service.ItemPageService;
import com.suyigou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 60000)
public class ItemsPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Value("${pagedir}")
    private String pagedir;

    /**
     * 生成静态页面
     *
     * @param goodsId
     */
    @Override
    public boolean genItemHtml(Long goodsId) {
        FileWriter writer = null;
        try {
            Configuration configuration = configurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            //1.查询goods和goodsDesc
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            Map map = new HashMap<>();
            map.put("goods", goods);
            map.put("goodsDesc", goodsDesc);

            //2.查询三级分类，导入模板
            TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
            TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
            TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());

            map.put("itemCat1", itemCat1);
            map.put("itemCat2", itemCat2);
            map.put("itemCat3", itemCat3);

            //4.SKU 列表
            TbItemExample itemExample = new TbItemExample();
            TbItemExample.Criteria itemExampleCriteria = itemExample.createCriteria();
            itemExampleCriteria.andGoodsIdEqualTo(goodsId);//根据goodsId查询
            itemExampleCriteria.andStatusEqualTo("1");//确保已经审核
            List<TbItem> itemList = itemMapper.selectByExample(itemExample);
            itemExample.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默认
            map.put("itemList", itemList);

            writer = new FileWriter(pagedir + goodsDesc.getGoodsId() + ".html");
            template.process(map, writer);
            return true;
        } catch (Exception e) {
            System.out.println("静态资源生成出错");
            return false;
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                System.out.println(e.getMessage() + "writer输出流关闭出错");
            }
        }
    }
}
