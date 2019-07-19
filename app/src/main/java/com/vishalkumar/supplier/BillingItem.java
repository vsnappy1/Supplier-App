package com.vishalkumar.supplier;

public class BillingItem {

    private int mId;
    private String mName;
    private int mQuantity;
    private int mStockQuantity;
    private Double mTP;
    private Double mDiscountOrPrice;
    private boolean mIsNet;

    // Public Constructor
    public BillingItem(int Id, String Name, int Quantity, int StockQuantity, Double TP, Double DiscountOrPrice, boolean isNet){
        mId = Id;
        mName = Name;
        mQuantity = Quantity;
        mStockQuantity = StockQuantity;
        mTP = TP;
        mDiscountOrPrice = DiscountOrPrice;
        mIsNet = isNet;
    }

    //Getter methods
    public int getId(){
        return mId;
    }

    public String getName(){
        return mName;
    }

    public int getQuantity(){
        return mQuantity;
    }

    public int getStockQuantity(){return mStockQuantity;}

    public Double getTP(){
        return mTP;
    }

    public Double getDiscountOrPrice(){
        return mDiscountOrPrice;
    }

    public boolean getIsNet(){return mIsNet; }

}
