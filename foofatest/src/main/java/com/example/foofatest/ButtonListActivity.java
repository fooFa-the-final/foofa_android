package com.example.foofatest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;
import com.example.foofatest.dto.Menu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ButtonListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_list);

        Log.d("life", getIntent().getStringExtra("loginUserId")+":");


        findViewById(R.id.favoriteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, MemberFavListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.memberRegisterBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, MemberRegisterActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.truckOpenBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, TruckOpenActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.foodtruckInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, TruckInfoActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.memberReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, ReviewListActivity.class);


                startActivity(intent);
            }
        });
        findViewById(R.id.followButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ButtonListActivity.this, MemberFollowActivity.class);
                startActivity(intent);

            }
        });
        findViewById(R.id.createReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ButtonListActivity.this, ReviewCreateActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.survey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ButtonListActivity.this, SurveyActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.memberNewsfeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ButtonListActivity.this, MemberNewsfeedActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.main_act1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ButtonListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });




    }


}
