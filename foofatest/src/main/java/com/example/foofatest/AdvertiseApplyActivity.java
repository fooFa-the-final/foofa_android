package com.example.foofatest;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;


public class AdvertiseApplyActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    DatePicker mdate;
    TextView advtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_apply);

        mdate = (DatePicker)findViewById(R.id.startAd);
        advtext = (TextView)findViewById(R.id.advDate);

        mdate.init(mdate.getYear(),mdate.getMonth(),mdate.getDayOfMonth(),
                new DatePicker.OnDateChangedListener(){

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        advtext.setText(String.format("%d%d%d",year,monthOfYear +1,dayOfMonth ));
                        Toast.makeText(AdvertiseApplyActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}