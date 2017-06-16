package com.example.foofatest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SurveyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        LinearLayout container = (LinearLayout) findViewById(R.id.linear);

        TextView q1 = new TextView(this);
        q1.setText("질문 1");
        container.addView(q1);

        RadioGroup rg = new RadioGroup(this);
        final RadioButton[] rb = new RadioButton[5];

        rg.setOrientation(RadioGroup.HORIZONTAL);
        for(int i=0;i<5;i++){
            rb[i] = new RadioButton(this);
            rb[i].setText("ddd");
            rg.addView(rb[i]);
        }
        container.addView(rg);
    }
}
