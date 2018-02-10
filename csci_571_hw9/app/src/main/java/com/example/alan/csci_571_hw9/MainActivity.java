package com.example.alan.csci_571_hw9;

//move on to other parts and do autocomplete later
//figure out this autocomplete poopoo :(
//need to fix some hw8 stuff but keep the original link up and make a new one with only certain stuff changed (hw8 isn't done grading yet)

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.file.Paths.get;

public class MainActivity extends AppCompatActivity {
    //list of components we'll need
    public static String autocompleteURL = "http://hazel-torus-194807.appspot.com/autocomplete";
    public static String quoteURL = "http://hazel-torus-194807.appspot.com/quote";
    public static String newsURL = "http://hazel-torus-194807.appspot.com/news";
    public static String quoteRefreshURL = "https://hazel-torus-194807.appspot.com/quoteRefresh";
    public static String chartURL = "file:///android_asset/chart.html";
    public static String historicalchartURL = "file:///android_asset/historicalchart.html";
    public static String JSONOBJECT = "JSONOBJECT";
    public static String STOCKSYMBOL = "STOCKSYMBOL";
    public static String PREFERENCE_KEY = "csci_571_hw9_fall_2017";
    public static int autocompleteThreshold = 1;

    private int AUTOCOMPLETELIMIT = 5;

    private Gson mainActivityGson = null;


    private AutoCompleteTextView autoTextView = null;
    private ArrayList<String> autoArrayList = null;
    private ArrayAdapter<String> autoArrayAdapter = null;
    //private AutoCompleteAdapter autoCompleteAdapter = null;
    private TextView quoteTextView = null;
    private TextView clearTextView = null;
    private Switch autoRefreshSwitch = null;
    private ImageButton refreshButton = null;

    private Spinner sortFieldSpinner = null;
    private Spinner sortOrderSpinner = null;
    private String[] sortFieldArray = new String[]{"Sort by","Default","Symbol","Price","Change"};
    private String[] sortOrderArray = new String[]{"Order","Ascending","Descending"};


    private ListView favoriteQuoteListView = null;
    private ProgressBar mainProgressBar = null;

    private StockFavoriteAdapter stockFavoriteAdapter = null;
    private ArrayList<StockFavoriteModel> stockFavoriteArrayList = null;

    private TextView mainErrorTextView = null;

    //WHY IS AUTOCOMPLETE NOT WORKINGGGG
    private void initComponents(){
        mainActivityGson = new Gson();

        mainErrorTextView = (TextView)findViewById(R.id.main_error_textview);
        mainErrorTextView.setVisibility(View.GONE);

        autoTextView = (AutoCompleteTextView)findViewById(R.id.stock_textview);

        autoTextView.setAdapter(autoArrayAdapter);
        autoTextView.setThreshold(autocompleteThreshold);

        /*autoCompleteAdapter = new AutoCompleteAdapter(MainActivity.this,android.R.layout.simple_dropdown_item_1line);
        autoTextView.setAdapter(autoCompleteAdapter);
        */
        quoteTextView = (TextView)findViewById(R.id.quote_textview);
        clearTextView = (TextView)findViewById(R.id.clear_textview);
        autoRefreshSwitch = (Switch)findViewById(R.id.autorefresh_switch);
        refreshButton = (ImageButton)findViewById(R.id.refresh_button);

        sortFieldSpinner = (Spinner)findViewById(R.id.sort_field_spinner);
        sortOrderSpinner = (Spinner)findViewById(R.id.sort_order_spinner);
        //fill the spinners with options
        final List<String> sortFieldList = new ArrayList<>(Arrays.asList(sortFieldArray));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,sortFieldList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sortFieldSpinner.setAdapter(spinnerArrayAdapter);


        final List<String> sortOrderList = new ArrayList<>(Arrays.asList(sortOrderArray));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerOrderArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,sortOrderList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerOrderArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sortOrderSpinner.setAdapter(spinnerOrderArrayAdapter);

        favoriteQuoteListView = (ListView)findViewById(R.id.favoritequote_listview);
        //comment this out later but for now make the list view invisible
        favoriteQuoteListView.setVisibility(View.GONE);


        mainProgressBar = (ProgressBar)findViewById(R.id.main_progressbar);
        mainProgressBar.setVisibility(View.GONE);

        //fill the favorites list if it exists
        if(stockFavoriteArrayList == null){
            stockFavoriteArrayList = new ArrayList<StockFavoriteModel>();

            SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENCE_KEY,MODE_PRIVATE);
            //let's fill the list
            Map<String,String> quoteSet = (Map<String, String>) prefs.getAll();

            //if(quoteSet.size() > 0) {
                Set<String> keySet = quoteSet.keySet();
                for (String key : keySet) {
                    String objStr = quoteSet.get(key);
                    StockFavoriteModel sfm = mainActivityGson.fromJson(objStr, StockFavoriteModel.class);
                    stockFavoriteArrayList.add(sfm);
                }
            //}

            //by default sort the stocks by the order we add them
            Collections.sort(stockFavoriteArrayList,new DefaultOrderComparator());
        }


