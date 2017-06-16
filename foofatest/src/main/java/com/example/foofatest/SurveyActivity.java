package com.example.foofatest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SurveyActivity extends AppCompatActivity {
    private List<SurveyItem> surveyItem;
    private LinearLayout container = null;
    private Button btn = null;
    private List<RadioGroup> rgs = null;
    private List<RadioButton[]> rbs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

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
                    String reviewId = intent.getExtras().get("reviewId").toString();
                    Log.d("log", "reviewId : " + reviewId);
                    List<SurveyReply> replies = new ArrayList<SurveyReply>();
                    for(RadioGroup rg : rgs){

                    }
                } else {
                    Toast.makeText(SurveyActivity.this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createForm(){
        for(int i=0;i<surveyItem.size();i++){
            TextView textView = new TextView(SurveyActivity.this);
            textView.setText(surveyItem.get(i).getQuestion());
            container.addView(textView);
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
            rbs.add(rb);
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
}
