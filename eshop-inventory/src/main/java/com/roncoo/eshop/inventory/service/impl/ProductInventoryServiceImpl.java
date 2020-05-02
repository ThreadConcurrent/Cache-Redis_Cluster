package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.dao.RedisDAO;
import com.roncoo.eshop.inventory.mapper.ProductInventoryMapper;
import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 这是商品库存Service实现类
 * @author WangCheng
 * @describe
 * @create 2020-05-02 10:47
 */
@Service("productInventoryService")
@Slf4j
public class ProductInventoryServiceImpl implements ProductInventoryService {

    @Resource
    private ProductInventoryMapper productInventoryMapper;

    @Resource
    private RedisDAO redisDAO;

    @Override
    public void updateProductInventory(ProductInventory productInventory) {
        productInventoryMapper.updateProductInventoryCnt(productInventory);
        log.info("===================日志===================: 已修改数据库中的库存,商品id=" + productInventory.getProductId() + ",商品库存数量=" + productInventory.getInventoryCnt());
    }

    @Override
    public void removeProductInventoryCache(ProductInventory productInventory) {
        String key = "product:inventory:"+productInventory.getProductId();
        log.info("===================日志===================: 已删除redis中的缓存,key=" + key);
        redisDAO.delete(key);
    }

    @Override
    public ProductInventory findProductInventory(Integer productId) {
        return productInventoryMapper.findProductInventory(productId);
    }

    /**
     * 设置商品库存的缓存
     * @param productInventory 商品库存
     */
    @Override
    public void setProductInventoryCache(ProductInventory productInventory) {
        String key = "product:inventory:"+productInventory.getProductId();
        redisDAO.set(key,String.valueOf(productInventory.getInventoryCnt()));
        log.info("===================日志===================: 已更新商品库存的缓存,商品id=" + productInventory.getProductId() + ",商品库存数量=" + productInventory.getInventoryCnt() + ",key=" + key);
    }

    /**
     * 获取商品库存的缓存
     * @param productId 商品id
     * @return 商品库存
     */
    @Override
    public ProductInventory getProductInventoryCache(Integer productId) {
        Long inventoryCnt = 0L;
        String key = "product:inventory:"+productId;
        String result = redisDAO.get(key);
        if (result != null && !"".equals(result)){
            try {
                inventoryCnt = Long.valueOf(result);
                // 读到缓存
                return new ProductInventory(productId,inventoryCnt);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 没有读到缓存
        return null;
    }


}
