package com.example.foofatest.dto;

/**
 * Created by BillGates on 2017-06-16.
 */

public class SurveyReply {

    private String itemId;
    private int score;
    private String surveyId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    @Override
    public String toString() {
        return "SurveyReply{" +
                "itemId='" + itemId + '\'' +
                ", score=" + score +
                ", surveyId='" + surveyId + '\'' +
                '}';
    }
}
