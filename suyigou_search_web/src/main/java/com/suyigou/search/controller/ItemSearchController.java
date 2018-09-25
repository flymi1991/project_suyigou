package com.suyigou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suyigou.search.service.ItemSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/itemsearch")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;

    @ResponseBody
    @RequestMapping("/search")
    public Map<String, Object> findAll(@RequestBody Map map) {
        Map<String, Object> resultMap = itemSearchService.search(map);
        return resultMap;
    }
}
