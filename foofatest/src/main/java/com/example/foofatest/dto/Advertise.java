package com.example.foofatest.dto;

/**
 * Created by kosta on 2017-06-10.
 */

public class Advertise {

    private String advId;
    private String sellerId;
    private int period;
    private int approve;
//    private java.sql.Date startdate;
    private String startdate;

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getAdvId() {
        return advId;
    }

    public void setAdvId(String advId) {
        this.advId = advId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getApprove() {
        return approve;
    }

    public void setApprove(int approve) {
        this.approve = approve;
    }

//    public java.sql.Date getStartDate() {
//        return startdate;
//    }
//
//    public void setStartDate(java.sql.Date startDate) {
//        this.startdate = startDate;
//    }


}
