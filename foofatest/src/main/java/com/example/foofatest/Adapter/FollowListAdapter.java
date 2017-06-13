package com.example.foofatest.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.foofatest.R;
import com.example.foofatest.dto.Follow;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by kosta on 2017-06-12.
 */

public class FollowListAdapter extends BaseAdapter {

    private Context context;
    private List<Follow> follows;
    private LayoutInflater inflater;

    public FollowListAdapter(Context context, List<Follow> follows) {
        this.context = context;
        this.follows = follows;
        this.inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return follows.size();
    }

    @Override
    public Object getItem(int position) {
        return follows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.follow_list_item, null);
            TextView toId = (TextView) convertView.findViewById(R.id.toId);
            TextView followCount = (TextView) convertView.findViewById(R.id.followcount);

            toId.setText(follows.get(position).getToId());
            followCount.setText(follows.get(position).getFollowCount());


        }

        return convertView;
    }



}