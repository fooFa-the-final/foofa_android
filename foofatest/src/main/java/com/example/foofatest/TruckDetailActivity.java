package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.foofatest.Adapter.FoodtruckDetailAdapter;
import com.example.foofatest.Adapter.FoodtruckDetailMenuAdapter;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Menu;
import com.example.foofatest.forMap.NMapPOIflagType;
import com.example.foofatest.forMap.NMapViewerResourceProvider;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TruckDetailActivity extends NMapActivity implements NMapView.OnMapStateChangeListener {
//
//    final private Geocoder geocoder = new Geocoder(TruckDetailActivity.this);
//    private double lat = 0;
//    private double lon = 0;
//    private String loca = "";//트럭 주소 입력
//    private List<Address> list = new ArrayList<>();
//    private NMapViewerResourceProvider mMapViewerResourceProvider = null;
//    private NMapOverlayManager mOverlayManager;
//    private NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = null;
//    private NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener;
//    // API-KEY
//    public static final String API_KEY = "noUvsaR702FX6WH5un5h";  //<---맨위에서 발급받은 본인 ClientID 넣으세요.
//    // 네이버 맵 객체
//    NMapView mMapView = null;
//    // 맵 컨트롤러
//    NMapController mMapController = null;
//    // 맵을 추가할 레이아웃
//   LinearLayout truckLocation;

    ///////////////////////////////////////////////////////naver Map용 Field
    private SharedPreferences prefs;
    private String loginUserId;
    private List<Foodtruck> foodtrucks;
    private Foodtruck foodtruck1;
    private List<Menu> menus1;
    private FoodtruckDetailMenuAdapter foodtruckDetailMenuAdapter;
    private FoodtruckDetailAdapter adapter;
    private String sellerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_detail);
//
//        // 네이버 지도를 넣기 위한 LinearLayout 컴포넌트
//      truckLocation = (LinearLayout) findViewById(R.id.truckLocation);
//
//        // 네이버 지도 객체 생성
//        mMapView = new NMapView(this);
//
//        // 지도 객체로부터 컨트롤러 추출
//        mMapController = mMapView.getMapController();
//
//        // 네이버 지도 객체에 APIKEY 지정
//        mMapView.setApiKey(API_KEY);
//
//        // 생성된 네이버 지도 객체를 LinearLayout에 추가시킨다.
//      truckLocation.addView(mMapView);
//
//        // 지도를 터치할 수 있도록 옵션 활성화
//        mMapView.setClickable(true);
//
//        // 확대/축소를 위한 줌 컨트롤러 표시 옵션 활성화
//        mMapView.setBuiltInZoomControls(true, null);
//
//        mMapView.setScalingFactor(2f);//맵 확대 레벨 업
//
//        // 지도에 대한 상태 변경 이벤트 연결
//        mMapView.setOnMapStateChangeListener(this);
//
////         create resource provider
//
//        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
//
//        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
//
//        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);

        int markerId = NMapPOIflagType.PIN;

        Intent intent = getIntent();
        foodtruck1 = (Foodtruck) intent.getExtras().get("foodtruck");
        foodtruck1.setMenus(menus1);
//        loca = foodtruck1.getLocation();

//        try {
//            list = geocoder.getFromLocationName(loca, 10);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        lat = list.get(0).getLatitude();//위도
//        lon = list.get(0).getLongitude();//경도

//        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
//        poiData.beginPOIdata(2);
//        poiData.addPOIitem(lon, lat, "here", markerId, 0);    //요기 좌표 입력해주면, 그 좌표가 표시됩니다.
//        poiData.endPOIdata();
//        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
//
//        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
//        mMapView.setOnMapViewTouchEventListener(new NMapView.OnMapViewTouchEventListener() {
//
//            @Override
//            public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {
//
//            }
//
//            @Override
//            public void onLongPressCanceled(NMapView nMapView) {
//
//            }
//
//            @Override
//            public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {
//
//            }
//
//            @Override
//            public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {
//
//            }
//
//            @Override
//            public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {
//
//            }
//
//            @Override
//            public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://map.naver.com/index.nhn?enc=utf8&level=2&lng=" + lon + "&lat=" + lat + "&pinTitle="+loca)));
//            }
//        });

