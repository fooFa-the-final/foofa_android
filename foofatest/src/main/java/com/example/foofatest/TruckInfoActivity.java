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
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Menu;

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



public class TruckInfoActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private Button truckStatus;
    private LayoutInflater inflater;

    private FoodtruckDetailAdapter adapter;
    private List<Foodtruck> foodtrucks;
    private List<Menu> menus1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_seller);

//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        final TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
//        tabHost.setup();
//
//        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("tab1").setContent(R.id.TextView01));
//        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("tab2").setContent(R.id.Button01));
//    }
//}



        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1);
//        tabHost1.setup();
 LocalActivityManager oo = new LocalActivityManager(getParent(), true);
//        LocalActivityManager oo = getLocalActivityManager();
        tabHost1.setup(oo);



//        tabHost1.addTab(tabHost1.newTabSpec("tab1").setIndicator("INFO").setContent(R.id.truckInfo1).setContent(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)));
//        tabHost1.addTab(tabHost1.newTabSpec("tab2").setIndicator("INFO1").setContent(R.id.truckReview).setContent(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)));
//        tabHost1.addTab(tabHost1.newTabSpec("tab3").setIndicator("INFO2").setContent(R.id.truckAdvertise).setContent(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)));


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
        final String id = prefs.getString("id", "");
        Log.d(id, "id는 뭔가여");
        foodtrucks = new ArrayList<>();
        adapter = new FoodtruckDetailAdapter(this, foodtrucks);

        truckStatus = (Button)findViewById(R.id.truckStatus);
//        truckStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TruckInfoActivity.this, TruckModifyActivity.class);
//                startActivity(intent);
//            }
//        });
        final ListView list = (ListView) findViewById(R.id.truckDetail);
        new FoodtruckDetailTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/detail.do?id="+id);
        list.setAdapter(adapter);

        // 방안 2 (login intent 트럭 담아오기
//        Intent intent = getIntent();
//        Foodtruck truck = (Foodtruck) intent.getExtras().get("truck");
//        ((TextView)findViewById(R.id.truckName)).setText(foodtrucks.get(0).getFoodtruckName());
//        ((TextView)findViewById(R.id.truckCategory)).setText(foodtrucks.get(0).getCategory1());
//        ((TextView)findViewById(R.id.truckArea)).setText(foodtrucks.get(0).getSpot());
//        ((TextView)findViewById(R.id.truckFavorite)).setText(foodtrucks.get(0).getFavoriteCount());
//        ((TextView)findViewById(R.id.truckReviewCount)).setText(foodtrucks.get(0).getReviewCount());
//        ((TextView)findViewById(R.id.truckNotice)).setText(foodtrucks.get(0).getNotice());
//        ((TextView)findViewById(R.id.truckHours)).setText(foodtrucks.get(0).getOperationTime());
//        ((TextView)findViewById(R.id.truckLocation)).setText(foodtrucks.get(0).getLocation());




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
