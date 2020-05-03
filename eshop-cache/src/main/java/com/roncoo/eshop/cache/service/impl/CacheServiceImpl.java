package com.roncoo.eshop.cache.service.impl;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 缓存Service实现类
 * @author WangCheng
 * @describe
 * @create 2020-05-03 11:11
 */
@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    public static final String CACHE_NAME = "local";

    /**
     * 将商品信息保存到本地缓存中
     * @param productInfo 商品信息
     * @return
     */
    @CachePut(value = CACHE_NAME, key = "'key_'+#productInfo.getId()")
    public ProductInfo saveLocalCache(ProductInfo productInfo){
        return productInfo;
    }

    /**
     * 从本地缓存中获取商品信息
     * @param productId 商品Id
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'key_'+#id")
    public ProductInfo getLocalCache(Long productId){
        return null;
    }
}
