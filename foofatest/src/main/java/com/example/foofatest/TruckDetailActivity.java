package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

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

public class TruckDetailActivity extends AppCompatActivity {

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

        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        loginUserId = prefs.getString("loginId", "");



        Intent intent = getIntent();
        foodtruck1 = (Foodtruck)intent.getExtras().get("foodtruck");
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
