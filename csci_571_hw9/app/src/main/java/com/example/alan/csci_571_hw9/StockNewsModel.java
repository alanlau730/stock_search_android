package com.example.alan.csci_571_hw9;

/**
 * Created by alanl on 11/24/2017.
 */

public class StockNewsModel {
    private String title = null;
    private String author = null;
    private String date = null;
    private String anchor = null;

    public StockNewsModel(String title, String author, String date, String anchor){
        this.title = title;
        this.author = author;
        this.date = date;
        this.anchor = anchor;
    }

    public String getTitle(){return this.title;}

    public String getAuthor(){return this.author;}

    public String  getDate(){return this.date;}

    public String getAnchor(){return this.anchor;}

    public boolean hasTitle(){return this.title != null;}

    public boolean hasAuthor(){return this.author != null;}

    public boolean hasDate(){return this.date != null;}

    public boolean hasAnchor(){return this.anchor != null;}
}

