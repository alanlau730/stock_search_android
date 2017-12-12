package com.example.alan.csci_571_hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alanl on 11/24/2017.
 */

public class StockNewsAdapter extends ArrayAdapter<StockNewsModel> {
    private ArrayList<StockNewsModel> dataList = null;
    private Context context = null;


    public StockNewsAdapter(ArrayList<StockNewsModel> data,Context context){
        super(context,R.layout.stock_news_item,data);
        this.dataList = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final StockNewsModel snm = getItem(position);

        LayoutInflater  li = LayoutInflater.from(getContext());
        convertView = li.inflate(R.layout.stock_news_item,parent,false);
        //fill the view with our data
        TextView newsTitle = (TextView)convertView.findViewById(R.id.news_title);
        TextView newsAuthor = (TextView)convertView.findViewById(R.id.news_author);
        TextView newsDate = (TextView) convertView.findViewById(R.id.news_date);

        if(snm!= null) {
            if(snm.hasAuthor()){
                newsAuthor.setText("Author:"+snm.getAuthor());
            }

            if(snm.hasTitle()){
                newsTitle.setText(snm.getTitle());
            }

            if(snm.hasDate()){
                newsDate.setText(snm.getDate()+" EST");//wtv the stock exchange is in ny i'll just hardcode this
            }

        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//open the actual article
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(snm.getAnchor()));
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }

}
