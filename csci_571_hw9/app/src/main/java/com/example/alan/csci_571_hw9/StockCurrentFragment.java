package com.example.alan.csci_571_hw9;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.example.alan.csci_571_hw9.MainActivity.autocompleteURL;

/**
 * A simple {@link Fragment} subclass.
 */

//NEED TO MAKE COPY OF GOOGLE CLOUD VERSION -.- and use that to make changes cuz still grading hw8

public class StockCurrentFragment extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "stock_current_section_number";
    //layout components
    private ImageButton fbButton = null;
    private ImageButton favButton = null;

    private ListView stockSymbolListView = null;
    private ArrayList<StockDetailsModel> stockDetailsList = null;
    private StockDetailsAdapter adapter = null;

    private Spinner indicatorSpinner = null;
   // private String currIndicatorString = "Price";
    private Button changeButton = null;
    private WebView chartWebView = null;

    //stock symbol data
    private JSONObject quoteJSON = null;
    private JSONObject stockRecentQuote = null;
    private JSONObject stockPrevRecentQuote = null;
    private String stockSymbol = null;
    //private Double stockLastPrice = Double.MIN_VALUE;
    private Double stockChangeVal = Double.MIN_VALUE;
    private Double stockChangePercent = Double.MIN_VALUE;//double check the change percent calculation when you have time
    private String stockTimeStamp = null;
    private Double stockOpenPrice = Double.MIN_VALUE;
    private Double stockClosePrice = Double.MIN_VALUE;
    private Double stockPrevClosePrice = Double.MIN_VALUE;
    private String stockDayRange = null;
    private Long stockVolume = Long.MIN_VALUE;

    private ProgressBar stockdetailsProgressBar = null;
    //private ProgressBar stockChartProgressBar = null;
    private boolean noJSInterfacesYet = true;
    private boolean showStockChart = false;

    private TextView currentTextView = null;

    private Gson stockCurrGson = null;//don't want to make it again after every favorite button click
    public StockCurrentFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static StockCurrentFragment newInstance(int sectionNumber,String symbolString) {//pass in the json data for our table
        StockCurrentFragment fragment = new StockCurrentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(MainActivity.STOCKSYMBOL,symbolString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_details_current, container, false);
        //intialize other layout components and listeners
        //well here goes nothing call volley here and refactor and load other view components that dont need the json
        initStockVariables();
        initComponents(rootView);
        initListeners();
        return rootView;
    }




    private void initStockVariables(){
        //parse json string and assign the necessary data to variables
        //pls let this work

        stockCurrGson = new Gson();

        if(stockdetailsProgressBar!= null) {
            stockdetailsProgressBar.setVisibility(View.VISIBLE);
        }

        if(currentTextView != null){
            currentTextView.setVisibility(View.GONE);
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, MainActivity.quoteURL+"?query="+
                        getArguments().getString(MainActivity.STOCKSYMBOL),
                        null,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            /*JSONObject*/ quoteJSON = response;



                            JSONObject timeSeriesData = quoteJSON.getJSONObject("Time Series (Daily)");
                            String stockRecentTime = "";
                            int count = 0;
                            Iterator<String> entry = timeSeriesData.keys();
                            while(entry.hasNext()){
                                if(count >=2){
                                    break;
                                }
                                ++count;
                                String key = entry.next();
                                if(count == 1){
                                    stockRecentQuote = timeSeriesData.getJSONObject(key);
                                    stockRecentTime = key;
                                }

                                if(count == 2){
                                    stockPrevRecentQuote = timeSeriesData.getJSONObject(key);
                                }
                            }

                            stockSymbol = getArguments().getString(MainActivity.STOCKSYMBOL);

                            //now check shared pref. if it has the symbol, make the star filled, otherwise it's empty
                            SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFERENCE_KEY,MODE_PRIVATE);

                            if(prefs.getString(stockSymbol,null) == null){//not in favorites list yet
                                favButton.setImageResource(R.drawable.empty);
                                Log.d("FAVINIT","INITIALIZE TO EMPTY");
                            }

                            else{
                                favButton.setImageResource(R.drawable.filled);
                                Log.d("FAVINIT","INITIALIZE TO FILL");
                            }




                            stockChangeVal = stockRecentQuote.getDouble("4. close")-stockPrevRecentQuote.getDouble("4. close");
                            stockChangePercent = stockChangeVal/(stockPrevRecentQuote.getDouble("4. close"))*100;
                            stockTimeStamp = quoteJSON.getString("TimeStamp");
                            Log.d("TIMESTAMP",stockTimeStamp);
                            String currDate = stockTimeStamp.substring(0,stockTimeStamp.indexOf(" "));
                            //int currHr = Integer.valueOf(stockTimeStamp.substring(stockTimeStamp.indexOf(" ")+1,stockTimeStamp.indexOf(":")));
                            stockClosePrice = stockRecentQuote.getDouble("4. close");
                            //for the time only
                            boolean duringWorkHrs = false;

                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            try {
                                Date startTime = sdf.parse("09:30:00");
                                Date endTime = sdf.parse("16:00:00");
                                String currTimeString = stockTimeStamp.substring(stockTimeStamp.indexOf(" ")+1,stockTimeStamp.indexOf("EDT")-1);
                                Date currTime = sdf.parse(currTimeString);
                                duringWorkHrs = (currDate.equals(stockRecentTime) && ((currTime.equals(startTime) ||
                                        (currTime.after(startTime) && currTime.before(endTime))  )));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            if(duringWorkHrs ){//if it's a workday and we are during work hours set close price to close price of previous quote
                                stockClosePrice = stockPrevRecentQuote.getDouble("4. close");
                            }


                            stockPrevClosePrice = stockPrevRecentQuote.getDouble("4. close");
                            stockOpenPrice = stockRecentQuote.getDouble("1. open");
                            stockDayRange = String.format("%.2f",stockRecentQuote.getDouble("3. low")) + " - "+
                                    String.format("%.2f",stockRecentQuote.getDouble("2. high"));
                            stockVolume = stockRecentQuote.getLong("5. volume");



                            //fill the stockdetails list view when ready

                            if(stockDetailsList == null){
                                stockDetailsList = new ArrayList<StockDetailsModel>();
                            }
                            else{
                                stockDetailsList.clear();
                            }

                            String changeArrowString = "down.png";
                            if(stockChangeVal >= 0){
                                changeArrowString = "up.png";
                            }

                            stockDetailsList.add(new StockDetailsModel("Stock Symbol",stockSymbol,null));
                            stockDetailsList.add(new StockDetailsModel("Last Price",stockClosePrice+"",null));
                            stockDetailsList.add(new StockDetailsModel("Change",String.format("%.2f",stockChangeVal)+" ("+
                                    String.format("%.2f",stockChangePercent)+"%)",changeArrowString));
                            stockDetailsList.add(new StockDetailsModel("Timestamp",stockTimeStamp,null));
                            stockDetailsList.add(new StockDetailsModel("Open",stockOpenPrice+"",null));
                            stockDetailsList.add(new StockDetailsModel("Close",stockClosePrice+"",null));
                            stockDetailsList.add(new StockDetailsModel("Day's Range",stockDayRange,null));
                            stockDetailsList.add(new StockDetailsModel("Volume",stockVolume+"",null));

                            if(adapter == null) {
                                adapter = new StockDetailsAdapter(stockDetailsList, getContext());
                                stockSymbolListView.setAdapter(adapter);
                                stockSymbolListView.setVisibility(View.VISIBLE);
                            }

                            stockdetailsProgressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {//consider more complex error handling later
                            stockdetailsProgressBar.setVisibility(View.GONE);//it's gone but this time we messed up
                            currentTextView.setVisibility(View.VISIBLE);
                            Log.d("ERROR","JSON PARSE ERROR");
                            e.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {

                   @Override
                   public void onErrorResponse(VolleyError error) {//eh figure out how to display the error message later
                       error.printStackTrace();
                       Log.d("CURRENTFRAGMENT","LOADING DETAILS ERROR");
                       currentTextView.setVisibility(View.VISIBLE);
                       stockdetailsProgressBar.setVisibility(View.GONE);

                   }
                });
        Volley.newRequestQueue(getContext()).add(jsonRequest);

    }

    private void initComponents(View rootView){//could stock data just be a list of textviews? :O
        stockdetailsProgressBar = (ProgressBar)rootView.findViewById(R.id.stock_details_progressbar);

        currentTextView = (TextView)rootView.findViewById(R.id.current_error_textview);
        /*stockChartProgressBar = (ProgressBar)rootView.findViewById(R.id.stock_chart_progressbar);
        stockChartProgressBar.setVisibility(View.GONE);
        */
        fbButton = (ImageButton)rootView.findViewById(R.id.facebook_button);
        favButton = (ImageButton)rootView.findViewById(R.id.favoritetoggle_button);
        stockSymbolListView = (ListView)rootView.findViewById(R.id.stock_symbol_listview);
        indicatorSpinner = (Spinner)rootView.findViewById(R.id.indicator_spinner);
        changeButton = (Button)rootView.findViewById(R.id.change_button);
        chartWebView = (WebView)rootView.findViewById(R.id.chart_webview);
        //chartWebView = rootView.findViewById(R.id.chart_webview);
        chartWebView.getSettings().setJavaScriptEnabled(true);
        chartWebView.getSettings().setDomStorageEnabled(true);

    }

    private void initListeners(){

        indicatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//only fires if different item is selected
               // String selectedIndicator = adapterView.getItemAtPosition(i).toString();
                changeButton.setClickable(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make a request for the chart
                String selectedChartType = indicatorSpinner.getSelectedItem().toString();
                getChart(selectedChartType);
                changeButton.setClickable(false);//disable until a different indicator is selected;
                //Log.d("CHANGEBUTTON","IM CLICKING THE BUTTON");
            }
        });
        changeButton.setClickable(false);//just on the initial load


        //for the favorite button
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FAV","THIS SYMBOL IS MY FAV NOW");

                SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFERENCE_KEY,MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                String s = prefs.getString(stockSymbol,null);
                //Log.d("ISITNULL",(s==null)+"");

                if(prefs.getString(stockSymbol,null) == null){//not in favorites list yet
                    Map<String,String> currPrefs = (Map<String,String>)prefs.getAll();
                    int currNum = -1;
                    if(currPrefs.size() == 0){
                        currNum = 0;
                    }
                    else{
                        Set<String> keySet = currPrefs.keySet();
                        for(String key:keySet){
                            String objStr = currPrefs.get(key);
                            StockFavoriteModel sfm =stockCurrGson.fromJson(objStr, StockFavoriteModel.class);
                            if(sfm.getDefaultOrder() > currNum){
                                currNum = sfm.getDefaultOrder();
                            }
                        }
                        currNum+=1;
                    }
                    Log.d("CURRNUm",currNum+"");
                    StockFavoriteModel sfm = new StockFavoriteModel(stockSymbol,stockPrevClosePrice,stockChangeVal,stockChangePercent);
                    sfm.setDefaultOrder(currNum);//first entry will have number 0, next ones will have 1,2 ,etc..for relative ordering
                    String jsonString = stockCurrGson.toJson(sfm);
                    prefEditor.putString(stockSymbol,jsonString);
                    prefEditor.commit();
                    favButton.setImageResource(R.drawable.filled);
                    Log.d("FAVCLICK","FILL NOW");
                }

                else{
                    prefEditor.remove(stockSymbol);
                    prefEditor.commit();
                    favButton.setImageResource(R.drawable.empty);
                    Log.d("FAVCLICK","UNFILL NOW");
                }


            }
        });

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
//figure out the webview and work on the simpler fragments first
    private void getChart(String chartType){
        //sma,ema,stoch,rsi,adx,cci,bbands,macd

        /*if(stockChartProgressBar!= null) {
            showStockChart = true;
            stockChartProgressBar.setVisibility(View.VISIBLE);
        }*/
        showStockChart = true;


        String indicator = "";
        switch(chartType){
            case "Price":
                indicator = "quote";
                break;
            case "SMA":
                indicator = "sma";
                break;
            case "EMA":
                indicator = "ema";
                break;
            case "MACD":
                indicator = "macd";
                break;
            case "RSI":
                indicator = "rsi";
                break;
            case "ADX":
                indicator = "adx";
                break;
            case "CCI":
                indicator = "cci";
                break;
            case "STOCH":
                indicator = "stoch";
                break;
            case "BBANDS":
                indicator = "bbands";
                break;
            default:
                indicator = chartType;
                break;
        }
        //may need to remove previous js interfaces if we can't stack them
        if(!noJSInterfacesYet){
            chartWebView.removeJavascriptInterface("Android");
        }

        chartWebView.addJavascriptInterface(new JavaScriptInterface(indicator,stockSymbol),"Android");


        chartWebView.loadUrl(MainActivity.chartURL);
        if(noJSInterfacesYet) {
            noJSInterfacesYet = false;
        }

        //work on webview scrolling you scrub
    }

    //bleh make this an inner class
    class JavaScriptInterface{//stock details chart and the price request all use the same data so if our indicator is "quote" just pass the json in
        //otherwise make new requests
        //private Context mContext;
        private String indicatorString = null;
        private String stockSymbol = null;


        JavaScriptInterface(/*Context c,*/String indicator,String ss) {
            //mContext = c;

            this.indicatorString = indicator;
            this.stockSymbol = ss;

        }

        @JavascriptInterface
        public String getIndicatorString() {
            return indicatorString;
        }

        @JavascriptInterface
        public boolean hasIndicatorString(){
            return indicatorString != null;
        }

        @JavascriptInterface
        public String getStockSymbol() {
            return this.stockSymbol;
        }

        @JavascriptInterface
        public boolean hasStockSymbol(){
            return this.stockSymbol != null;
        }


        @JavascriptInterface
        public void hideProgressBar(){
            showStockChart = false;
        }
    };




}

