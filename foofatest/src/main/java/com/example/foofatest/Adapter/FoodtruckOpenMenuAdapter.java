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
 * Created by kosta on 2017-06-12.
 */

public class FoodtruckOpenMenuAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Menu> menus;

    public FoodtruckOpenMenuAdapter(Context context, List<Menu> menus) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.menus = menus;
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
        View item = convertView;
        Holder holder = null;

        if (item == null) {
            item = inflater.inflate(R.layout.list_menu_item, null);

            holder = new Holder();

            holder.menuName = (TextView)item.findViewById(R.id.modMenuName);
            holder.menuPrice = (TextView)item.findViewById(R.id.modMenuPrice);
            holder.menuState = (TextView)item.findViewById(R.id.modMenuState);

            item.setTag(holder);
        } else {
            holder = (Holder)item.getTag();
        }

        holder.menuName.setText(menus.get(position).getMenuName());
        holder.menuPrice.setText(menus.get(position).getPrice() + "");

        if(menus.get(position).isMenuState()) {
            holder.menuState.setText("판매중");
        } else {
            holder.menuState.setText("매진");
        }
        return item;
    }

    private class Holder{
        TextView menuName;
        TextView menuPrice;
        TextView menuState;
    }

    public void add(int position, Menu menu){
        menus.add(position, menu);
        notifyDataSetChanged();
    }


    public void modify(int positon, Menu menu){
        menus.remove(positon);
        menus.add(positon, menu);
        notifyDataSetChanged();
    }

    public void delete(int position){
        menus.remove(position);
        notifyDataSetChanged();
    }
}
