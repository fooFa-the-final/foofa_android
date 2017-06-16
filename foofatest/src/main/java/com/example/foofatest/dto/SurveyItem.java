package com.example.foofatest.dto;

import java.io.Serializable;

/**
 * Created by BillGates on 2017-06-16.
 */

public class SurveyItem implements Serializable{
    private String itemId;
    private String question;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "SurveyItem{" +
                "itemId='" + itemId + '\'' +
                ", question='" + question + '\'' +
                '}';
    }
}
