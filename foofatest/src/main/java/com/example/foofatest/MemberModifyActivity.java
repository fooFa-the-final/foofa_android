package com.example.foofatest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foofatest.Json.JsonParsingControl;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemberModifyActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private String memberId;
    private Member member;
    private EditText passwordEdit, password1Edit, email;
    private TextView Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_modify);

        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        memberId = prefs.getString("loginId", "");

        Id = (TextView)findViewById(R.id.memberId);
        passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        password1Edit= (EditText)findViewById(R.id.password1Edit);
        email = (EditText)findViewById(R.id.email);
        Id.setText(memberId);
        Button submitBtn = (Button)findViewById(R.id.modify);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                member.setPassword(passwordEdit.getText().toString());
                member.setEmail(email.getText().toString());
                Log.d("1111",member.toString());

                new MemberModifyActivity.ModifyTask().execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/modify.do", member);
            }
    });
    }
    private class ModifyTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            Member member = (Member) params[1];
            return JsonParsingControl.POST((String) params[0], member);
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = null;
            intent = new Intent(MemberModifyActivity.this, MemberModifyActivity.class);
            startActivity(intent);
        }

    }

}
