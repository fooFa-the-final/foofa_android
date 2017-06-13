package com.example.foofatest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    }
}
