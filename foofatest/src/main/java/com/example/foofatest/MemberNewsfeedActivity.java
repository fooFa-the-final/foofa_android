package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.foofatest.Adapter.ReviewListAdapter;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Image;
import com.example.foofatest.dto.Member;
import com.example.foofatest.dto.Review;

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

public class MemberNewsfeedActivity extends AppCompatActivity {

    private List<Review> data;
    private ListView listView;
    private SharedPreferences prefs;
    private ReviewListAdapter adapter;
    private String fromId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_newsfeed);
        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        fromId = prefs.getString("loginId", "");
        if (fromId.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        data = new ArrayList<>();
        new ReviewLoadingTask().execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/review/followerReview.do?fromId=" + fromId);
        listView = (ListView) findViewById(R.id.list);
        adapter = new ReviewListAdapter(data, this);
        listView.setAdapter(adapter);


    }
    private class ReviewLoadingTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = document.getElementsByTagName("review");
                for(int i=0;i<nodeList.getLength();i++){
                    Review review = new Review();
                    Foodtruck foodtruck = new Foodtruck();
                    Member writer = new Member();
                    List<Image> images = new ArrayList<>();
                    Image image = new Image();
                    Node node = nodeList.item(i);
                    Element element = (Element)node;
                    String src = "http://foofa.crabdance.com:8888/FoodtruckFinderProject/resources/img/reviewImg/"+getTagValue("filename", element);
                    image.setFilename(src);
                    images.add(image);
                    review.setReviewId(getTagValue("reviewId", element));
                    foodtruck.setFoodtruckId(getTagValue("foodtruckId", element));
                    foodtruck.setFoodtruckName(getTagValue("foodtruckName", element));
                    review.setContents(getTagValue("contents", element));
                    writer.setMemberId(getTagValue("memberId", element));
                    NodeList list = element.getElementsByTagName("score").item(1).getChildNodes();
                    review.setScore(Integer.parseInt(list.item(0).getNodeValue()));
                    review.setFoodtruck(foodtruck);
                    review.setWriter(writer);
                    review.setImages(images);
                    data.add(review);
                }
                Log.d("1111", ""+data.size());
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
            adapter.notifyDataSetChanged();
        }
    }

    private static String getTagValue(String tag, Element element){
        try {
            NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node value = (Node)list.item(0);
            return value.getNodeValue();
        } catch(NullPointerException e){
            if(tag == "filename"){
                return "noimage.jpg";
            }
        }
        return "";
    }

}
