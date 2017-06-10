package com.example.foofatest.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kosta on 2017-06-10.
 */

public class Foodtruck implements Serializable {

    private String foodtruckId;
    private String sellerId;
    private String foodtruckName;
    private String foodtruckImg;
    private String operationTime;
    private String spot;
    private String notice;
    private String location;
    private String category1;
    private String category2;
    private String category3;
    private boolean card;
    private boolean parking;
    private boolean drinking;
    private boolean catering;
    private boolean state;
    private int favoriteCount;
    private int reviewCount;
    private double score;
    private List<Menu> menus;

    public String getFoodtruckId() {
        return foodtruckId;
    }

    public void setFoodtruckId(String foodtruckId) {
        this.foodtruckId = foodtruckId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getFoodtruckName() {
        return foodtruckName;
    }

    public void setFoodtruckName(String foodtruckName) {
        this.foodtruckName = foodtruckName;
    }

    public String getFoodtruckImg() {
        return foodtruckImg;
    }

    public void setFoodtruckImg(String foodtruckImg) {
        this.foodtruckImg = foodtruckImg;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    public String getSpot() {
        return spot;
    }

    public void setSpot(String spot) {
        this.spot = spot;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public boolean isCard() {
        return card;
    }

    public void setCard(boolean card) {
        this.card = card;
    }

    public boolean isParking() {
        return parking;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }

    public boolean isDrinking() {
        return drinking;
    }

    public void setDrinking(boolean drinking) {
        this.drinking = drinking;
    }

    public boolean isCatering() {
        return catering;
    }

    public void setCatering(boolean catering) {
        this.catering = catering;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
