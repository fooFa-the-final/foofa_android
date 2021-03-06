package com.example.foofatest;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foofatest.Adapter.FoodtruckDetailAdapter;
import com.example.foofatest.Adapter.FoodtruckDetailMenuAdapter;
import com.example.foofatest.Adapter.TruckReviewAdapter;
import com.example.foofatest.Json.JsonParsingControl;
import com.example.foofatest.dto.Advertise;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Image;
import com.example.foofatest.dto.Member;
import com.example.foofatest.dto.Menu;
import com.example.foofatest.dto.Review;
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
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.DataFormatException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.example.foofatest.R.id.menu;
import static com.example.foofatest.R.id.truckLocation;


public class TruckInfoActivity extends NMapActivity implements NMapView.OnMapStateChangeListener {

    final private Geocoder geocoder = new Geocoder(TruckInfoActivity.this);
    private double lat = 0;
    private double lon = 0;
    private String loca = "";//트럭 주소 입력
    private List<Address> list = new ArrayList<>();
    private NMapViewerResourceProvider mMapViewerResourceProvider = null;
    private NMapOverlayManager mOverlayManager;
    private NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = null;

    ///////////////////////////////////////////////////////naver Map용 Field
    private NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener;
    // API-KEY
    public static final String API_KEY = "noUvsaR702FX6WH5un5h";  //<---맨위에서 발급받은 본인 ClientID 넣으세요.
    // 네이버 맵 객체
    NMapView mMapView = null;
    // 맵 컨트롤러
    NMapController mMapController = null;
    // 맵을 추가할 레이아웃
    LinearLayout truckLocation;


    private SharedPreferences prefs;
    private Button truckStatus;
    private LayoutInflater inflater;
    private String loginUserId;
    private FoodtruckDetailAdapter adapter;
    private TruckReviewAdapter truckReviewAdapter;
    private FoodtruckDetailMenuAdapter foodtruckDetailMenuAdapter;
    private TextView truckName, truckCategory, truckArea, truckFavorite, truckReviewCount, truckNotice, truckHours, truckCard, truckAlchol, truckParking, truckCatering;

    private ImageView image;
    private RatingBar ratingBar;

    private Foodtruck foodtruck1;
    private List<Menu> menus1;

    private List<Review> reviews;
    private Button changeBtn;
    private List<Foodtruck> foodtrucks;


    private java.sql.Date startDay1;

