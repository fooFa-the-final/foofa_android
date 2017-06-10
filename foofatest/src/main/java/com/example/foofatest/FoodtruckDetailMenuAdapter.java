package com.example.foofatest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

/**
 * Created by kosta on 2017-06-10.
 */

public class FoodtruckDetailMenuAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private List<Menu> menus;

    public FoodtruckDetailMenuAdapter(Context context, LayoutInflater inflater, List<Menu> menu) {
        this.context = context;
        this.inflater = inflater;
        this.menus = menu;
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Object getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    if(convertView == null) {
        convertView = inflater.inflate(R.layout.truck_menu, null);
    }
        return null;
    }
}
