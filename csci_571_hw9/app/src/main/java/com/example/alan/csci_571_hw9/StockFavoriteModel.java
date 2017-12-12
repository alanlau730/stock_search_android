package com.example.alan.csci_571_hw9;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by alanl on 11/29/2017.
 */

public class StockFavoriteModel{//when you come back get the custom listview working
    private String stockName= null;
    private double stockPrice = Double.MIN_NORMAL;
    private double stockChangeVal = Double.MIN_NORMAL;
    private double stockChangePct = Double.MIN_NORMAL;
    private int defaultOrder = -1;
    //private JSONObject stockObject = null;

    public StockFavoriteModel(String stockname, double stockprice, double stockchangeval, double stockchangepct){
        this.stockName = stockname;
        this.stockPrice = stockprice;
        this.stockChangeVal = stockchangeval;
        this.stockChangePct = stockchangepct;

        //make a jsonobject representation
        /*stockObject = new JSONObject();
        try {
            stockObject.put("Symbol",stockname);
            stockObject.put("Price",stockprice);
            stockObject.put("ChangeValue",stockchangeval);
            stockObject.put("ChangePercent",stockchangepct);
            Log.d("STOCKOBJ",stockObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }


    public String getStockName(){return this.stockName;}

    public double getStockPrice(){return this.stockPrice;}

    public double  getStockChangeVal(){return this.stockChangeVal;}

    public double getStockChangePct(){return this.stockChangePct;}

    public boolean hasStockName(){return this.stockName!=null;}

    public boolean hasStockPrice(){return this.stockPrice!= Double.MIN_NORMAL;}

    public boolean  hasStockChangeVal(){return this.stockChangeVal!= Double.MIN_NORMAL;}

    public boolean hasStockChangePct(){return this.stockChangePct!= Double.MIN_NORMAL;}

    public boolean hasDefaultOrder(){return this.defaultOrder!= -1;}

    public int getDefaultOrder(){return this.defaultOrder;}

    public void setDefaultOrder(int order){this.defaultOrder = order;}

    public void setStockPrice(double price){this.stockPrice = price;}

    public void setStockChangeVal(double changeval){this.stockChangeVal = changeval;}

    public void setStockChangePct(double changePct){this.stockChangePct = changePct;}

}