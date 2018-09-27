package com.suyigou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    Map<String, Object> search(Map searchMap);

    //导入索引库
    void importList(List list);

    void deleteByGoodsIds(List<Long> goodsIdList);
}
