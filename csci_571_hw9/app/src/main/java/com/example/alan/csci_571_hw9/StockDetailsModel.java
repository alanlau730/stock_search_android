package com.example.alan.csci_571_hw9;

import android.widget.ImageView;

/**
 * Created by alanl on 11/21/2017.
 */

public class StockDetailsModel {//when you come back get the custom listview working
    private String fieldName = null;
    private String fieldVal = null;
    private String imageVal = null;

    public StockDetailsModel(String fieldname,String fieldval,String imageviewImg){
        if(imageviewImg != null){//will be null unless we need to display an image...probably could've used inheritance but wtv
            this.imageVal = imageviewImg;//just the up arrow or the down arrow
        }
        this.fieldName = fieldname;
        this.fieldVal = fieldval;
    }

    public String getFieldName(){return this.fieldName;}

    public String getFieldVal(){return this.fieldVal;}

    public String  getImageVal(){return this.imageVal;}

    public boolean hasImageVal(){return this.imageVal != null;}

    public boolean hasFieldName(){return this.fieldName != null;}

    public boolean hasFieldVal(){return this.fieldVal != null;}

}
