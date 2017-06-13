package com.example.foofatest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.foofatest.Adapter.ReviewListAdapter;
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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ReviewListActivity extends AppCompatActivity {

    private List<Review> data;
    private ReviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        ListView list = (ListView) findViewById(R.id.reviewList);

        final ReviewLoadingTask task = new ReviewLoadingTask();
        task.execute("http://localhost:8888/FoodtruckFinderProject/mobile/review/member/list.do?memberId=momo");
    }

    private class ReviewLoadingTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = document.getElementsByTagName("Review");
                for(int i=0;i<nodeList.getLength();i++){
                    Review review = new Review();

                    Node node = nodeList.item(i);
                    Element element = (Element)node;

                    review.setReviewId(getTagValue("reviewId", element));
                    review.getFoodtruck().setFoodtruckId(getTagValue("foodtruckId", element));
                    review.getFoodtruck().setFoodtruckName(getTagValue("foodtruckName", element));
                    review.setContents(getTagValue("contents", element));
                    review.getWriter().setMemberId(getTagValue("memberId", element));
                    review.setScore(Integer.parseInt(getTagValue("score", element)));
                    data.add(review);
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
            adapter.notifyDataSetChanged();
        }
    }

    private static String getTagValue(String tag, Element element){
        NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node)list.item(0);
        return value.getNodeValue();
    }
}
