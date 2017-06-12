package com.example.foofatest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
                    Toast.makeText(RegisterActivity.this, "회원가입 시도.", Toast.LENGTH_SHORT).show();
                    member.setMemberId(idEdit.getText().toString());
                    member.setPassword(password2Edit.getText().toString());
                    member.setBirthday(birthdayEdit.getText().toString());
                    member.setEmail(emailEdit.getText().toString());
                    if(maleRadio.isChecked()){
                        member.setGender("M");
                    }else {
                        member.setGender("F");
                    }
                    new RegisterActivity.RegisterTask().execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/memberRegister.do", member);
                }else {
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private class RegisterTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
                Member member = (Member) params[1];

                return POST((String) params[0], member);
        }

        @Override
        protected void onPostExecute(String string) {
            Log.d("life", string+" !@#");
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
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

    public static String POST(String url, Member member) {
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String json = gson.toJson(member);

            Log.d("InputStream", jsonObject.toString());
            // convert JSONObject to JSON to String

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // Set some headers to inform server about the type of the content
            httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("Content-type", "application/json");

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("utf-8"));
            os.flush();
            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if (is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpCon.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
