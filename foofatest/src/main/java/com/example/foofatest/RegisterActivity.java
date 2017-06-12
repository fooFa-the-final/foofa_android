package com.example.foofatest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class RegisterActivity extends AppCompatActivity {

    private Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText idEdit = (EditText) findViewById(R.id.reg_id);
        final EditText password1Edit = (EditText) findViewById(R.id.reg_password1);
        final EditText password2Edit = (EditText) findViewById(R.id.reg_password2);
        final EditText birthdayEdit = (EditText) findViewById(R.id.reg_birthday);
        final EditText emailEdit = (EditText) findViewById(R.id.reg_email);
        final RadioButton maleRadio = (RadioButton) findViewById(R.id.reg_genderM);

        Button submitBtn = (Button)findViewById(R.id.reg_memberBtn);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(password1Edit.getText().toString().equals(password2Edit.getText().toString())){
                    member.setMemberId(idEdit.getText().toString());
                    member.setPassword(password2Edit.getText().toString());
                    member.setBirthday(birthdayEdit.getText().toString());
                    member.setEmail(emailEdit.getText().toString());
                    if(maleRadio.isChecked()){
                        member.setGender("M");
                    }else {
                        member.setGender("F");
                    }
                    new RegisterActivity.RegisterTask().execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/memberRegister.do");
                }
            }
        });

    }


    private class RegisterTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String)params[0]);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = doc.getElementsByTagName("foodtruck");
                for(int i=0; i<nodeList.getLength(); i++){
                    Foodtruck foodtruck = new Foodtruck();
                    Node node = nodeList.item(i);

                    Element element = (Element)node;

                    foodtruck.setFoodtruckName(getTagValue("foodtruckName",element));

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    private static String getTagValue(String tag, Element element){

        if(element.getElementsByTagName(tag).item(0)==null){
            return "";
        }

        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node)nodeList.item(0);
        return  node.getNodeValue();
    }
}
