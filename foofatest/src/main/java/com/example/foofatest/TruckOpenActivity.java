package com.example.foofatest;

import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.foofatest.dto.Foodtruck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class TruckOpenActivity extends AppCompatActivity {
    private Foodtruck foodtruck;
    private int hour;
    private int minute;
    private int ampm;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_open);

        /*Intent intent = getIntent();
        foodtruck = (Foodtruck)intent.getExtras().get("foodtruck");*/

        foodtruck = new Foodtruck();
        foodtruck.setFoodtruckId("F1041");
        foodtruck.setSellerId("king1");
        foodtruck.setFoodtruckName("와이키키제주");
        foodtruck.setLocation("제주시 애월읍");
        foodtruck.setNotice("오늘도 역시 재료 소진시 영업마감합니다. 보통 2-3시에 마감하니 들리시기 직전 영업상태를 다시한번 확인해주세요!");
        foodtruck.setCard(true);
        foodtruck.setDrinking(true);
        foodtruck.setParking(false);
        foodtruck.setCatering(true);

        final EditText location = (EditText)findViewById(R.id.modLocation);
        final EditText notice = (EditText)findViewById(R.id.modNotice);
        /*final TimePicker startTime = (TimePicker)findViewById(R.id.modStartTime);
        final TimePicker endTime = (TimePicker)findViewById(R.id.modEndTime);*/
        final CheckBox card = (CheckBox)findViewById(R.id.modAcceptCard);
        final CheckBox alchol = (CheckBox)findViewById(R.id.modAlchol);
        final CheckBox parking = (CheckBox)findViewById(R.id.modParking);
        final CheckBox catering = (CheckBox)findViewById(R.id.modCatering);

        final Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        ampm = cal.get(Calendar.AM_PM);

        location.setText(foodtruck.getLocation());
        notice.setText(foodtruck.getNotice());
        /*startTime.setHour(hour);
        startTime.setMinute(minute);
        endTime.setHour(hour);
        endTime.setMinute(minute);*/
        card.setChecked(foodtruck.isCard());
        alchol.setChecked(foodtruck.isDrinking());
        parking.setChecked(foodtruck.isParking());
        catering.setChecked(foodtruck.isCatering());


        Button modifyBtn = (Button)findViewById(R.id.modTruckBtn);

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodtruck.setLocation(location.getText().toString());
                foodtruck.setNotice(notice.getText().toString());
                foodtruck.setCard(card.isChecked());
                foodtruck.setDrinking(alchol.isChecked());
                foodtruck.setParking(parking.isChecked());
                foodtruck.setCatering(catering.isChecked());



                sendTask.execute((Runnable) foodtruck);

            }
        });

    }

    public class sendTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            HashMap<String, Foodtruck> map = new HashMap<String, Foodtruck>();
            map.put("foodtruck", foodtruck);
            send(map);
            return null;
        }
    }

    //  매개변수를 웹으로 보내고 그 결과를 반환하는 함수
    private String send(HashMap<String, Foodtruck> map){

        String response = "";   // DB서버의 응답을 담는 변수
        String addr = "http://10.0.2.2:8888/FoodtruckFinderProject/mobile/truck/modify.do";
        try {
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();   //해당 URL에 연결
            conn.setConnectTimeout(1000); //타임아웃: 10초
            conn.setUseCaches(false); //캐시 사용 안함
            conn.setRequestMethod("POST"); //POST로 연결
            conn.setDoInput(true);
            conn.setDoOutput(true);

            if(map != null){ // 웹서버로 보낼 매개변수가 있는 경우
                OutputStream os = conn.getOutputStream();   //서버로 보내기 위한 출력 스트림
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));    //UTF-8로 전송
                bw.write(getPostString(map)); //매개변수 전송
                bw.flush();
                bw.close();
                os.close();
            }

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));   //서버의 응답을 읽기 위한 입력 스트림
                while((line = br.readLine()) != null)   //서버의 응답을 읽어옴
                    response += line;
            }

            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    //매개변수를 URL에 붙이는 함수
    private String getPostString(HashMap<String, Foodtruck> map){
        StringBuilder result = new StringBuilder();
        boolean first = true; //첫 번째 매개변수 여부

        for(Map.Entry<String, Foodtruck> entry : map.entrySet()){
            if(first)
                first = false;
            else    //첫 번째 매개변수가 아닌 경우엔 앞에 &를 붙임
            result.append("&");

            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                /*result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));*/
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        return result.toString();
    }


}
