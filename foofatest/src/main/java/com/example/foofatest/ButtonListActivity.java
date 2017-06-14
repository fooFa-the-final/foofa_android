package com.example.foofatest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;
import com.example.foofatest.dto.Menu;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ButtonListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_list);

        Log.d("life", getIntent().getStringExtra("loginUserId")+":");


        findViewById(R.id.favoriteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, MemberFavListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.memberRegisterBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, MemberRegisterActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.truckOpenBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, TruckOpenActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.foodtruckInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, TruckInfoActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.memberReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonListActivity.this, ReviewListActivity.class);
                startActivity(intent);
            }
        });




    }



    public class MenuDetailTask extends AsyncTask<String, Void, Void> {

        Member member ;



        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String) params[0]);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));
                NodeList nodeList = doc.getElementsByTagName("member");
                Member member = new Member();
                    Node node = nodeList.item(0);
                    Element element = (Element) node;
                    member.setMemberId(getTagValue("memberId", element));
                    member.setBirthday(getTagValue("birthday", element));
                    member.setFollowCount(Integer.parseInt(getTagValue("followCount", element)));
                member.setReviewCount(Integer.parseInt(getTagValue("reviewCount", element)));
                member.setProfileImg("http://foofa.crabdance.com:8888/FoodtruckFinderProject/resources/img/upload/" + getTagValue("profileImg", element));
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

    }

    private static String getTagValue(String tag, Element element) {
        try {
            NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node value = (Node) list.item(0);
            return value.getNodeValue();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return "";
    }

    private class ImageLoadingTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewWeakReference;

        public ImageLoadingTask(ImageView img) {
            this.imageViewWeakReference = new WeakReference<ImageView>(img);
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
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewWeakReference != null) {
                ImageView imageView = imageViewWeakReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private Bitmap getRemoteImage(final URL url) {
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

}
