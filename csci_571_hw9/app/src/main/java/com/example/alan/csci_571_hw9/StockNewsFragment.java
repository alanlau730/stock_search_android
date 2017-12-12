package com.example.alan.csci_571_hw9;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockNewsFragment extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "stock_news_section_number";
    private ListView stockNewsListView = null;
    private ArrayList<StockNewsModel> stockNewsList = null;
    private StockNewsAdapter adapter = null;
    private ProgressBar stockNewsProgressBar = null;
    private TextView newsErrorTextView = null;

    public StockNewsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static StockNewsFragment newInstance(int sectionNumber,String symbolString) {
        StockNewsFragment fragment = new StockNewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(MainActivity.STOCKSYMBOL,symbolString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_details_news, container, false);
        initStockVariables();
        initComponents(rootView);
        initListeners();
        return rootView;
    }

    private void initStockVariables(){
        if(stockNewsProgressBar != null){
            stockNewsProgressBar.setVisibility(View.VISIBLE);
        }

        if(newsErrorTextView != null){
            newsErrorTextView.setVisibility(View.GONE);
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, MainActivity.newsURL+"?query="+
                        getArguments().getString(MainActivity.STOCKSYMBOL),
                        null,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("NEWS",response.toString());

                        //what to do with the news results hmmm
                        //title,author,date,anchor
                        try {
                            JSONArray newsArray = response.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");
                            if(stockNewsList == null){
                                stockNewsList = new ArrayList<StockNewsModel>();
                            }
                            else{
                                stockNewsList.clear();
                            }


                            for(int i=0;i<newsArray.length();++i){
                                JSONObject obj = newsArray.getJSONObject(i);
                                String title = obj.getJSONObject("title").getString("$t");
                                String anchor = obj.getJSONObject("link").getString("$t");
                                String fullDate = obj.getJSONObject("pubDate").getString("$t");
                                String date = fullDate.substring(0,fullDate.indexOf("-")-1);
                                String author = obj.getJSONObject("sa:author_name").getString("$t");
                                stockNewsList.add(new StockNewsModel(title,author,date,anchor));
                            }

                            if(adapter == null){
                                adapter = new StockNewsAdapter(stockNewsList,getContext());
                                stockNewsListView.setAdapter(adapter);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            newsErrorTextView.setVisibility(View.VISIBLE);
                        }


                        stockNewsProgressBar.setVisibility(View.GONE);
                    }
                },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {//eh figure out how to display the error message later
                                error.printStackTrace();
                                stockNewsProgressBar.setVisibility(View.GONE);
                                newsErrorTextView.setVisibility(View.VISIBLE);
                            }
                        });
        Volley.newRequestQueue(getContext()).add(jsonRequest);
    }

    private void initComponents(View rootView){
        stockNewsListView = (ListView)rootView.findViewById(R.id.news_listview);
        stockNewsProgressBar = (ProgressBar)rootView.findViewById(R.id.stock_news_progressbar);
        newsErrorTextView = (TextView) rootView.findViewById(R.id.news_error_textview);
    }

    private void initListeners(){

    }
}
