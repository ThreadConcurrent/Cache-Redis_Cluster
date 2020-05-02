package com.roncoo.eshop.inventory.mapper;

import com.roncoo.eshop.inventory.model.ProductInventory;
import org.apache.ibatis.annotations.Param;

/**
 * 库存数量mapper
 * @author WangCheng
 * @describe
 * @create 2020-05-02 10:30
 */
public interface ProductInventoryMapper {

    /**
     * @param productInventory 商品库存
     */
    void updateProductInventoryCnt(ProductInventory productInventory);

    /**
     * 根据商品Id查询商品库存信息
     * @param productId 商品Id
     * @return 商品库存信息
     */
    ProductInventory findProductInventory(@Param("productId") Integer productId);
}