    private DatePicker mdate;
    private TextView advtext;
    private RadioGroup radioGroup;
    private Date startDay;
    private String period;
    private int realDay;
    private String sellerId;
    private Advertise advertise;
    /////////////////////////////truck close 용 field
    public static final String TAG = "Test_Alert_Dialog";
    private long now;//종료시의 시간을 받아온다.
    private Date date;//시간을 date 형태로 변환

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_seller);


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

        int markerId = NMapPOIflagType.PIN;
        Intent intent = getIntent();
        foodtruck1 = (Foodtruck) intent.getExtras().get("foodtruck");

        loca = foodtruck1.getLocation();
        try {
            list = geocoder.getFromLocationName(loca, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (loca.equals(null)) {//영업전이면 위경도 안 받아옴

        } else {
            lat = list.get(0).getLatitude();//위도
            lon = list.get(0).getLongitude();//경도
        }


        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poiData.beginPOIdata(2);
        if (lat != 0 && lon != 0) {
            poiData.addPOIitem(lon, lat, "here", markerId, 0);    //요기 좌표 입력해주면, 그 좌표가 표시됩니다.
        }
        poiData.endPOIdata();
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        // poiDataOverlay.showAllPOIdata(0);
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
//
//
//        /////////////////////////////////////////////////naverMap용 source


        Log.d("1111", foodtruck1.toString());
//        String truck = foodtruck.getFoodtruckName();
//        Log.d("1111", truck);


        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1);
        LocalActivityManager oo = new LocalActivityManager(getParent(), true);
        tabHost1.setup(oo);

        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.truckInfo1);
        ts1.setIndicator("INFO");
        tabHost1.addTab(ts1);
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.truckReview);
        ts2.setIndicator("리뷰");
        tabHost1.addTab(ts2);
        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3");
        ts3.setContent(R.id.truckAdvertise);
        ts3.setIndicator("광고신청");
        tabHost1.addTab(ts3);

        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        loginUserId = prefs.getString("loginId", "");


        Log.d(loginUserId, "id는 뭔가여");

        findViewById(R.id.truckChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInfoActivity.this, TruckOpenActivity.class);
                startActivity(intent);
            }
        });

        foodtrucks = new ArrayList<>();
        final ListView list = (ListView) findViewById(R.id.truckDetail);

        new FoodtruckDetailTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/detail.do?id=" + loginUserId);
        adapter = new FoodtruckDetailAdapter(this, foodtrucks);
        list.setAdapter(adapter);

        reviews = new ArrayList<>();
        truckReviewAdapter = new TruckReviewAdapter(this, reviews);

        new ReviewDetialTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/review/list/turck.do?id=" + loginUserId);
        final ListView reviewlist = (ListView) findViewById(R.id.truckReviewListlist);
        reviewlist.setAdapter(truckReviewAdapter);

        menus1 = new ArrayList<>();
        final ListView menulist = (ListView) findViewById(R.id.truckInfoMenu1);
        new MenuDetailTask().execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/menu/detail.do?id=" + loginUserId);
        foodtruckDetailMenuAdapter = new FoodtruckDetailMenuAdapter(this, menus1);
        menulist.setAdapter(foodtruckDetailMenuAdapter);
        Log.d("1111", String.valueOf(menus1.size()));

        TextView text = (TextView) findViewById(R.id.truckChange);
        changeBtn = (Button) findViewById(R.id.truckChange);


        Button button = (Button) findViewById(R.id.truckMenuModify);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInfoActivity.this, TruckOpenActivity.class);
                foodtrucks.get(0).setMenus(menus1);
                intent.putExtra("foodtruck", (Serializable) foodtrucks.get(0));
                startActivity(intent);
            }
        });
        changeBtn.setOnClickListener(new Button.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (changeBtn.getText().toString() == "영업시작") {
                                                 Intent intent = new Intent(TruckInfoActivity.this, TruckOpenActivity.class);
                                                 foodtrucks.get(0).setMenus(menus1);
                                                 intent.putExtra("foodtruck", (Serializable) foodtrucks.get(0));
                                                 startActivity(intent);
                                             } else if (changeBtn.getText().toString() == "영업종료") {
                                                 AlertDialog.Builder ad = new AlertDialog.Builder(TruckInfoActivity.this);
                                                 ad.setTitle("영업종료");       // 제목 설정
                                                 ad.setMessage("영업을 종료하시겠습니까?(매출을 입력해 주세요)");   // 내용 설정

                                                 final EditText et = new EditText(TruckInfoActivity.this);

                                                 et.setInputType(InputType.TYPE_CLASS_NUMBER);//숫자만 입력받기위한 부분

                                                 ad.setView(et);
                                                 ad.setPositiveButton("Insert", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         Log.v("Test_Alert_Dialog", "Yes Btn Click");

                                                         // Text 값 받아서 로그 남기기
                                                         String value = et.getText().toString();
                                                         now = System.currentTimeMillis();
                                                         date = new Date(now);

                                                         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                                         String today = sdf.format(date);
                                                         Log.d("Test_Alert_Dialog", value);

                                                         new CloseTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/closeTruck.do?id="
                                                                 + loginUserId + "&revenue=" + value + "&today=" + today);//server 접근 방법 다시 !

                                                         Intent intent = new Intent(TruckInfoActivity.this, TruckInfoActivity.class);//main페이지 다시 출력

                                                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//페이지 넘어가기전에 main(영업종료누르기 전)페이지 삭제
                                                         intent.putExtra("foodtruck", foodtrucks.get(0));
                                                         startActivity(intent);

                                                         dialog.dismiss();     //닫기
                                                         // Event
                                                     }
                                                 });
                                                 ad.setNegativeButton("Cencle", new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         Log.d(TAG, "No Btn Click");

                                                         dialog.dismiss();     //닫기
                                                         // Event
                                                     }
                                                 });
                                                 ad.show();


                                             }
                                         }
                                     }

        );

        mdate = (DatePicker) findViewById(R.id.startAd);
        advtext = (TextView) findViewById(R.id.advDate);

        Date sd;

        mdate.init(mdate.getYear(), mdate.getMonth(), mdate.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        advtext.setText(String.format("%d%d%d", year, monthOfYear + 1, dayOfMonth));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String strDate = format.format(calendar.getTime());
                        SimpleDateFormat transFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date sd = transFormat1.parse(strDate);
//                            java.util.Date uDate = new java.util.Date(sd);
                            startDay1 = convertUtilToSql(sd);



                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });


        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radio_btn = (RadioButton) findViewById(checkedId);
                RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup); // 라디오그룹 객체 맵핑
                RadioButton selectedRdo = (RadioButton) findViewById(rg.getCheckedRadioButtonId()); // rg 라디오그룹의 체크된(getCheckedRadioButtonId) 라디오버튼 객체 맵핑
                period = selectedRdo.getText().toString(); // 해당 라디오버튼 객체의 값 가져오기
            }
        });


        Button submitBtn = (Button) findViewById(R.id.advbtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TruckInfoActivity.this, "광고신청", Toast.LENGTH_SHORT).show();
                Advertise advertise = new Advertise();
                period = period.substring(0, period.length() - 1);
                realDay = Integer.parseInt(period);
                int a = 0;
                advertise.setSellerId(loginUserId);
                mdate.getYear();
                mdate.getMonth();
                mdate.getDayOfMonth();


                advertise.setStartdate(String.valueOf(startDay1));
                advertise.setPeriod(realDay);
                advertise.setApprove(a);
                new AdvertiseTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/advertiseRegister.do", advertise);
            }
        });
    }

    private class CloseTask extends AsyncTask<String, Void, String> {//truck close를 위한 class

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection http = null;
            InputStream is = null;
            String checkStr = null;
            URL url = null;
            try {
                url = new URL(params[0]);
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.connect();
                is = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                //인코딩
                checkStr = reader.readLine();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return checkStr;
        }
    }

    private static java.sql.Date convertUtilToSql(java.util.Date uDate) {
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        return sDate;
    }


    ////////////////////////////////naverMap Method
    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        if (nMapError == null) { // success
            mMapController.setMapCenter(//지도 출력시 맵 중앙 지정
                    new NGeoPoint(lon, lat), 11);
        } else { // fail
            android.util.Log.e("NMAP", "onMapInitHandler: error="
                    + nMapError.toString());
        }
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


    public class MenuDetailTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String) params[0]);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));
                NodeList nodeList = doc.getElementsByTagName("menu");
                Log.d("1113", String.valueOf(nodeList.getLength()));
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Menu menu = new Menu();
                    Node node = nodeList.item(i);
                    Element element = (Element) node;
                    menu.setMenuId(getTagValue("menuId", element));
