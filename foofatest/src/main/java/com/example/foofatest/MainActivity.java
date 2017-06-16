package com.example.foofatest;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.foofatest.Adapter.SearchTruckListAdapter;
import com.example.foofatest.Json.JsonParsingControl;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.forMap.NMapPOIflagType;
import com.example.foofatest.forMap.NMapViewerResourceProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

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

public class MainActivity extends NMapActivity implements NMapView.OnMapStateChangeListener, CompoundButton.OnCheckedChangeListener, AbsListView.OnScrollListener {

    final private Geocoder geocoder = new Geocoder(MainActivity.this);
    private double lat = 0;
    private double lon = 0;
    private List<Double> lats;
    private List<Double> lons;
    private String loca = "";//트럭 주소 입력
    private List<String> locas;//트럭 주소들
    private List<Address> list_loc = new ArrayList<>();
    private NMapViewerResourceProvider mMapViewerResourceProvider = null;
    private NMapOverlayManager mOverlayManager;
    private NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = null;
    private NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener;
    // API-KEY
    public static final String API_KEY = "noUvsaR702FX6WH5un5h";  //<---맨위에서 발급받은 본인 ClientID 넣으세요.
    // 네이버 맵 객체
    NMapView mMapView = null;
    // 맵 컨트롤러
    NMapController mMapController = null;
    // 맵을 추가할 레이아웃
    LinearLayout truckLocation;
    private int markerId;

    ///////////////////////////////////////////////////////naver Map용 Field
    private SearchTruckListAdapter adapter;
    private List<Foodtruck> foodtrucks;
    private EditText key, loc;
    private Button go;
    private Foodtruck truck4search;
    private ListView list;
    private Spinner spinner;
    private CheckBox card, drinking, parking, catering, nowOpen;
    private boolean lastItemVisibleFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 네이버 지도를 넣기 위한 LinearLayout 컴포넌트
        truckLocation = (LinearLayout) findViewById(R.id.truckLocation);


        // 네이버 지도 객체 생성
        mMapView = new NMapView(this);

        // 지도 객체로부터 컨트롤러 추출
        mMapController = mMapView.getMapController();

        // 네이버 지도 객체에 APIKEY 지정
        mMapView.setApiKey(API_KEY);

        // 생성된 네이버 지도 객체를 LinearLayout에 추가시킨다.
        truckLocation.addView(mMapView);

        // 지도를 터치할 수 있도록 옵션 활성화
        mMapView.setClickable(true);

        // 확대/축소를 위한 줌 컨트롤러 표시 옵션 활성화
        mMapView.setBuiltInZoomControls(true, null);

        mMapView.setScalingFactor(2f);//맵 확대 레벨 업

        // 지도에 대한 상태 변경 이벤트 연결
        mMapView.setOnMapStateChangeListener(this);

//         create resource provider

        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
        markerId = NMapPOIflagType.PIN;
        mMapView.setOnMapViewTouchEventListener(new NMapView.OnMapViewTouchEventListener() {
            @Override
            public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onLongPressCanceled(NMapView nMapView) {

            }

            @Override
            public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

            }

            @Override
            public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://map.naver.com/index.nhn?enc=utf8&level=2&lng=" + lons.get(0) + "&lat=" + lats.get(0) + "&pinTitle=" + loca)));
            }
        });

