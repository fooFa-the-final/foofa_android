package com.example.foofatest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.foofatest.R;
import com.example.foofatest.dto.Menu;

import java.util.List;

/**
 * Created by kosta on 2017-06-10.
 */

public class FoodtruckDetailMenuAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Menu> menus;


    public FoodtruckDetailMenuAdapter(Context context, List<Menu> menu) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.truck_menu, null);
        }
        TextView menuName = (TextView) convertView.findViewById(R.id.menuName);
        TextView menuPrice = (TextView) convertView.findViewById(R.id.menuPrice);
        TextView menuState = (TextView) convertView.findViewById(R.id.menuState);
        menuName.setText(menus.get(position).getMenuName());
        if(menus.get(position).isMenuState() == true) {
            menuState.setText("판매중");
        } else {
            menuState.setText("매진");
        }
        menuPrice.setText(String.valueOf(menus.get(position).getPrice()));
        return convertView;
    }
}
