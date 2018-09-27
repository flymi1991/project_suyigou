package com.suyigou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.suyigou.pojo.TbItem;
import com.suyigou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service(timeout = 60000)
public class ItemServiceISearchImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(searchList(searchMap));
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //重新查询品牌列表和规格列表
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)) {
            //如果查询参数中有category选项
            map.putAll(searchBrandAndSpecList(categoryName));
        } else {
            //如果没有category选项，默认按照第一个查询
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }
        return map;
    }


    //创建私有方法，用于返回查询列表的结果（高亮）
    private Map<String, Object> searchList(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        ArrayList<TbItem> items = new ArrayList<>();
        HighlightQuery query = new SimpleHighlightQuery();

        //按关键字过滤
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //指定高亮域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);

        //商品类别过滤
        if (!"".equals(searchMap.get("category"))) {
            Criteria cateCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery categoryQuery = new SimpleFilterQuery(cateCriteria);
            query.addFilterQuery(categoryQuery);
        }
        //按品牌过滤
        if (!"".equals(searchMap.get("brand"))) {
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery brandQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(brandQuery);
        }

        //按规格过滤
        Map specMap = (Map) searchMap.get("spec");
        if (specMap != null) {
            Set set = specMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = (String) specMap.get(key);
                key = "item_spec_" + key;
                Criteria specCriteria = new Criteria(key).is(value);
                SimpleFilterQuery specQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(specQuery);
            }
        }
        //获取查询结果集
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : entryList) {
            //获取原实体类
            TbItem item = highlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();
            if (highlightList.size() > 0 && highlightList.get(0).getSnipplets().size() > 0) {
                String title = highlightEntry.getHighlights().get(0).getSnipplets().get(0);
                item.setTitle(title);//设置高亮的结果
                items.add(item);
            }
        }
        //结果集
        map.put("rows", items);
        return map;
    }

    //根据搜索关键字查询所有category
    private List<String> searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_title").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> entries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> groupList = entries.getContent();
        for (GroupEntry<TbItem> entry : groupList) {
            String value = entry.getGroupValue();
            list.add(value);
        }
        return list;
    }

    //创建私有方法，从缓存读取数据
    private Map searchBrandAndSpecList(String category) {
        HashMap<Object, Object> map = new HashMap<>();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId != null) {
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId + "");
            map.put("brandList", brandList);
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId + "");
            map.put("specList", specList);
        }
        return map;
    }
}