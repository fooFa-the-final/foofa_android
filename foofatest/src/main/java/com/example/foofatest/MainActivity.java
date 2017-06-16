package com.example.foofatest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.foofatest.Adapter.SearchTruckListAdapter;
import com.example.foofatest.Json.JsonParsingControl;
import com.example.foofatest.dto.Foodtruck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private SearchTruckListAdapter adapter;
    private List<Foodtruck> foodtrucks;
    private EditText key, loc;
    private Button go;
    private Foodtruck truck4search;
    private ListView list;
    private CheckBox card, drinking, parking, catering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner)findViewById(R.id.spinner_sort);
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(this, R.array.sort, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sAdapter);
        card = (CheckBox)findViewById(R.id.filter_card);
        drinking = (CheckBox)findViewById(R.id.filter_drinking);
        parking = (CheckBox)findViewById(R.id.filter_parking);
        catering = (CheckBox)findViewById(R.id.filter_catering);
        card.setOnCheckedChangeListener(this);
        drinking.setOnCheckedChangeListener(this);
        parking.setOnCheckedChangeListener(this);
        catering.setOnCheckedChangeListener(this);

        foodtrucks = new ArrayList<>();

        adapter = new SearchTruckListAdapter(this, foodtrucks);

        list = (ListView)findViewById(R.id.searchTruckList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TruckDetailActivity.class);
                intent.putExtra("foodtruck", foodtrucks.get(position));
                startActivity(intent);
            }
        });

        loc = (EditText)findViewById(R.id.searchLoc);
        loc.setText("Current Location");
        key = (EditText)findViewById(R.id.searchKey);

        go = (Button)findViewById(R.id.search_go_btn);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                truck4search = justBeforeSerch();
                HttpAsyncTask httpAsyncTask = new HttpAsyncTask(MainActivity.this);
                httpAsyncTask.execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/foodtruck/search.do", truck4search);
            }
        });

        list.setAdapter(adapter);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        foodtrucks.clear();
        truck4search = justBeforeSerch();
        HttpAsyncTask httpAsyncTask = new HttpAsyncTask(MainActivity.this);
        httpAsyncTask.execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/foodtruck/search.do", truck4search);
    }

    private class HttpAsyncTask extends AsyncTask<Object, Void, String>{
        private MainActivity mainActivity;

        public HttpAsyncTask(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        protected String doInBackground(Object... params) {
            Foodtruck foodtruck = (Foodtruck)params[1];

            return JsonParsingControl.POST((String)params[0], foodtruck);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Log.d("test", "onPostExecute: "+result);
            method(result);
            //Log.d("test", "onPostExecute: " + foodtrucks.size());
            list.invalidateViews();
            //adapter.notifyDataSetChanged();
        }
    }



    public void method(String data){
        Gson gson = new GsonBuilder().create();
        List<Foodtruck> trucks = new ArrayList<>();

        try {
            JsonParser jsonParser = new JsonParser();

            //JsonObject jsonObject = (JsonObject)jsonParser.parse(data);

            JsonArray jsonArray = (JsonArray)jsonParser.parse(data);
            for(int i = 0; i < jsonArray.size(); i++ ){
                Foodtruck foodtruck = gson.fromJson(jsonArray.get(i), Foodtruck.class);
                //Log.d("test", "method: "+foodtruck.toString());
                String img = foodtruck.getFoodtruckImg();
                Log.d("test", "method: "+img);
                foodtruck.setFoodtruckImg("http://10.0.2.2:8888/FoodtruckFinderProject/resources/img/food/"+img);
                foodtrucks.add(foodtruck);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //return trucks;
    }

    public Foodtruck justBeforeSerch(){

        truck4search = new Foodtruck();

        //truck4search.setFoodtruckName("양식");
        truck4search.setLocation("서울");
        /*truck4search.setFoodtruckName(key.getText().toString());
        truck4search.setLocation(key.getText().toString());*/
        if(card.isChecked()){
            truck4search.setCard(true);
        }
        if(drinking.isChecked()){
            truck4search.setDrinking(true);
        }
        if(parking.isChecked()){
            truck4search.setParking(true);
        }
        if(catering.isChecked()){
            truck4search.setCatering(true);
        }


     return truck4search;
    }

}
