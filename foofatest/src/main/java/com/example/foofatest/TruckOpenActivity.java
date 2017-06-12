package com.example.foofatest;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foofatest.dto.Foodtruck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
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

                HttpAsyncTask httpTask = new HttpAsyncTask(TruckOpenActivity.this);
                httpTask.execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/foodtruck/modify.do", foodtruck);
                Toast.makeText(getBaseContext(), "conntection", Toast.LENGTH_LONG).show();
            }
        });

    }

    private class HttpAsyncTask extends AsyncTask<Object, Void, String> {

        private  TruckOpenActivity truckOpenAct;

        HttpAsyncTask(TruckOpenActivity truckOpenActivity) {
            this.truckOpenAct = truckOpenActivity;
        }

        @Override
        protected String doInBackground(Object... objects) {

            Foodtruck foodtruck = (Foodtruck)objects[1];

            return POST((String) objects[0], foodtruck);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static String POST(String url, Foodtruck foodtruck){
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String json = gson.toJson(foodtruck);

            Log.d("InputStream", jsonObject.toString());
            // convert JSONObject to JSON to String

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // Set some headers to inform server about the type of the content
            httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("Content-type", "application/json");

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("utf-8"));
            os.flush();
            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        Log.d("InputStream", result);
        return result;
    }
}
