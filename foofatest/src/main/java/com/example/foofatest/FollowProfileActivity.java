package com.example.foofatest;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foofatest.Adapter.FollowListAdapter;
import com.example.foofatest.dto.Follow;

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

public class FollowProfileActivity extends AppCompatActivity {

    private Fragment extras;
    private SharedPreferences prefs;
    private String fromId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_profile);
        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        fromId = prefs.getString("loginId", "");
        if (fromId.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        final TextView memberId = (TextView) findViewById(R.id.myId);
        TextView birthday = (TextView) findViewById(R.id.mybirthday);
        TextView email = (TextView) findViewById(R.id.myEmail);
        ImageView img = (ImageView) findViewById(R.id.myProfile);
        Button followbtn = (Button)findViewById(R.id.follow);
        Intent intent = getIntent();
        memberId.setText(intent.getStringExtra("memberId"));
        birthday.setText(intent.getStringExtra("birthday"));
        email.setText(intent.getStringExtra("email"));
        Bundle extras = getIntent().getExtras();
        Uri myUri = Uri.parse(extras.getString("profileImg"));
        img.setImageURI(myUri);
        new FollowListAdapter.ImageLoadingTask(img).execute(extras.getString("profileImg"));

        followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Follow follow = new Follow();
                follow.setFromId(fromId);
                follow.setToId(String.valueOf(memberId));
                Log.d("1111",follow.getFromId());
                Log.d("1111",follow.getToId());
                new FollowProfileActivity.FollowTask().execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/follow/follow.do?fromId=" + follow.getFromId() +"&toId="  + follow.getToId());

            }
        });


    }
    class ImageLoadingTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;

        public ImageLoadingTask(ImageView img){
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
    private class FollowTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String) params[0]);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = doc.getElementsByTagName("Follow");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Follow follow = new Follow();
                    Node node = nodeList.item(i);
                    Element element = (Element) node;
                    follow.setFromId(getTagValue("FromId", element));
                    follow.setToId(getTagValue("toId", element));

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
