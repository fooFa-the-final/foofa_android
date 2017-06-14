package com.example.foofatest.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by BillGates on 2017-06-14.
 */

public class ReviewImageAdpater extends PagerAdapter{
    private Context context;

    public ReviewImageAdpater(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
