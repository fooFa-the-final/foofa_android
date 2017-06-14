package com.example.foofatest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.foofatest.dto.Review;


public class ReviewDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        Intent intent = getIntent();
        Review review = (Review) intent.getExtras().get("review");

        ((TextView)findViewById(R.id.writerId)).setText(review.getWriter().getMemberId());
        ((TextView)findViewById(R.id.reviewContent)).setText(review.getContents());
        ((TextView)findViewById(R.id.reviewTruckName)).setText(review.getFoodtruck().getFoodtruckName());
        ((RatingBar)findViewById(R.id.reviewScore)).setRating(review.getScore());
    }
}