//
//
//        /////////////////////////////////////////////////naverMap용 source


        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        loginUserId = prefs.getString("loginId", "");


        intent = getIntent();
        foodtruck1 = (Foodtruck) intent.getExtras().get("foodtruck");
        sellerId = (String) intent.getExtras().get("foodtruckId");

        foodtruck1.setMenus(menus1);

        Log.d("1111", sellerId);
        Log.d("1111", foodtruck1.getFoodtruckId());


        foodtrucks = new ArrayList<>();
        final ListView list = (ListView) findViewById(R.id.truckDetail);


        new FoodtruckDetailTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/detail.do?id=" + sellerId);
        adapter = new FoodtruckDetailAdapter(this, foodtrucks);
        list.setAdapter(adapter);


        menus1 = new ArrayList<>();
        final ListView menulist = (ListView) findViewById(R.id.truckInfoMenu1);

        new MenuDetailTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/menu/detail.do?id=" + sellerId);
        foodtruckDetailMenuAdapter = new FoodtruckDetailMenuAdapter(this, menus1);

        menulist.setAdapter(foodtruckDetailMenuAdapter);
        Log.d("1111", String.valueOf(menus1.size()));

    }

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
//        if (nMapError == null) { // success
//            mMapController.setMapCenter(//지도 출력시 트럭위치를 중앙으로
//                    new NGeoPoint(lon, lat), 11);
//        } else { // fail
//            android.util.Log.e("NMAP", "onMapInitHandler: error="
//                    + nMapError.toString());
//        }
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

    public class FoodtruckDetailTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String) params[0]);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));
                NodeList nodeList = doc.getElementsByTagName("foodtruck");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Foodtruck foodtruck = new Foodtruck();
                    Node node = nodeList.item(i);
                    Element element = (Element) node;
                    foodtruck.setFoodtruckId(getTagValue("foodtruckId", element));
                    foodtruck.setSellerId(getTagValue("sellerId", element));
                    foodtruck.setFoodtruckName(getTagValue("foodtruckName", element));
                    foodtruck.setOperationTime(getTagValue("operationTime", element));
                    foodtruck.setSpot(getTagValue("spot", element));
                    foodtruck.setNotice(getTagValue("notice", element));
                    foodtruck.setLocation(getTagValue("location", element));
                    foodtruck.setCategory1(getTagValue("category1", element));
                    foodtruck.setCategory2(getTagValue("category2", element));
                    foodtruck.setCategory3(getTagValue("category3", element));
                    foodtruck.setCard(Boolean.parseBoolean(getTagValue("card", element)));
                    foodtruck.setParking(Boolean.parseBoolean(getTagValue("parking", element)));
                    foodtruck.setDrinking(Boolean.parseBoolean(getTagValue("drinking", element)));
                    foodtruck.setCatering(Boolean.parseBoolean(getTagValue("catering", element)));
                    foodtruck.setState(Boolean.parseBoolean(getTagValue("state", element)));
                    foodtruck.setFavoriteCount(Integer.parseInt(getTagValue("favoriteCount", element)));
                    foodtruck.setReviewCount(Integer.parseInt(getTagValue("reviewCount", element)));
                    foodtruck.setScore(Double.parseDouble(getTagValue("score", element)));
//                    List<Menu> menus1 = new ArrayList<>();
//                    NodeList list1 = element.getElementsByTagName("menus").item(i).getChildNodes();
//                    Log.d("1111", String.valueOf(list1.getLength()));
//                    int k = list1.getLength();
//
//                    for (int a = 0; a <= k; a++) {
//                        Menu menu = new Menu();
//                        menu.setMenuName(getTagValue("menuName", element));
//                        menu.setPrice(Integer.parseInt(getTagValue("favoriteCount", element)));
//                        menu.setMenuState(Boolean.parseBoolean(getTagValue("menuState", element)));
//                        menu.setMenuId(getTagValue("menuId", element));
//                        menus1.add(menu);
//                    }
//                    foodtruck.setMenus(menus1);
                    foodtruck.setFoodtruckImg("http://foofa.crabdance.com:8888/FoodtruckFinderProject/resources/img/food/" + getTagValue("foodtruckImg", element));
                    foodtrucks.add(foodtruck);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }


    private static String getTagValue(String tag, Element element) {
        try {
            NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node value = (Node) list.item(0);
            return value.getNodeValue();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return "";
    }


    public class MenuDetailTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String) params[0]);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));
                NodeList nodeList = doc.getElementsByTagName("menu");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Menu menu = new Menu();
                    Node node = nodeList.item(i);
                    Element element = (Element) node;
                    menu.setMenuId(getTagValue("menuId", element));
                    menu.setFoodtruckId(getTagValue("foodtruckId", element));
                    menu.setMenuName(getTagValue("menuName", element));
                    menu.setPrice(Integer.parseInt(getTagValue("price", element)));
                    menu.setMenuState(Boolean.parseBoolean(getTagValue("menuState", element)));
                    menus1.add(menu);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            foodtruckDetailMenuAdapter.notifyDataSetChanged();
        }
    }


}
