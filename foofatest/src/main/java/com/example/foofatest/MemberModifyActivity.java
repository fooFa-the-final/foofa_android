package com.example.foofatest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.foofatest.dto.Member;

public class MemberModifyActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private String memberId;
    private Member member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_modify);

        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        memberId = prefs.getString("loginId", "");





    }
}
