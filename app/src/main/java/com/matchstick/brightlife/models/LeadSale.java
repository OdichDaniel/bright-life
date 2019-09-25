package com.matchstick.brightlife.models;

public class LeadSale {
    String customerName, customerTelephone, productName;

    public LeadSale(String customerName, String customerTelephone, String productName) {
        this.customerName = customerName;
        this.customerTelephone = customerTelephone;
        this.productName = productName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerTelephone() {
        return customerTelephone;
    }

    public String getProductName() {
        return productName;
    }
}