        if(stockFavoriteAdapter == null){
            stockFavoriteAdapter = new StockFavoriteAdapter(stockFavoriteArrayList,this);
            favoriteQuoteListView.setAdapter(stockFavoriteAdapter);
            registerForContextMenu(favoriteQuoteListView);
            if(!stockFavoriteArrayList.isEmpty()){
                favoriteQuoteListView.setVisibility(View.VISIBLE);
                refreshSymbols();//refresh each time we load according to what the specs want :|
            }
        }

    }

    private void refreshSymbols()
    {
        for(int i=0;i<stockFavoriteArrayList.size();++i)
        {
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, MainActivity.quoteRefreshURL+"?searchIdx="+i+"&searchSymbol="+
                            stockFavoriteArrayList.get(i).getStockName(),
                            null,new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONObject quoteJSON = response;

                                JSONObject timeSeriesData = quoteJSON.getJSONObject("Time Series (Daily)");
                                int arrayIdx = quoteJSON.getInt("SEARCHIDX");
                                JSONObject stockRecentQuote = null;
                                JSONObject stockPrevRecentQuote = null;
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

                                double stockChangeVal = stockRecentQuote.getDouble("4. close")-stockPrevRecentQuote.getDouble("4. close");
                                double stockChangePercent = stockChangeVal/(stockPrevRecentQuote.getDouble("4. close"))*100;
                                String stockTimeStamp = quoteJSON.getString("TimeStamp");
                                //Log.d("TIMESTAMP",stockTimeStamp);
                                String currDate = stockTimeStamp.substring(0,stockTimeStamp.indexOf(" "));
                                //int currHr = Integer.valueOf(stockTimeStamp.substring(stockTimeStamp.indexOf(" ")+1,stockTimeStamp.indexOf(":")));
                                double stockClosePrice = stockRecentQuote.getDouble("4. close");
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

                                StockFavoriteModel sfm = stockFavoriteArrayList.get(arrayIdx);
                                sfm.setStockPrice(stockRecentQuote.getDouble("4. close"));
                                sfm.setStockChangePct(stockChangePercent);
                                sfm.setStockChangeVal(stockChangeVal);
                                //update the shared preferences too

                                SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENCE_KEY,MODE_PRIVATE);
                                SharedPreferences.Editor prefEditor = prefs.edit();
                                String objStr = mainActivityGson.toJson(sfm);
                                prefEditor.putString(sfm.getStockName(),objStr);
                                prefEditor.commit();



                                Log.d("REFRESH","SHOULDA REFRESHED");
                                stockFavoriteAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {//consider more complex error handling later

                                Log.d("ERROR","JSON PARSE ERROR");
                                e.printStackTrace();

                            }

                        }
                    },
                            new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {//eh figure out how to display the error message later
                                    error.printStackTrace();
                                    Log.d("REFRESHERROR","I HAVE A REFRESH ERROR");

                                }
                            });
            Volley.newRequestQueue(MainActivity.this).add(jsonRequest);
        }
    }


    private void initListeners(){
        clearTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CLEARTEXTVIEW","I CLEARED THE VIEW");
                autoTextView.setText("");
            }
        });

        //will this work???
        favoriteQuoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                StockFavoriteModel sfm = (StockFavoriteModel)adapterView.getItemAtPosition(i);
                String symbol = sfm.getStockName();
                //if we click on an item we should load the corresponding page
                Intent intent = new Intent(MainActivity.this,StockDetailsActivity.class);
                intent.putExtra(STOCKSYMBOL,symbol);
                startActivity(intent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("NEED TO REFRESH","REFRESH THE VIEWS");
                //loop through all preferences and make the volley requests i guess
                refreshSymbols();
            }
        });

        autoRefreshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.d("CHECKED",isChecked+"");
                if(isChecked){

                }
                else{

                }
            }

        });

        quoteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String autoText = autoTextView.getText().toString().toUpperCase();
                //Toast.makeText(MainActivity.this,autoText,Toast.LENGTH_SHORT).show();

                if(autoText.isEmpty() || autoText.matches("")){
                    Toast.makeText(MainActivity.this,"Please enter a stock name or symbol",Toast.LENGTH_SHORT).show();
                }
                else{//make the request to json
                    Intent i = new Intent(MainActivity.this,StockDetailsActivity.class);
                    String queryString = autoText;

                    if(autoText.indexOf("-") != -1) {
                        queryString = autoText.substring(0, autoText.indexOf("-") - 1);
                    }
                    //Log.d("QUERYSTRING",queryString);
                    i.putExtra(STOCKSYMBOL,queryString);
                    startActivity(i);
                }
            }
        });

        autoTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//need to make a new dropdown and underlying array everytime -.-
                //only display autocomplete options if the text view is not empty
                mainErrorTextView.setVisibility(View.GONE);
                favoriteQuoteListView.setVisibility(View.VISIBLE);


                //String lastChar = s.toString().substring(s.length()-1);


                if(s.length() > 0){
                    mainProgressBar.setVisibility(View.VISIBLE);
                    JsonArrayRequest jsonRequest = new JsonArrayRequest
                      (Request.Method.GET, autocompleteURL+"?query="+s.toString().toUpperCase(),
                        null,new Response.Listener<JSONArray>() {
                         @Override
                         public void onResponse(JSONArray response) {
                             Log.d("JSONRESPONSE",response.toString());
                             mainProgressBar.setVisibility(View.GONE);

                             autoArrayList = new ArrayList<String>();
                         //populate autocomplete view with the json result
                             int limit = Math.min(response.length(),AUTOCOMPLETELIMIT);

                          for(int i=0;i<limit;++i){
                              try {
                                  JSONObject obj = response.getJSONObject(i);
                                  String msg = obj.getString("Symbol")+ " - " + obj.getString("Name")
                                          + " ("+obj.getString("Exchange")+")";
                                  Log.d("MSG",msg);
                                  //autoArrayList.add(msg);
                                  autoArrayList.add(msg);
                                  //autoArrayAdapter.notifyDataSetChanged();

                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }

                          }
                             autoArrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_dropdown_item_1line,
                                     autoArrayList);
                             autoTextView.setAdapter(autoArrayAdapter);
                             autoArrayAdapter.notifyDataSetChanged();

                          //Log.d("ARRAYLISTCOUNT",autoArrayList.size()+"");
                          //autoArrayAdapter.notifyDataSetChanged();
                         }
                      },
                      new Response.ErrorListener() {

                      @Override
                      public void onErrorResponse(VolleyError error) {//eh figure out how to display the error message later
                      error.printStackTrace();
                      mainErrorTextView.setVisibility(View.VISIBLE);
                          favoriteQuoteListView.setVisibility(View.GONE);
                          mainProgressBar.setVisibility(View.GONE);
                      }
                    });
                   Volley.newRequestQueue(MainActivity.this).add(jsonRequest);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("BEFORECHANGE",s.length()+"");

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("AFTERCHANGE","WHEN IS THIS BEING CALLED");
                Log.d("AFTERCHANGE LEN",s.toString().length()+"");



            }
        });





        sortFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//only fires if different item is selected
                // String selectedIndicator = adapterView.getItemAtPosition(i).toString();
               String text = adapterView.getItemAtPosition(i).toString();
               boolean ascending = false;
               Log.d("SORTORDER",sortOrderSpinner.getSelectedItem().toString());

               if(sortOrderSpinner.getSelectedItem().toString() != "Descending"){
                   ascending = true;
               }

                switch(text){
                    case "Default":
                        Collections.sort(stockFavoriteArrayList,new DefaultOrderComparator());
                        if(!ascending){
                            Collections.reverse(stockFavoriteArrayList);
                        }
                        break;
                    case "Symbol":
                        Collections.sort(stockFavoriteArrayList,new SymbolComparator());
                        if(!ascending){
                            Collections.reverse(stockFavoriteArrayList);
                        }
                        break;
                    case "Price":
                        Collections.sort(stockFavoriteArrayList,new PriceComparator());
                        if(!ascending){
                            Collections.reverse(stockFavoriteArrayList);
                        }
                        break;
                    case "Change":
                        Collections.sort(stockFavoriteArrayList,new ChangeComparator());
                        if(!ascending){
                            Collections.reverse(stockFavoriteArrayList);
                        }
                        break;
                    default:
                        break;
                }


                stockFavoriteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sortOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//only fires if different item is selected
                // String selectedIndicator = adapterView.getItemAtPosition(i).toString();
                String text = sortFieldSpinner.getSelectedItem().toString();
                boolean ascending = false;
                Log.d("SORTORDER",sortOrderSpinner.getSelectedItem().toString());

                if(sortOrderSpinner.getSelectedItem().toString() != "Descending"){
                    ascending = true;
                }

                switch(text){
                    case "Default":
                        Collections.sort(stockFavoriteArrayList,new DefaultOrderComparator());
                        if(!ascending){
                            Collections.reverse(stockFavoriteArrayList);
                        }
                        break;
                    case "Symbol":
                        Collections.sort(stockFavoriteArrayList,new SymbolComparator());
                        if(!ascending){
                            Collections.reverse(stockFavoriteArrayList);
                        }
                        break;
                    case "Price":
                        Collections.sort(stockFavoriteArrayList,new PriceComparator());
                        if(!ascending){
                            Collections.reverse(stockFavoriteArrayList);
                        }
                        break;
                    case "Change":
                        Collections.sort(stockFavoriteArrayList,new ChangeComparator());
                        if(!ascending){
                            Collections.reverse(stockFavoriteArrayList);
                        }
                        break;
                    default:
                        break;
                }


                stockFavoriteAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize all the app components we need
        initComponents();
        initListeners();
        //Log.d("ONCREATE","CREATEDEH");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,//menu that shows up when we long press the favorite list
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.add(0,v.getId(),0,"Remove from Favorites?");
        menu.getItem(0).setEnabled(false);
        menu.add(0,v.getId(),0,"No");
        menu.add(0,v.getId(),0,"Yes");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo  info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String itemOption = item.getTitle().toString();
        Log.d("CONDITION",itemOption.equals("Yes")+"");
        if(itemOption.equals("Yes")) {

            int pos = info.position;
            //Log.d("POS", pos + "");
            StockFavoriteModel sfm = stockFavoriteAdapter.getItem(pos);
            String symbol = sfm.getStockName();
            SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENCE_KEY,MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = prefs.edit();
            prefEditor.remove(symbol);
            prefEditor.commit();
            stockFavoriteAdapter.remove(sfm);
            stockFavoriteAdapter.notifyDataSetChanged();

        }
        return super.onContextItemSelected(item);

    }
