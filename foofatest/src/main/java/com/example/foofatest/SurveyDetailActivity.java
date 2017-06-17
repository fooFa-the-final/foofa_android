package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SurveyDetailActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private String sellerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_detail);

        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        sellerId = prefs.getString("loginId", "");
        if (sellerId.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }





    }
}
