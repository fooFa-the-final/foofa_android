package com.example.foofatest;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
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


public class TruckInfoActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private Button truckStatus;
    private LayoutInflater inflater;
    private String loginUserId;
    private FoodtruckDetailAdapter adapter;
    private TruckReviewAdapter truckReviewAdapter;
    private FoodtruckDetailMenuAdapter foodtruckDetailMenuAdapter;

    private List<Foodtruck> foodtrucks;
    private List<Menu> menus1;
    private List<Review> reviews;
    private Button changeBtn;



    private DatePicker mdate;
    private TextView advtext;
    private RadioGroup radioGroup;
    private Date startDay;
    private String period;
    private int realDay;
    private String sellerId;
    private Advertise advertise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_seller);

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
        foodtrucks = new ArrayList<>();
        adapter = new FoodtruckDetailAdapter(this, foodtrucks);

        findViewById(R.id.truckChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInfoActivity.this, TruckOpenActivity.class);
                startActivity(intent);
            }
        });


        final ListView list = (ListView) findViewById(R.id.truckDetail);
        new FoodtruckDetailTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/detail.do?id=" + loginUserId);
        list.setAdapter(adapter);

        reviews = new ArrayList<>();
        truckReviewAdapter = new TruckReviewAdapter(this, reviews);

        new ReviewDetialTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/review/list/turck.do?id=" + loginUserId);
        final ListView reviewlist = (ListView) findViewById(R.id.truckReviewListlist);
        reviewlist.setAdapter(truckReviewAdapter);


        menus1 = new ArrayList<>();
        foodtruckDetailMenuAdapter = new FoodtruckDetailMenuAdapter(this, menus1);

        new MenuDetailTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/menu/detail.do?id=" + loginUserId);
        final ListView menulist = (ListView) findViewById(R.id.truckInfoMenu1);
        menulist.setAdapter(foodtruckDetailMenuAdapter);


        final TextView text = (TextView) findViewById(R.id.truckChange);
        changeBtn = (Button) findViewById(R.id.truckChange);

        changeBtn.setOnClickListener(new Button.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if(text.toString() != "영업시작") {
                                                 Intent intent = new Intent(TruckInfoActivity.this, TruckOpenActivity.class);
                                                 foodtrucks.get(0).setMenus(menus1);
                                                 intent.putExtra("foodtruck", (Serializable) foodtrucks.get(0));
                                                 text.setText("영업종료");
                                                 startActivity(intent);
                                             } else if(text.toString()!="영업종료") {
                                                 Intent intent = new Intent(TruckInfoActivity.this, TruckClosedActivity.class);
                                                 intent.putExtra("foodtruck", (Serializable) foodtrucks);
                                                 text.setText("영업시작");
                                                 startActivity(intent);
                                             }
                                         }
                                     }

        );

        mdate = (DatePicker) findViewById(R.id.startAd);
        advtext = (TextView) findViewById(R.id.advDate);

        mdate.init(mdate.getYear(), mdate.getMonth(), mdate.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        advtext.setText(String.format("%d%d%d", year, monthOfYear + 1, dayOfMonth));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
                        String strDate = format.format(calendar.getTime());
                        SimpleDateFormat transFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            startDay = transFormat1.parse(strDate);
                            //아주굿
//                            Log.d("1111", String.valueOf(to1));
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

        findViewById(R.id.advbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Advertise advertise = new Advertise();
                Log.d("1111", String.valueOf(startDay));
                period = period.substring(0, period.length() - 1);
                Log.d("1111", period);

                realDay = Integer.parseInt(period);

                Log.d("1111", String.valueOf(realDay));
                advertise.setAdvId(sellerId);
                advertise.setStartdate(startDay);
                advertise.setPeriod(realDay);

                new AdvertiseTask().execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/advertiseRegister.do", advertise);

            }
        });


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
                    List<Image> images = new ArrayList<>();
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
                    Log.d("1111", String.valueOf(list1.getLength()));
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
            adapter.notifyDataSetChanged();
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
                    break;
                case "false":
                    Toast.makeText(TruckInfoActivity.this, "광고 등록에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(TruckInfoActivity.this, TruckInfoActivity.class);
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
}
