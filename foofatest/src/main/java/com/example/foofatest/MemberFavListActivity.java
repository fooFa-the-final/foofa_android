package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.foofatest.Adapter.FavoriteListlAdapter;
import com.example.foofatest.dto.Foodtruck;

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
import java.lang.reflect.Member;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MemberFavListActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private String loginUserId;
    private ListView listView;
    private FavoriteListlAdapter adapter;
    private List<Foodtruck> foodtrucks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_fav_list);

        pref = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        loginUserId = pref.getString("loginUserId", "");

        //login 안되어 있으면 이동.
        if (loginUserId.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        new FavoriteTask().execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/sellerlogin.do?id="+loginUserId);

        listView = (ListView)findViewById(R.id.memberFavList);
        adapter = new FavoriteListlAdapter(this, foodtrucks);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListViewItemClickListener());
    }

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MemberFavListActivity.this, TruckDetailActivity.class);
            Foodtruck foodtruck = foodtrucks.get(position);
            intent.putExtra("foodtruck", foodtruck);
            startActivity(intent);
        }
    }

    private class FavoriteTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String)params[0]);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = doc.getElementsByTagName("foodtruck");
                for(int i=0; i<nodeList.getLength(); i++){
                    Foodtruck foodtruck = new Foodtruck();
                    Node node = nodeList.item(i);

                    Element element = (Element)node;

                    foodtruck.setFoodtruckName(getTagValue("foodtruckName",element));
                    foodtruck.setCategory1(getTagValue("category1", element));
                    foodtruck.setCategory2(getTagValue("category2", element));
                    foodtruck.setCategory3(getTagValue("category3", element));

                    foodtruck.setFoodtruckImg("http://106.242.203.67:8888/FoodtruckFinderProject/resources/img/food/"+getTagValue("foodtruckImg",element));

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

    private static String getTagValue(String tag, Element element){

        if(element.getElementsByTagName(tag).item(0)==null){
            return "";
        }

        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node)nodeList.item(0);
        return  node.getNodeValue();
    }
}
