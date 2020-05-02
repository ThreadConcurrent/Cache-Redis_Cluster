package com.roncoo.eshop.inventory.model;

/**
 * 库存数量Model
 * @author WangCheng
 * @describe
 * @create 2020-05-02 10:35
 */

public class ProductInventory {

    /**
     * 商品Id
     */
    private Integer productId;

    /**
     * 库存数量
     */
    private Long inventoryCnt;

    public ProductInventory(){

    }

    public ProductInventory(Integer productId, Long inventoryCnt) {
        this.productId = productId;
        this.inventoryCnt = inventoryCnt;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Long getInventoryCnt() {
        return inventoryCnt;
    }

    public void setInventoryCnt(Long inventoryCnt) {
        this.inventoryCnt = inventoryCnt;
    }
}
