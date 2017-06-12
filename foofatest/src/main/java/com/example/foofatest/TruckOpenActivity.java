package com.example.foofatest;

import android.app.Activity;
import android.content.Context;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.foofatest.dto.Foodtruck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.List;
import java.util.Map;

public class TruckOpenActivity extends AppCompatActivity {
    private Foodtruck foodtruck;
    private int hour;
    private int minute;
    private int ampm;
    private String Geolocation = "";

    final Geocoder geocoder = new Geocoder(this);

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

        final TextView locationText = (TextView)findViewById(R.id.locationText);
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


        location.setText(Geolocation);
        notice.setText(foodtruck.getNotice());
        /*startTime.setHour(hour);
        startTime.setMinute(minute);
        endTime.setHour(hour);
        endTime.setMinute(minute);*/
        card.setChecked(foodtruck.isCard());
        alchol.setChecked(foodtruck.isDrinking());
        parking.setChecked(foodtruck.isParking());
        catering.setChecked(foodtruck.isCatering());

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);

        }catch (SecurityException ex){

        }


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
                lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
            }
        });

    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            List<Address> list = null;


            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도

            try {
                list = geocoder.getFromLocation(
                        latitude, // 위도
                        longitude, // 경도
                        1); // 얻어올 값의 개수
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");

            }
            if (list != null) {
                if (list.size() == 0) {
                    Geolocation = "";
                } else {
                    Geolocation = list.get(0).getAddressLine(0).toString();
                }
            }
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
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

        return result;
    }
}
