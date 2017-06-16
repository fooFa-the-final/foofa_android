package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.foofatest.dto.Report;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        final RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup);
        Button submit = (Button)findViewById(R.id.submit);
        final EditText reason = (EditText) findViewById(R.id.reason);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.direct){
                    setUseableEditText(reason, true);
                } else {
                    setUseableEditText(reason, false);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report report = new Report();
                SharedPreferences login = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
                String id = login.getString("loginId", "");
                Intent intent = getIntent();
                Log.d("log", "login id : " + id);
                report.setMemberId(id);
                report.setReviewId(intent.getExtras().get("reviewId").toString());
                Log.d("log", "reviewId : " + report.getReviewId());
                if(rg.getCheckedRadioButtonId() == R.id.direct){
                    report.setReason(reason.getText().toString());
                } else {
                    int rbid = rg.getCheckedRadioButtonId();
                    RadioButton rb = (RadioButton)findViewById(rbid);
                    report.setReason(rb.getText().toString());
                }

                intent.putExtra("report", report);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setUseableEditText(EditText et, boolean useable){
        et.setClickable(useable);
        et.setEnabled(useable);
        et.setFocusable(useable);
        et.setFocusableInTouchMode(useable);
    }
}
