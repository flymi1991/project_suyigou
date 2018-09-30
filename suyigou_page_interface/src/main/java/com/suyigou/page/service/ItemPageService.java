package com.suyigou.page.service;

public interface ItemPageService {
    /**
     * 生成item静态详细页
     * @param goodsId
     */
    boolean genItemHtml(Long goodsId);
}
