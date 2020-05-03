package com.roncoo.eshop.cache.service;

import com.roncoo.eshop.cache.model.ProductInfo;

/**
 * 缓存Service接口
 * @author WangCheng
 * @describe
 * @create 2020-05-03 11:08
 */
public interface CacheService {

    /**
     * 将商品信息保存到本地缓存中
     * @param productInfo 商品信息
     * @return
     */
    public ProductInfo saveLocalCache(ProductInfo productInfo);

    /**
     * 从本地缓存中获取商品信息
     * @param productId 商品Id
     * @return
     */
    public ProductInfo getLocalCache(Long productId);

}
