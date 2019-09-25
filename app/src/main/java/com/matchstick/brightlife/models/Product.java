package com.matchstick.brightlife.models;

import com.squareup.moshi.Json;

public class Product {

    @Json(name = "product_name")
    private String productName;
    @Json(name = "price")
    private Integer price;
    @Json(name = "image")
    private String image;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}