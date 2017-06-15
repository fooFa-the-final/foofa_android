package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foofatest.Json.JsonParsingControl;
import com.example.foofatest.dto.Advertise;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AdvertiseApplyActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    DatePicker mdate;
    TextView advtext;
    RadioGroup radioGroup;
    Date startDay;
    String period;
    int realDay;
    String sellerId;
    private Advertise advertise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_apply);
        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        sellerId = prefs.getString("loginId", "");
        if (sellerId.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        mdate = (DatePicker) findViewById(R.id.startAd);
        advtext = (TextView) findViewById(R.id.advDate);


        mdate.init(mdate.getYear(), mdate.getMonth(), mdate.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        advtext.setText(String.format("%d%d%d", year, monthOfYear + 1, dayOfMonth));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
                        String strDate = format.format(calendar.getTime());
                        SimpleDateFormat transFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            startDay = transFormat1.parse(strDate);
                            //아주굿
//                            Log.d("1111", String.valueOf(to1));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });


        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radio_btn = (RadioButton) findViewById(checkedId);
                RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup); // 라디오그룹 객체 맵핑
                RadioButton selectedRdo = (RadioButton) findViewById(rg.getCheckedRadioButtonId()); // rg 라디오그룹의 체크된(getCheckedRadioButtonId) 라디오버튼 객체 맵핑
                period = selectedRdo.getText().toString(); // 해당 라디오버튼 객체의 값 가져오기
            }
        });

        findViewById(R.id.advbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Advertise advertise = new Advertise();
                Log.d("1111", String.valueOf(startDay));
                period = period.substring(0, period.length() - 1);
                realDay = Integer.parseInt(period);

                Log.d("1111", String.valueOf(realDay));
                advertise.setAdvId(sellerId);
//                advertise.setStartdate(startDay);
                advertise.setPeriod(realDay);

                new AdvertiseApplyActivity.AdvertiseTask().execute("경-로", advertise);

            }
        });

    }

    private class AdvertiseTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            Advertise advertise = (Advertise) params[1];
            return JsonParsingControl.POST((String) params[0], advertise);
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = null;
            intent = new Intent(AdvertiseApplyActivity.this, TruckInfoActivity.class);
            startActivity(intent);
        }

    }
}