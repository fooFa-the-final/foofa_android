package com.example.foofatest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

public class TruckInfoActivity extends AppCompatActivity {



    private SharedPreferences prefs;
    private Button truckStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_info);


        truckStatus = (Button)findViewById(R.id.truckStatus);

        truckStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TruckInfoActivity.this, TruckModifyActivity.class);
                intent.putExtra("foodtruck", id);
                startActivity(intent);
            }
        });

        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1) ;
        tabHost1.setup() ;

        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.info) ;
        ts1.setIndicator("TAB 1") ;
        tabHost1.addTab(ts1) ;


        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts1.setContent(R.id.리뷰) ;
        ts1.setIndicator("TAB 2") ;
        tabHost1.addTab(ts1) ;


        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3") ;
        ts1.setContent(R.id.광고신청) ;
        ts1.setIndicator("TAB 3") ;
        tabHost1.addTab(ts1) ;



    }
}
