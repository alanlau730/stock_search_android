package com.example.alan.csci_571_hw9;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockHistoryFragment extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "stock_history_section_number";
    private WebView historyWebView = null;
    private boolean noJSInterfacesYet = true;
    private String stockSymbol = null;

    public StockHistoryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static StockHistoryFragment newInstance(int sectionNumber,String symbolString) {
        StockHistoryFragment fragment = new StockHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(MainActivity.STOCKSYMBOL,symbolString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_details_history, container, false);
        initComponents(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d("ACTIVITY","CREATED?");
        //may need to remove previous js interfaces if we can't stack them
        if(!noJSInterfacesYet){
            historyWebView.removeJavascriptInterface("Android");
        }

        historyWebView.getSettings().setJavaScriptEnabled(true);
        historyWebView.getSettings().setDomStorageEnabled(true);
        historyWebView.addJavascriptInterface(new JSInterface(stockSymbol),"Android");


        historyWebView.loadUrl(MainActivity.historicalchartURL);
        if(noJSInterfacesYet) {
            noJSInterfacesYet = false;
        }
        historyWebView.setVisibility(View.VISIBLE);

    }
    private void initComponents(View rootView){
        historyWebView = (WebView)rootView.findViewById(R.id.historicalchart_webview);


        stockSymbol = getArguments().getString(MainActivity.STOCKSYMBOL);
        Log.d("HISTORYSYMBOL",stockSymbol);

    }


    class JSInterface{//stock details chart and the price request all use the same data so if our indicator is "quote" just pass the json in
        //otherwise make new requests
        //private Context mContext;

        private String stockSymbol = null;


        JSInterface(/*Context c,*/String ss) {
            //mContext = c;
            this.stockSymbol = ss;
            //Log.d("JSINTERFACE",stockSymbol);
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

        }
    }


}