/*
    class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable{
        private ArrayList<String> stockSymbolList = null;

        public AutoCompleteAdapter(Context context,int id){
            super(context,id);
            stockSymbolList = new ArrayList<String>();
        }

        public int getCount(){return stockSymbolList.size();}

        public String getItem(int index){return stockSymbolList.get(index);}

        //double check this filter in the morning
        public Filter getFilter(){
            Filter filter = new Filter(){
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults filterResults = new FilterResults();
                    if(charSequence!=null){//make the volley request
                        Log.d("CHARSEQ",charSequence.toString());
                        //mainProgressBar.setVisibility(View.VISIBLE);
                        JsonArrayRequest jsonRequest = new JsonArrayRequest
                                (Request.Method.GET, autocompleteURL+"?query="+charSequence.toString().toUpperCase(),
                                        null,new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Log.d("JSONRESPONSE",response.toString());
                                        //mainProgressBar.setVisibility(View.GONE);

                                        //populate autocomplete view with the json result

                                        Log.d("RESPONSE AREA LEN",response.length() + "");
                                        if(!stockSymbolList.isEmpty()){//new list every time
                                            stockSymbolList.clear();
                                        }

                                        for(int i=0;i<response.length();++i){
                                            try {
                                                JSONObject obj = response.getJSONObject(i);
                                                String msg = obj.getString("Symbol")+ " - " + obj.getString("Name")
                                                        + " ("+obj.getString("Exchange")+")";
                                                Log.d("MSG",msg);
                                                stockSymbolList.add(msg);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    }
                                },
                                        new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {//eh figure out how to display the error message later
                                                error.printStackTrace();
                                                Log.d("ERRORTAG","ERROR IN GETTING JSON");
                                            }
                                        });
                        Volley.newRequestQueue(MainActivity.this).add(jsonRequest);
                        filterResults.values = stockSymbolList;
                        filterResults.count = stockSymbolList.size();
                        notifyDataSetChanged();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    if(filterResults!=null && filterResults.count > 0){
                        Log.d("PUBLISHRESULTS","CHANGED");
                        notifyDataSetChanged();
                    }
                    else{
                        Log.d("PUBLISHRESULTS","INVALIDATED");
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

    }*/


    //bunch of inner comparator classes
    class DefaultOrderComparator implements Comparator<StockFavoriteModel> {//is ascending by default..need to manually reverse if we need descending


        @Override
        public int compare(StockFavoriteModel o1, StockFavoriteModel o2) {

            if(o1.getDefaultOrder() < (o2.getDefaultOrder())){
                return -1;
            }

            if(o1.getDefaultOrder() > (o2.getDefaultOrder())){
                return 1;
            }

            return 0;
        }
    }


    class SymbolComparator implements Comparator<StockFavoriteModel> {//is ascending by default..need to manually reverse if we need descending


        @Override
        public int compare(StockFavoriteModel o1, StockFavoriteModel o2) {

            return o1.getStockName().compareTo(o2.getStockName());
        }
    }

    class PriceComparator implements Comparator<StockFavoriteModel> {//is ascending by default..need to manually reverse if we need descending


        @Override
        public int compare(StockFavoriteModel o1, StockFavoriteModel o2) {

            if(o1.getStockPrice() < (o2.getStockPrice())){
                return -1;
            }

            if(o1.getStockPrice() > (o2.getStockPrice())){
                return 1;
            }

            return 0;
        }
    }

    class ChangeComparator implements Comparator<StockFavoriteModel> {//is ascending by default..need to manually reverse if we need descending


        @Override
        public int compare(StockFavoriteModel o1, StockFavoriteModel o2) {

            if(o1.getStockChangeVal() < (o2.getStockChangeVal())){
                return -1;
            }

            if(o1.getStockChangeVal() > (o2.getStockChangeVal())){
                return 1;
            }

            return 0;
        }
    }
}



