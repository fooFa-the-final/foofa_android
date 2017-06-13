package com.example.foofatest;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.foofatest.Adapter.FoodtruckDetailAdapter;
import com.example.foofatest.Adapter.FoodtruckDetailMenuAdapter;
import com.example.foofatest.Adapter.TruckReviewAdapter;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;
import com.example.foofatest.dto.Menu;
import com.example.foofatest.dto.Review;

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
import java.util.Date;
import java.util.List;
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
//        ts1.setContent(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        tabHost1.addTab(ts1);
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.truckReview);
        ts2.setIndicator("리뷰");
//        ts2.setContent(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        tabHost1.addTab(ts2);
        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3");
        ts3.setContent(R.id.truckAdvertise);
        ts3.setIndicator("광고신청");
//        ts3.setContent(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        tabHost1.addTab(ts3);


        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        loginUserId= prefs.getString("id", "");
//        Intent intent = new Intent();
//        final String id = intent.getExtras().getString("loginUserId".toString());

        Log.d(loginUserId, "id는 뭔가여");
        foodtrucks = new ArrayList<>();
        adapter = new FoodtruckDetailAdapter(this, foodtrucks);


        findViewById(R.id.truckStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInfoActivity.this, TruckModifyActivity.class);
                startActivity(intent);
            }
        });

        final ListView list = (ListView) findViewById(R.id.truckDetail);
        new FoodtruckDetailTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/detail.do?id="+loginUserId);
        list.setAdapter(adapter);

        reviews = new ArrayList<>();
        truckReviewAdapter = new TruckReviewAdapter(this, reviews);

        new ReviewDetialTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/review/list/turck.do?id="+loginUserId);


        final ListView reviewlist = (ListView) findViewById(R.id.truckReviewListlist);
        reviewlist.setAdapter(truckReviewAdapter);
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
                    Node node = nodeList.item(i);
                    Element element = (Element) node;
                    review.setContents(getTagValue("contents", element));
                    review.setScore(Float.valueOf(getTagValue("score", element)));
                    review.setRecommand(Integer.parseInt(getTagValue("recommand", element)));
                    review.setReviewId(getTagValue("reviewId", element));
//                    review.setWriteDate(DataFormatException(getTagValue("writeDate", element)));
                    writer.setMemberId(getTagValue("memberId", element));
                    foodtruck.setFoodtruckId(getTagValue("foodtruckId", element));
                    foodtruck.setSellerId(getTagValue("sellerId", element));
                    foodtruck.setFoodtruckName(getTagValue("foodtruckName", element));
                    foodtruck.setOperationTime(getTagValue("operationTime", element));
                    foodtruck.setSpot(getTagValue("spot", element));
                    foodtruck.setNotice(getTagValue("notice", element));
                    foodtruck.setLocation(getTagValue("location", element));
                    foodtruck.setCategory1(getTagValue("category1", element));
//                    foodtruck.setCategory2(getTagValue("category2", element));
//                    foodtruck.setCategory3(getTagValue("category3", element));
                    foodtruck.setCard(getTagBoolean("card", element));
                    foodtruck.setParking(getTagBoolean("parking", element));
                    foodtruck.setDrinking(getTagBoolean("drinking", element));
                    foodtruck.setCatering(getTagBoolean("catering", element));
                    foodtruck.setState(getTagBoolean("state", element));
                    foodtruck.setFavoriteCount(Integer.parseInt(getTagValue("favoriteCount", element)));
                    foodtruck.setReviewCount(Integer.parseInt(getTagValue("reviewCount", element)));
                    foodtruck.setScore(Double.parseDouble(getTagValue("score", element)));
                    NodeList nodeList1 = doc.getElementsByTagName("menus");
                    foodtruck.setFoodtruckImg("http://foofa.crabdance.com:8888/FoodtruckFinderProject/resources/img/food/"+getTagValue("foodtruckImg", element));
                    review.setWriter(writer);
                    review.setFoodtruck(foodtruck);
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
            adapter.notifyDataSetChanged();
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
//                    foodtruck.setCategory2(getTagValue("category2", element));
//                    foodtruck.setCategory3(getTagValue("category3", element));
                    foodtruck.setCard(getTagBoolean("card", element));
                    foodtruck.setParking(getTagBoolean("parking", element));
                    foodtruck.setDrinking(getTagBoolean("drinking", element));
                    foodtruck.setCatering(getTagBoolean("catering", element));
                    foodtruck.setState(getTagBoolean("state", element));
                    foodtruck.setFavoriteCount(Integer.parseInt(getTagValue("favoriteCount", element)));
                    foodtruck.setReviewCount(Integer.parseInt(getTagValue("reviewCount", element)));
                    foodtruck.setScore(Double.parseDouble(getTagValue("score", element)));
                    NodeList nodeList1 = doc.getElementsByTagName("menus");
//                    List<Menu> menus1 = new ArrayList<>();
//                    for(int k = 0 ; k < nodeList1.getLength(); k++) {
//                        Menu menu = new Menu();
//                        menu.setFoodtruckId(getTagValue("menumenuId", element));
//                        menu.setMenuName(getTagValue("menuName", element));
//                        menu.setPrice(Integer.parseInt(getTagValue("favoriteCount", element)));
//                        menu.setMenuState(getTagBoolean("menuState", element));
//                        menu.setFoodtruckId(getTagValue("menuId", element));
//                        menus1.add(menu);
//                    }
//                    foodtruck.setMenus(menus1);
                    foodtruck.setFoodtruckImg("http://foofa.crabdance.com:8888/FoodtruckFinderProject/resources/img/food/"+getTagValue("foodtruckImg", element));
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

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node) nodeList.item(0);
        return value.getNodeValue();
    }

    private static Boolean getTagBoolean(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node) nodeList.item(0);
        return value.hasChildNodes();
//되는지 모르겠따;
    }




}
