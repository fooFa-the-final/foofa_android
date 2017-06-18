package com.example.foofatest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.foofatest.Adapter.FoodtruckOpenMenuAdapter;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Menu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TruckOpenActivity extends AppCompatActivity {
    private FoodtruckOpenMenuAdapter adapter;
    private Foodtruck foodtruck;
    private int hour, minute;
    private String Geolocation = "";
    private TextView locationText;
    private EditText location1;
    private CheckBox card, drinking, parking, catering;
    private EditText notice, mName, mPrice;
    private TimePicker startTime, endTime;
    private List<Menu> menus;
    private Button addBtn;
    private Spinner spinner;
    private String select_item;

    private AdapterView.AdapterContextMenuInfo info;

//    final Geocoder geocoder = new Geocoder(this);

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_open);

        Intent intent = getIntent();
        foodtruck = (Foodtruck)intent.getExtras().get("foodtruck");

        menus = foodtruck.getMenus();
        adapter = new FoodtruckOpenMenuAdapter(this, menus);


        ListView list = (ListView)findViewById(R.id.modMenus);
        list.setOnTouchListener(new ListView.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        list.setAdapter(adapter);
        registerForContextMenu(list);


        locationText = (TextView) findViewById(R.id.locationText);
        location1 = (EditText) findViewById(R.id.myLocation);
        notice = (EditText) findViewById(R.id.modNotice);
        startTime = (TimePicker)findViewById(R.id.modStartTime);
        startTime.setIs24HourView(true);
        endTime = (TimePicker)findViewById(R.id.modEndTime);
        endTime.setIs24HourView(true);
        card = (CheckBox) findViewById(R.id.modAcceptCard);
        drinking = (CheckBox) findViewById(R.id.modAlchol);
        parking = (CheckBox) findViewById(R.id.modParking);
        catering = (CheckBox) findViewById(R.id.modCatering);
        mName = (EditText)findViewById(R.id.inputMenuName);
        mPrice = (EditText)findViewById(R.id.inputMenuPrice);
        addBtn = (Button)findViewById(R.id.add);
        spinner = (Spinner)findViewById(R.id.inputMenuState);
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_menu_state, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_item = (String)spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button)v;

                Menu m = new Menu();
                try {
                    if(select_item.equals("판매중")){
                        m.setMenuState(true);
                    } else {
                        m.setMenuState(false);
                    }
                    m.setMenuName(mName.getText().toString());
                    m.setPrice(Integer.parseInt(mPrice.getText().toString()));
                    m.setFoodtruckId(foodtruck.getFoodtruckId());

                } catch (NumberFormatException e){
                    e.printStackTrace();
                }

                if(button.getText().equals("ADD")){
                    adapter.add(0, m);
                } else {
                    adapter.modify(info.position, m);
                    addBtn.setText("ADD");
                }
                mName.setText("");
                mPrice.setText("");
            }
        });

        final Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);


        notice.setText(foodtruck.getNotice());
        startTime.setHour(hour);
        startTime.setMinute(minute);
        endTime.setHour(hour);
        endTime.setMinute(minute);
        card.setChecked(foodtruck.isCard());
        drinking.setChecked(foodtruck.isDrinking());
        parking.setChecked(foodtruck.isParking());
        catering.setChecked(foodtruck.isCatering());

        /*final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);

        } catch (SecurityException ex) {

        }*/


        Button modifyBtn = (Button) findViewById(R.id.modTruckBtn);

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodtruck.setLocation(location1.getText().toString());
                foodtruck.setNotice(notice.getText().toString());
                foodtruck.setCard(card.isChecked());
                foodtruck.setDrinking(drinking.isChecked());
                foodtruck.setParking(parking.isChecked());
                foodtruck.setCatering(catering.isChecked());
                foodtruck.setOperationTime(getTime(startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute()));
                foodtruck.setMenus(menus);


                HttpAsyncTask httpTask = new HttpAsyncTask(TruckOpenActivity.this);
                httpTask.execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/foodtruck/open.do", foodtruck);
                Toast.makeText(getBaseContext(), "conntection", Toast.LENGTH_LONG).show();
                //lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
            }
        });

    }

    /*private final LocationListener mLocationListener = new LocationListener() {
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
                    location1.setText(Geolocation);
                    locationText.setText("Location detail(done)");
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
    };*/

    private class HttpAsyncTask extends AsyncTask<Object, Void, String> {

        private TruckOpenActivity truckOpenAct;

        HttpAsyncTask(TruckOpenActivity truckOpenActivity) {
            this.truckOpenAct = truckOpenActivity;
        }

        @Override
        protected String doInBackground(Object... objects) {

            Foodtruck foodtruck = (Foodtruck) objects[1];

            return POST((String) objects[0], foodtruck);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("ok")){
                Intent intent = new Intent(TruckOpenActivity.this, TruckInfoActivity.class);
                intent.putExtra("foodtruck", foodtruck);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(TruckOpenActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static String POST(String url, Foodtruck foodtruck) {
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();

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
                if (is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpCon.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(android.view.Menu.NONE, 1, android.view.Menu.NONE, "Delete");
        menu.add(android.view.Menu.NONE, 2, android.view.Menu.NONE, "Modify");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch(item.getItemId()){
            case 1:
                adapter.delete(info.position);
                break;
            case 2:
                Menu menu = menus.get(info.position);
                mName.setText(menu.getMenuName());
                mPrice.setText(menu.getPrice() + "");
                if(menu.isMenuState()){
                    spinner.setSelection(0);
                } else {
                    spinner.setSelection(1);
                }

                addBtn.setText("MODIFY");

                adapter.modify(info.position, menu);
        }

        return super.onContextItemSelected(item);
    }


    private String getTime(int sHour, int sMin, int eHour, int eMin){
        String sAP;
        String eAP;

        if(sHour > 12){
            sAP = "pm";
            sHour -= 12;
        } else {
            sAP = "am";
        }

        if(eHour > 12){
            eAP = "pm";
            eHour -= 12;
        } else {
            eAP = "am";
        }

        return sHour + ":" + sMin + sAP + "/" + eHour + ":" + eMin + eAP;
    }



}


