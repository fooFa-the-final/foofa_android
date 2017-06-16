package com.example.foofatest.dto;

import java.util.List;

/**
 * Created by BillGates on 2017-06-16.
 */

public class Survey {
    private String surveyId;
    private String foodtruckId;
    private int ages;
    private int count;
    private char gender;
    private String suggestion;
    private List<SurveyReply> replies;
    private float score;
}
