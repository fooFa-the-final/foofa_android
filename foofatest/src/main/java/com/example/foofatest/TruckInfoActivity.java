package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.foofatest.Adapter.FoodtruckDetailMenuAdapter;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Menu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TruckInfoActivity extends AppCompatActivity {


    private SharedPreferences prefs;
    private Button truckStatus;

    private FoodtruckDetailMenuAdapter adapter;
    private Foodtruck foodtruck;
    private List<Menu> menus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_seller);


        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1);
        tabHost1.setup();
        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);

        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.truckInfo);
        ts1.setIndicator("INFO");
        tabHost1.addTab(ts1);


        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts1.setContent(R.id.truckReview);
        ts1.setIndicator("리뷰");
        tabHost1.addTab(ts1);


        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3");
        ts1.setContent(R.id.truckAdvertise);
        ts1.setIndicator("광고신청");
        tabHost1.addTab(ts1);

//        Intent intent = getIntent();
//        Foodtruck truck = (Foodtruck) intent.getExtras().get("truck");
//
//
//        ((TextView)findViewById(R.id.truckName)).setText(truck.getFoodtruckName());
//        ((TextView)findViewById(R.id.truckCategory)).setText(truck.getCategory1());
//        ((TextView)findViewById(R.id.truckArea)).setText(truck.getSpot());
//        ((TextView)findViewById(R.id.truckFavorite)).setText(truck.getFavoriteCount());
//        ((TextView)findViewById(R.id.truckReviewCount)).setText(truck.getReviewCount());
//        ((TextView)findViewById(R.id.truckNotice)).setText(truck.getNotice());
//        ((TextView)findViewById(R.id.truckHours)).setText(truck.getOperationTime());
//        ((TextView)findViewById(R.id.truckLocation)).setText(truck.getLocation());
//
//
//        truckStatus = (Button)findViewById(R.id.truckStatus);
//        truckStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TruckInfoActivity.this, TruckModifyActivity.class);
//                intent.putExtra("foodtruck", foodtruck);
//                startActivity(intent);
//            }
//        });

    }



//    public class FoodtruckLoadingTask extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... params) {
//            try {
//   /*                 URL url = new URL((String) params[0]);
//                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                    DocumentBuilder builder = factory.newDocumentBuilder();
//                    Document doc = builder.parse(new InputSource(url.openStream()));
//                    NodeList nodeList = doc.getElementsByTagName("foodtruck");
//                    Node node = doc.getElementsByTagName("foodtruck");
//                    Element element = (Element) node;
//                    Foodtruck foodtruck = new Foodtruck();*/
//
//            }
//        }
//
//
//    }

}
