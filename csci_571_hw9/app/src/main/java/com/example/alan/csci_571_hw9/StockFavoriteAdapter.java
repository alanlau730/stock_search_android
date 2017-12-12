package com.example.alan.csci_571_hw9;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alanl on 11/29/2017.
 */

public class StockFavoriteAdapter extends ArrayAdapter<StockFavoriteModel> {
    private ArrayList<StockFavoriteModel> dataList = null;
    private Context context = null;


    public StockFavoriteAdapter(ArrayList<StockFavoriteModel> data,Context context){
        super(context,R.layout.stock_favorite_item,data);
        this.dataList = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final StockFavoriteModel sfm = getItem(position);

        LayoutInflater li = LayoutInflater.from(getContext());
        convertView = li.inflate(R.layout.stock_favorite_item,parent,false);

        //fill the view with our data
        TextView stockSymbol = (TextView)convertView.findViewById(R.id.favorite_symbol);
        TextView stockPrice = (TextView)convertView.findViewById(R.id.favorite_price);
        TextView stockPriceChange = (TextView)convertView.findViewById(R.id.favorite_price_change);


        if(sfm!= null) {
            //Log.d("SDM",(sdm == null)+"");
            if(sfm.hasStockName()) {
                //Log.d("HASFIELDNAME",sdm.hasFieldName()+"");
                stockSymbol.setText(sfm.getStockName());
            }

            if(sfm.hasStockPrice()) {
                stockPrice.setText(sfm.getStockPrice()+"");
            }


            if(sfm.hasStockChangePct() && sfm.hasStockChangeVal()){
                if(sfm.getStockChangeVal() < 0){
                    stockPriceChange.setTextColor(Color.RED);
                }
                else{
                    stockPriceChange.setTextColor(Color.GREEN);
                }
                String changeStr = String.format("%.2f",sfm.getStockChangeVal());
                String changePctStr = String.format("%.2f",sfm.getStockChangePct());
                stockPriceChange.setText(changeStr+" ("+changePctStr+"%)");
            }
        }

        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CLICKEDSYMBOL",sfm.getStockName());
                //
            }
        });*/
        /*convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("LONGCLICK","ISBEINGCLICKED");
                return false;
            }
        });*/

        return convertView;
    }

}