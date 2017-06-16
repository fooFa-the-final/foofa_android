package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foofatest.Adapter.FoodtruckDetailAdapter;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Menu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    private EditText idEdit;
    private EditText pwEdit;

    private CheckBox sellerCheck;
    private CheckBox autoLoginCheck;
    public List<Foodtruck> foodtrucks;

    public FoodtruckDetailAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        prefs = getSharedPreferences("autologinUserId", Context.MODE_PRIVATE);
        String id = prefs.getString("id", "");
        if (!id.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        idEdit = (EditText) findViewById(R.id.idedit);
        pwEdit = (EditText) findViewById(R.id.pwedit);
        sellerCheck = (CheckBox) findViewById(R.id.sellerCheck);
        autoLoginCheck = (CheckBox) findViewById(R.id.autoLoginCheck);


        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sellerCheck.isChecked()){
                    new LoginCheckTask().execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/sellerlogin.do?id="
                            + idEdit.getText() + "&password=" + pwEdit.getText());
                    String tempId ;
                    tempId = idEdit.getText().toString();
                    new FoodtruckDetailTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/detail.do?id=" + tempId);
                }else {
                    new LoginCheckTask().execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/memberlogin.do?id="
                            + idEdit.getText() + "&password=" + pwEdit.getText());
                }
            }
        });
    }

    private class LoginCheckTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection http = null;
            InputStream is = null;
            String checkStr = null;
            URL url = null;
            try {
                url = new URL(params[0]);
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
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
        @Override
        protected void onPostExecute(String s) {
            if(s.equals("true")){

                if(autoLoginCheck.isChecked()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("id", idEdit.getText().toString());
                    editor.putString("pw", pwEdit.getText().toString());
                    editor.apply();
                }
                Toast.makeText(LoginActivity.this, "정상 로그인 되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent;
                if(sellerCheck.isChecked()){
                    finish();
                }else {
                    intent = new Intent(LoginActivity.this, TruckInfoActivity.class);
                }

                intent = new Intent(LoginActivity.this, ButtonListActivity.class);

                SharedPreferences login = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
                login.edit().putString("loginId", idEdit.getText().toString()).apply();
                intent.putExtra("loginUserId",idEdit.getText().toString());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "아이디또는 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public class FoodtruckDetailTask extends AsyncTask<String, Void, Void> {




        @Override
        protected Void doInBackground(String... params) {

            foodtrucks = new ArrayList<>();

//            adapter = new FoodtruckDetailAdapter(this, foodtrucks);

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
            Intent intent;
            intent = new Intent(LoginActivity.this, TruckInfoActivity.class);
            Foodtruck foodturckPrime = new Foodtruck();
            foodturckPrime = foodtrucks.get(0);
            Log.d("1111", String.valueOf(foodtrucks.get(0).getFoodtruckId()));
            intent.putExtra("foodtruck", (Serializable) foodturckPrime);
            startActivity(intent);
            finish();

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




