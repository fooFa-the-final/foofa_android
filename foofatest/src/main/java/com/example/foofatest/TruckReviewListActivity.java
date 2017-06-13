package com.example.foofatest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ListView;

import com.example.foofatest.Adapter.FoodtruckDetailAdapter;
import com.example.foofatest.Adapter.FoodtruckDetailMenuAdapter;
import com.example.foofatest.Adapter.TruckReviewAdapter;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;
import com.example.foofatest.dto.Menu;
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
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TruckReviewListActivity extends AppCompatActivity {


    private SharedPreferences prefs;
    private Button truckStatus;
    private LayoutInflater inflater;
    private String loginUserId;
    private FoodtruckDetailAdapter adapter;
    private TruckReviewAdapter truckReviewAdapter;
    private FoodtruckDetailMenuAdapter foodtruckDetailMenuAdapter;

    private List<Foodtruck> foodtrucks;
    private List<Menu> menus1;
    private List<Review> reviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_review_list);

        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        loginUserId= prefs.getString("id", "");
//        Intent intent = new Intent();
//        final String id = intent.getExtras().getString("loginUserId".toString());
        reviews = new ArrayList<>();
        truckReviewAdapter = new TruckReviewAdapter(this, reviews);
        new ReviewDetialTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/review/list/turck.do?id="+loginUserId);
        final ListView reviewlist = (ListView) findViewById(R.id.truckReviewListlist);
        reviewlist.setAdapter(truckReviewAdapter);

    }


    public class ReviewDetialTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String) params[0]);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));
                NodeList nodeList = doc.getElementsByTagName("review");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Review review = new Review();
                    Foodtruck foodtruck = new Foodtruck();
                    Member writer = new Member();
                    Node node = nodeList.item(i);
                    Element element = (Element) node;
                    review.setContents(getTagValue("contents", element));
                    //review.setScore(Float.valueOf(getTagValue("score", element)));
                    review.setRecommand(Integer.parseInt(getTagValue("recommand", element)));
                    review.setReviewId(getTagValue("reviewId", element));
//                    review.setWriteDate(DataFormatException(getTagValue("writeDate", element)));
                    writer.setMemberId(getTagValue("memberId", element));
                    foodtruck.setFoodtruckId(getTagValue("foodtruckId", element));
                    foodtruck.setSellerId(getTagValue("sellerId", element));
                    foodtruck.setFoodtruckName(getTagValue("foodtruckName", element));
                    foodtruck.setOperationTime(getTagValue("operationTime", element));
                    foodtruck.setSpot(getTagValue("spot", element));
                    foodtruck.setNotice(getTagValue("notice", element));
                    foodtruck.setLocation(getTagValue("location", element));
                    foodtruck.setCategory1(getTagValue("category1", element));
                    foodtruck.setCategory2(getTagValue("category2", element));
                    foodtruck.setCategory3(getTagValue("category3", element));
                    foodtruck.setCard(getTagBoolean("card", element));
                    foodtruck.setParking(getTagBoolean("parking", element));
                    foodtruck.setDrinking(getTagBoolean("drinking", element));
                    foodtruck.setCatering(getTagBoolean("catering", element));
                    foodtruck.setState(getTagBoolean("state", element));
                    foodtruck.setFavoriteCount(Integer.parseInt(getTagValue("favoriteCount", element)));
                    foodtruck.setReviewCount(Integer.parseInt(getTagValue("reviewCount", element)));
                    //review.setScore(Float.valueOf((getTagValue("score", element))));
                    NodeList nodeList1 = doc.getElementsByTagName("menus");
                    foodtruck.setFoodtruckImg("http://foofa.crabdance.com:8888/FoodtruckFinderProject/resources/img/food/"+getTagValue("foodtruckImg", element));
                    review.setWriter(writer);
                    review.setFoodtruck(foodtruck);
                    reviews.add(review);
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
            adapter.notifyDataSetChanged();
        }
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node) nodeList.item(0);
        if(value.getNodeValue() == null) {
            Objects.equals(value.getNodeValue(), "");
        }
        return value.getNodeValue();
    }

    private static Boolean getTagBoolean(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node) nodeList.item(0);
        return value.hasChildNodes();
//되는지 모르겠따;
    }


}
