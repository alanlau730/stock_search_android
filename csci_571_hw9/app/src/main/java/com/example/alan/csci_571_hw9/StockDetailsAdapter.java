package com.example.alan.csci_571_hw9;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alanl on 11/21/2017.
 */

public class StockDetailsAdapter extends ArrayAdapter<StockDetailsModel> {
    private ArrayList<StockDetailsModel> dataList = null;
    private Context context = null;


    public StockDetailsAdapter(ArrayList<StockDetailsModel> data,Context context){
            super(context,R.layout.stock_details_item,data);
            this.dataList = data;
            this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        StockDetailsModel sdm = getItem(position);

        LayoutInflater  li = LayoutInflater.from(getContext());
        convertView = li.inflate(R.layout.stock_details_item,parent,false);
        //fill the view with our data
        TextView fieldName = (TextView)convertView.findViewById(R.id.field_name);
        TextView fieldVal = (TextView)convertView.findViewById(R.id.field_value);

        //problem here is we get sdm from getItemPosition..which may be null because we only call stockmodel constructor
        //in stockcurrentfragment...so we should try to do null checks...if this doesn't work i'm going to need to try some async thing :(

        if(sdm!= null) {
            //Log.d("SDM",(sdm == null)+"");
            if(sdm.hasFieldName()) {
                //Log.d("HASFIELDNAME",sdm.hasFieldName()+"");
                fieldName.setText(sdm.getFieldName());
            }

            if(sdm.hasFieldVal()) {
                fieldVal.setText(sdm.getFieldVal());
            }


            if(sdm.hasImageVal()){
                ImageView imageView = (ImageView)convertView.findViewById(R.id.image_value);
                if(sdm.getImageVal().contains("down")){//down arrow
                    imageView.setImageResource(R.drawable.down);
                    imageView.setVisibility(View.VISIBLE);
                    //imageView.setPadding(0,0,0,0);
                }
                else{//up arrow
                    imageView.setImageResource(R.drawable.up);
                    imageView.setVisibility(View.VISIBLE);
                    //imageView.setPadding(0,0,0,0);
                }
            }
        }

        return convertView;
    }

}