///////////////////////////////////////naver map source

        spinner = (Spinner) findViewById(R.id.spinner_sort);
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(this, R.array.sort, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sAdapter);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                foodtrucks.clear();
                truck4search = justBeforeSerch();
                HttpAsyncTask httpAsyncTask = new HttpAsyncTask(MainActivity.this);
                httpAsyncTask.execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/foodtruck/search.do", truck4search);
            }
        });
        card = (CheckBox)findViewById(R.id.filter_card);
        drinking = (CheckBox)findViewById(R.id.filter_drinking);
        parking = (CheckBox)findViewById(R.id.filter_parking);
        catering = (CheckBox)findViewById(R.id.filter_catering);
        nowOpen = (CheckBox)findViewById(R.id.filter_nowOpen);
        card.setOnCheckedChangeListener(this);
        drinking.setOnCheckedChangeListener(this);
        parking.setOnCheckedChangeListener(this);
        catering.setOnCheckedChangeListener(this);
        nowOpen.setOnCheckedChangeListener(this);

        foodtrucks = new ArrayList<>();

        adapter = new SearchTruckListAdapter(this, foodtrucks);
        truck4search = new Foodtruck();

        list = (ListView) findViewById(R.id.searchTruckList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TruckDetailActivity.class);
                intent.putExtra("foodtruck", foodtrucks.get(position));
                startActivity(intent);
            }
        });

        list.setOnScrollListener(this);

        loc = (EditText) findViewById(R.id.searchLoc);
        loc.setText("Current Location");
        key = (EditText) findViewById(R.id.searchKey);

        go = (Button) findViewById(R.id.search_go_btn);
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
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {

    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {

    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        foodtrucks.clear();
        truck4search = justBeforeSerch();
        HttpAsyncTask httpAsyncTask = new HttpAsyncTask(MainActivity.this);
        httpAsyncTask.execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/foodtruck/search.do", truck4search);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
        //즉 스크롤이 바닥에 닿아 멈춘 상태에 처리를 하겠다는 뜻
        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            truck4search = justBeforeSerch();
            HttpAsyncTask httpAsyncTask = new HttpAsyncTask(MainActivity.this);
            httpAsyncTask.execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/foodtruck/search.do", truck4search);
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);

    }


    private class HttpAsyncTask extends AsyncTask<Object, Void, String> {
        private MainActivity mainActivity;

        public HttpAsyncTask(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        protected String doInBackground(Object... params) {
            Foodtruck foodtruck = (Foodtruck) params[1];

            return JsonParsingControl.POST((String) params[0], foodtruck);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Log.d("test", "onPostExecute: "+result);
            method(result);
            locas = new ArrayList<>();
            lats = new ArrayList<>();
            lons = new ArrayList<>();

            for (Foodtruck fd : foodtrucks) {
                loca = fd.getLocation();
                locas.add(loca);
            }


            NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
            poiData.beginPOIdata(2);
            for (String loc1 : locas) {
                try {
                    list_loc = geocoder.getFromLocationName(loc1, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                lat = list_loc.get(0).getLatitude();//위도
                lon = list_loc.get(0).getLongitude();//경도
                lats.add(lat);
                lons.add(lon);
                poiData.addPOIitem(lon, lat, loc1, markerId, 0);
            }
            poiData.endPOIdata();
            NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

            // poiDataOverlay.showAllPOIdata(0);
            poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

            //////////////////////////////////////////////////////

            //Log.d("test", "onPostExecute: " + foodtrucks.size());
            adapter.notifyDataSetChanged();
        }
    }


    public void method(String data) {
        Gson gson = new GsonBuilder().create();
        List<Foodtruck> trucks = new ArrayList<>();

        try {
            JsonParser jsonParser = new JsonParser();

            //JsonObject jsonObject = (JsonObject)jsonParser.parse(data);

            JsonArray jsonArray = (JsonArray) jsonParser.parse(data);

            for (int i = 0; i < jsonArray.size(); i++) {
                Foodtruck foodtruck = gson.fromJson(jsonArray.get(i), Foodtruck.class);
                //Log.d("test", "method: "+foodtruck.toString());
                String img = foodtruck.getFoodtruckImg();
                Log.d("test", "method: " + img);
                foodtruck.setFoodtruckImg("http://10.0.2.2:8888/FoodtruckFinderProject/resources/img/food/" + img);
                foodtrucks.add(foodtruck);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //return trucks;
    }

    public Foodtruck justBeforeSerch(){

        truck4search = new Foodtruck();

        truck4search.setFoodtruckName("양식");
        truck4search.setLocation("서울");
        /*truck4search.setFoodtruckName(key.getText().toString());
        truck4search.setLocation(loc.getText().toString());*/
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
        if(nowOpen.isChecked()){
            truck4search.setState(true);
        }

        int sortPostion = spinner.getSelectedItemPosition();

        if(sortPostion==1){
            truck4search.setFoodtruckId("reviewCount");
        } else if(sortPostion==2){
            truck4search.setFoodtruckId("favoriteCount");
        }


        return truck4search;
    }

}