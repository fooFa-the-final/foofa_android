package com.example.foofatest.dto;

import java.io.Serializable;

/**
 * Created by kosta on 2017-06-10.
 */

public class Menu implements Serializable{

    private String menuId;
    private String menuName;
    private int price;
    private boolean menuState;
    private String foodtruckId;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isMenuState() {
        return menuState;
    }

    public void setMenuState(boolean menuState) {
        this.menuState = menuState;
    }

    public String getFoodtruckId() {
        return foodtruckId;
    }

    public void setFoodtruckId(String foodtruckId) {
        this.foodtruckId = foodtruckId;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "menuId='" + menuId + '\'' +
                ", menuName='" + menuName + '\'' +
                ", price=" + price +
                ", menuState=" + menuState +
                ", foodtruckId='" + foodtruckId + '\'' +
                '}';
    }
}
