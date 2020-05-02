package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import lombok.extern.slf4j.Slf4j;

/**
 * 重新加载商品库存的缓存
 * @author WangCheng
 * @describe
 * @create 2020-05-02 10:57
 */
@Slf4j
public class ProductInventoryCacheRefreshRequest implements Request{

    /**
     * 商品Id
     */
    private Integer productId;

    /**
     * 商品库存Service
     */
    private ProductInventoryService productInventoryService;

    /**
     * 是否强制刷新缓存
     */
    private boolean forceRefresh;

    public ProductInventoryCacheRefreshRequest(Integer productId,
                                               ProductInventoryService productInventoryService,
                                               boolean forceRefresh){
        this.productId = productId;
        this.productInventoryService = productInventoryService;
        this.forceRefresh = false;
    }


    /**
     *  重新加载缓存
     */
    @Override
    public void process() {
        // 从数据库中加载最新的商品库存数量
        ProductInventory productInventory = productInventoryService.findProductInventory(productId);
        log.info("===================日志===================: 已查询到商品最新的库存数量,商品Id=" + productId + ",商品库存数量=" + productInventory.getInventoryCnt());
        // 将最新的商品库存数量,刷新到redis缓存中去
        productInventoryService.setProductInventoryCache(productInventory);
    }

    @Override
    public Integer getProductId() {
        return productId;
    }

    public boolean isForceRefresh() {
        return forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }
}
