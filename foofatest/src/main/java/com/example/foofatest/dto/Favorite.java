package com.example.foofatest.dto;

/**
 * Created by kosta on 2017-06-15.
 */

public class Favorite {

    private String foodtruckId;
    private String memberId;

    public String getFoodtruckId() {
        return foodtruckId;
    }

    public void setFoodtruckId(String foodtruckId) {
        this.foodtruckId = foodtruckId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
