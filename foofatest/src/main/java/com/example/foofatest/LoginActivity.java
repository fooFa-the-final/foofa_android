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

    private CheckBox sellerCheck;
    private CheckBox autoLoginCheck;


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
                }else {
                    new LoginCheckTask().execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/memberlogin.do?id="
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
                    intent = new Intent(LoginActivity.this, TruckInfoActivity.class);
                }else {
                    intent = new Intent(LoginActivity.this, TruckInfoActivity.class);
                }

                intent = new Intent(LoginActivity.this, ButtonListActivity.class);

                SharedPreferences login = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
                Log.d("life", idEdit.getText()+"??");
                Log.d("life", idEdit.getText().toString()+"??");
                login.edit().putString("loginId", idEdit.getText().toString()).apply();
                intent.putExtra("loginUserId",idEdit.getText().toString());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "아이디또는 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
            }
        }
    }

}




