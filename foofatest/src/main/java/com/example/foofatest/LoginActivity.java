package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    private EditText idEdit;
    private EditText pwEdit;

    private CheckBox sellerChek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        String id = prefs.getString("id", "");
        if(!id.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        idEdit = (EditText) findViewById(R.id.idedit);
        pwEdit = (EditText) findViewById(R.id.pwedit);
        sellerChek = (CheckBox) findViewById(R.id.sellerCheck);

        if(sellerChek.isChecked()) {
            findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LoginCheckTask().execute("http://106.242.203.67:8088/FoodtruckFinderProject/mobilelogin.do?id="
                            + idEdit.getText() + "&password=" + pwEdit.getText());
                    Toast.makeText(LoginActivity.this, "Seller", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LoginCheckTask().execute("http://106.242.203.67:8088/FoodtruckFinderProject/mobilelogin.do?id="
                            + idEdit.getText() + "&password=" + pwEdit.getText());
                    Toast.makeText(LoginActivity.this, "Member", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
    }
}
