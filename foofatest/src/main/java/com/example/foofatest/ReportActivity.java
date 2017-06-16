package com.example.foofatest;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

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
    }

    private void setUseableEditText(EditText et, boolean useable){
        et.setClickable(useable);
        et.setEnabled(useable);
        et.setFocusable(useable);
        et.setFocusableInTouchMode(useable);
    }
}
