package com.example.foofatest;

import android.app.TabActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class MemberActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        TabHost mTab = getTabHost();

        TabHost.TabSpec spec;

        Intent intent;



        intent = new Intent(this, ReviewListActivity.class);

        spec = mTab.newTabSpec("OneTab").setIndicator("프로필").setContent(intent);

        mTab.addTab(spec);



        intent = new Intent(this, MemberFavListActivity.class);

        spec = mTab.newTabSpec("write").setIndicator("단골").setContent(intent);

        mTab.addTab(spec);



        intent = new Intent(this, MemberFollowActivity.class);

        spec = mTab.newTabSpec("friend").setIndicator("팔로우").setContent(intent);

        mTab.addTab(spec);



        intent = new Intent(this, MemberNewsfeedActivity.class);

        spec = mTab.newTabSpec("setting").setIndicator("뉴스피드").setContent(intent);

        mTab.addTab(spec);




    }
}
