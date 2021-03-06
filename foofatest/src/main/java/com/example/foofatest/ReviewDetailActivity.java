package com.example.foofatest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foofatest.Json.JsonParsingControl;
import com.example.foofatest.dto.Report;
import com.example.foofatest.dto.Review;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class ReviewDetailActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        Intent intent = getIntent();
        final Review review = (Review) intent.getExtras().get("review");

        ((TextView)findViewById(R.id.writerId)).setText(review.getWriter().getMemberId());
        ((TextView)findViewById(R.id.reviewContent)).setText(review.getContents());
        ((TextView)findViewById(R.id.reviewTruckName)).setText(review.getFoodtruck().getFoodtruckName());
        ((TextView)findViewById(R.id.recommandCount)).setText(review.getRecommand()+"");
        ((RatingBar)findViewById(R.id.reviewScore)).setRating(review.getScore());
        ImageLoadingTask task = new ImageLoadingTask();
        task.execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/review/imageList.do?reviewId=" + review.getReviewId());

        findViewById(R.id.reviewReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewDetailActivity.this, ReportActivity.class);
                intent.putExtra("reviewId", review.getReviewId());
                startActivityForResult(intent, 0);
            }
        });

    }

    private class ImageLoadingTask extends AsyncTask<String, Void, Void>{
        List<String> taskimage = new ArrayList<>();
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = document.getElementsByTagName("image");
                for(int i=0;i<nodeList.getLength();i++) {
                    Node node = (Node)nodeList.item(i);
                    Element element = (Element)node;

                    NodeList list = element.getElementsByTagName("filename").item(0).getChildNodes();
                    Node value = list.item(0);
                    taskimage.add("http://106.242.203.67:8888/FoodtruckFinderProject/resources/img/reviewImg/" +value.getNodeValue());
                    Log.d("log", "value : " + value.getNodeValue());
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            viewPager = (ViewPager)findViewById(R.id.reviewImage);

            myPagerAdapter = new MyPagerAdapter(getLayoutInflater(), taskimage);
            viewPager.setAdapter(myPagerAdapter);
        }
    }

    private class MyPagerAdapter extends PagerAdapter{
        LayoutInflater inflater;
        List<String> images;
        public MyPagerAdapter(LayoutInflater inflater, List<String> images) {
            this.inflater = inflater;
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.review_image_view, null);

            ImageView img = (ImageView)view.findViewById(R.id.img_viewpager_childImage);
            new ImageTask(img).execute(images.get(position));
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    private class ImageTask extends AsyncTask<String, Void, Bitmap>{

        private final WeakReference<ImageView> imageViewReference;
        public ImageTask(ImageView img){
            this.imageViewReference = new WeakReference<ImageView>(img);
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return getRemoteImage(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(isCancelled()){
                bitmap = null;
            }

            if(imageViewReference != null){
                ImageView imageView = imageViewReference.get();
                if(imageView != null){
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private Bitmap getRemoteImage(final URL url){
        Bitmap bitmap = null;
        URLConnection conn;

        try {
            conn = url.openConnection();
            conn.connect();

            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            ReportTask task = new ReportTask();
            task.execute(data.getExtras().get("report"));
        }
    }

    private class ReportTask extends AsyncTask<Object, Void, String>{

        @Override
        protected String doInBackground(Object... params) {
            //URL url = new URL("http://192.168.0.87:8888/FoodtruckFinderProject/mobile/report/create.do");
            Report report = (Report)params[0];
            JsonParsingControl jsonParsingControl = new JsonParsingControl();
            //jsonParsingControl.POST("http://192.168.0.87:8888/FoodtruckFinderProject/mobile/report/create.do", report);

            return jsonParsingControl.POST("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/report/create.do", report);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("log", "result : " + result);
            if(result.equals("true")) {
                Toast.makeText(ReviewDetailActivity.this, "신고가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ReviewDetailActivity.this, "이미 신고 된 리뷰입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
