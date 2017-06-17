package com.example.foofatest.Json;

import android.util.Log;

import com.example.foofatest.dto.Advertise;
import com.example.foofatest.dto.Favorite;
import com.example.foofatest.dto.Follow;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;
import com.example.foofatest.dto.Menu;
import com.example.foofatest.dto.Report;
import com.example.foofatest.dto.Review;
import com.example.foofatest.dto.Survey;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by juhee on 2017. 6. 13..
 */

public class JsonParsingControl {

    public static String POST(String url, Object javaObject) {
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String json ="";
            if(javaObject instanceof Member){
                json = gson.toJson((Member)javaObject);
            }else if(javaObject instanceof Foodtruck){
                json = gson.toJson((Foodtruck)javaObject);
            }else if(javaObject instanceof Follow){
                json = gson.toJson((Follow)javaObject);
            }else if(javaObject instanceof Advertise){
                json = gson.toJson((Advertise)javaObject);
            }else if(javaObject instanceof Menu){
                json = gson.toJson((Menu)javaObject);
            }else if(javaObject instanceof Review){
                json = gson.toJson((Review)javaObject);
            }else if(javaObject instanceof Favorite) {
                json = gson.toJson((Favorite)javaObject);
            }else if(javaObject instanceof Report) {
                json = gson.toJson((Report)javaObject);
            }else if(javaObject instanceof Survey) {
                json = gson.toJson((Survey) javaObject);
            }

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
