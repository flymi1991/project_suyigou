package com.suyigou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.suyigou.dao.TbGoodsDescMapper;
import com.suyigou.dao.TbGoodsMapper;
import com.suyigou.page.service.ItemPageService;
import com.suyigou.pojo.TbGoods;
import com.suyigou.pojo.TbGoodsDesc;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service(timeout = 60000)
public class ItemsPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

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
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            Map map = new HashMap<>();
            map.put("goods", goods);
            map.put("goodsDesc", goodsDesc);
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
                System.out.println(e.getMessage() + "writer资源关闭出错");
            }
        }
    }
}