//                    menu.setFoodtruckId(getTagValue("foodtruckId", element));
                    menu.setMenuName(getTagValue("menuName", element));
                    menu.setPrice(Integer.parseInt(getTagValue("price", element)));
                    menu.setMenuState(Boolean.parseBoolean(getTagValue("menuState", element)));
                    Log.d("1333",menu.getMenuName());
                    menus1.add(menu);
                }
                Log.d("1333", menus1.toString());
                Log.d("1333", String.valueOf(menus1.size()));
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

    public class ReviewDetialTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String) params[0]);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));
                NodeList nodeList = doc.getElementsByTagName("review");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Review review = new Review();
                    Foodtruck foodtruck = new Foodtruck();
                    Member writer = new Member();
                    ArrayList<Image> images = new ArrayList<>();
                    Image image = new Image();
                    Node node = nodeList.item(i);
                    Element element = (Element) node;
                    String src = "http://foofa.crabdance.com:8888/FoodtruckFinderProject/resources/img/reviewImg/" + getTagValue("filename", element);
                    image.setFilename(src);
                    images.add(image);
                    review.setImages(images);
                    review.setReviewId(getTagValue("reviewId", element));
                    foodtruck.setFoodtruckId(getTagValue("foodtruckId", element));
                    foodtruck.setFoodtruckName(getTagValue("foodtruckName", element));
                    review.setContents(getTagValue("contents", element));
                    writer.setMemberId(getTagValue("memberId", element));
                    NodeList list = element.getElementsByTagName("score").item(1).getChildNodes();
                    review.setScore(Integer.parseInt(list.item(0).getNodeValue()));
                    review.setFoodtruck(foodtruck);
                    review.setWriter(writer);

                    reviews.add(review);
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
            truckReviewAdapter.notifyDataSetChanged();
        }
    }

    private class AdvertiseTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            Advertise advertise = (Advertise) params[1];
            return JsonParsingControl.POST((String) params[0], advertise);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("life", "result" + result);

            Intent intent = null;
            switch (result) {
                case "true":
                    Toast.makeText(TruckInfoActivity.this, "광고 등록에 성공했습니다.", Toast.LENGTH_SHORT).show();

                    intent = new Intent(TruckInfoActivity.this, TruckInfoActivity.class);
                    intent.putExtra("foodtruck", foodtruck1);
                    break;
                case "false":
                    Toast.makeText(TruckInfoActivity.this, "광고 등록에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(TruckInfoActivity.this, TruckInfoActivity.class);
                    intent.putExtra("foodtruck", foodtruck1);
                    break;
            }
            if (intent != null) {
                intent = new Intent(TruckInfoActivity.this, TruckInfoActivity.class);
                startActivity(intent);
            }

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
                    List<Menu> menus1 = new ArrayList<>();
                    NodeList list1 = element.getElementsByTagName("menus").item(i).getChildNodes();
                    int k = list1.getLength();
                    for (int a = 0; a <= k; a++) {
                        Menu menu = new Menu();
                        menu.setMenuName(getTagValue("menuName", element));
                        menu.setPrice(Integer.parseInt(getTagValue("favoriteCount", element)));
                        menu.setMenuState(Boolean.parseBoolean(getTagValue("menuState", element)));
                        menu.setMenuId(getTagValue("menuId", element));
                        menus1.add(menu);
                    }
                    foodtruck.setMenus(menus1);
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

            menus1 = new ArrayList<>();
            menus1 = foodtruck1.getMenus();
            if (foodtrucks.get(0).isState() != true) {
                changeBtn.setText("영업시작");
            } else {
                changeBtn.setText("영업종료");
            }
        }
    }


}
