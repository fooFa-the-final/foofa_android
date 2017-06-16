package com.example.foofatest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foofatest.dto.Review;
import com.example.foofatest.dto.Survey;
import com.example.foofatest.dto.SurveyItem;
import com.example.foofatest.dto.SurveyReply;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SurveyActivity extends AppCompatActivity {
    private List<SurveyItem> surveyItem;
    List<SurveyReply> replies;
    private Survey survey;
    private LinearLayout container = null;
    private Button btn = null;
    private List<RadioGroup> rgs = null;
    private List<Integer> scores = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        survey = new Survey();
        container = (LinearLayout) findViewById(R.id.linear);
        btn = (Button)findViewById(R.id.submit);
        /*TextView q1 = new TextView(this);
        q1.setText("질문 1");
        container.addView(q1);*/

        /*RadioGroup rg = new RadioGroup(this);
        final RadioButton[] rb = new RadioButton[5];*/

        /*rg.setOrientation(RadioGroup.HORIZONTAL);
        for(int i=0;i<5;i++){
            rb[i] = new RadioButton(this);
            rb[i].setText("ddd");
            rg.addView(rb[i]);
        }
        container.addView(rg);
        */

        final SurveyTask surveyTask = new SurveyTask();
        surveyTask.execute();
        final Intent intent = getIntent();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(surveyTask.getStatus() == AsyncTask.Status.FINISHED){
                    //String reviewId = intent.getExtras().get("reviewId").toString();
                    String reviewId = "R1";
                    Log.d("log", "reviewId : " + reviewId);
                    replies = new ArrayList<SurveyReply>();
                    Log.d("log", "rgs Size : " + rgs.size());
                    for(int i=0;i<rgs.size();i++){
                        Log.d("log", "i : " + i);
                        SurveyReply reply = new SurveyReply();
                        reply.setItemId(surveyItem.get(i).getItemId());
                        int id = rgs.get(i).getCheckedRadioButtonId();
                        Log.d("log", "selectIndex : " + id);
                        //RadioButton radb = (RadioButton)findViewById(id);
                        int index = rgs.get(i).indexOfChild(findViewById(rgs.get(i).getCheckedRadioButtonId()));
                        //reply.setScore(Integer.parseInt(radb.getText().toString()));
                        reply.setScore(index+1);
                        Log.d("log", reply.toString());
                        replies.add(reply);
                    }
                    ReviewTask reviewTask = new ReviewTask();
                    reviewTask.execute(reviewId);
                } else {
                    Toast.makeText(SurveyActivity.this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createForm(){
        Log.d("log", "item size : " +surveyItem.size());
        rgs = new ArrayList<>();
        for(int i=0;i<surveyItem.size();i++){
            TextView textView = new TextView(SurveyActivity.this);
            textView.setText(surveyItem.get(i).getQuestion());
            container.addView(textView);
            //rbs = new ArrayList<>();
            RadioGroup rg = new RadioGroup(SurveyActivity.this);
            final RadioButton[] rb = new RadioButton[5];
            rg.setOrientation(RadioGroup.HORIZONTAL);

            for(int j=0;j<5;j++){
                rb[j] = new RadioButton(this);
                rb[j].setText(String.valueOf(j+1));
                rg.addView(rb[j]);
            }

            container.addView(rg);
            rgs.add(rg);
            Log.d("log", "added!");
        }



    }

    private class SurveyTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            surveyItem = new ArrayList<>();
            try {
                URL url = new URL("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/survey/form.do");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = document.getElementsByTagName("surveyItem");
                for(int i=0;i<nodeList.getLength();i++){
                    Node node = nodeList.item(i);
                    Element element = (Element)node;

                    NodeList list = element.getElementsByTagName("itemId").item(0).getChildNodes();
                    Node value = list.item(0);
                    SurveyItem item = new SurveyItem();
                    item.setItemId(value.getNodeValue());

                    list = element.getElementsByTagName("question").item(0).getChildNodes();
                    value = list.item(0);
                    item.setQuestion(value.getNodeValue());
                    Log.d("log", item.toString());
                    surveyItem.add(item);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            createForm();
        }

    }

    private class ReviewTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            String reviewId = params[0];
            try {
                URL url = new URL("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/review/detail.do?reviewId="+reviewId);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = document.getElementsByTagName("review");
                for(int i=0;i<nodeList.getLength();i++){
                    Node node = nodeList.item(i);
                    Element element = (Element)node;

                    survey.setAges(calAges(getTagValue("birthday", element)));
                    survey.setFoodtruckId(getTagValue("foodtruckId", element));
                    survey.setGender(getTagValue("gender", element).charAt(0));
                    survey.setReplies(replies);


                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            EditText suggestion = (EditText)findViewById(R.id.suggestion);
            survey.setSuggestion(suggestion.getText().toString());
            Log.d("log", "survey : " + survey.toString());
        }
    }

    private int calAges(String birth){
        birth = birth.substring(2, 4);
        Date date = new Date();
        int year = date.getYear() + 1;
        int age = year - Integer.parseInt(birth);
        int ageRange = age/10 * 10;
        return ageRange;
    }
    private static String getTagValue(String tag, Element element){
        NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node)list.item(0);
        return value.getNodeValue();
    }
}
