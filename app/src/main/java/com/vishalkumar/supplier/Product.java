package com.vishalkumar.supplier;

// This class is just made for inserting demo data in database

public class Product {

    private String mName;
    private Double mTP;
    private Integer mQuantity;
    private Double mDiscount;
    private Double mFinalRate;

    public  Product(String Name, Double TP, Integer Quantity, Double Discount){

        mName = Name;
        mTP = TP;
        mQuantity = Quantity;
        mDiscount = Discount;
        mFinalRate = TP - (TP*(Discount/100));
    }

    //Get product name
    public String getName(){
        return mName;
    }

    // Get product TP
    public Double getTP(){
        return mTP;
    }

    // Get product Quantity;
    public Integer getQuantity(){
        return mQuantity;
    }

    // Get product Discount
    public Double getDiscount(){
        return mDiscount;
    }

    // Get product Final Rate
    public Double getFinalRate(){
        //Round of the result in 2 decimal places
        String result = String.format("%.2f", mFinalRate);
        return Double.parseDouble(result);
    }
}
