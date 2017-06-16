package com.example.foofatest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foofatest.MemberFollowActivity;
import com.example.foofatest.R;
import com.example.foofatest.dto.Follow;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by kosta on 2017-06-12.
 */

public class FollowListAdapter extends BaseAdapter {

    private Context context;
    private List<Member> follows;
    private LayoutInflater inflater;
    private SharedPreferences pref;

    public FollowListAdapter(Context context, List<Member> follows) {
        this.context = context;
        this.follows = follows;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return follows.size();
    }

    @Override
    public Object getItem(int position) {
        return follows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.follow_list_item, null);
        }
            final TextView memberId = (TextView) convertView.findViewById(R.id.toId);
//            TextView followCount = (TextView) convertView.findViewById(R.id.followcount);
//            TextView reviewCount = (TextView) convertView.findViewById(R.id.reviewconut);
            TextView birthday = (TextView) convertView.findViewById(R.id.birthday);
            TextView email = (TextView) convertView.findViewById(R.id.email);
            ImageView profileImg = (ImageView) convertView.findViewById(R.id.profileImg);
            Button ufbtn = (Button)convertView.findViewById(R.id.ufbtn);
        ufbtn.setTag(position);
        ufbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(parent.getContext(), MemberFollowActivity.class);
                parent.getContext().startActivity(intent);
                Member member = follows.get(position);
                Follow follow = new Follow();
                follow.setToId(member.getMemberId());
                follow.setFromId(member.getGender());
                Log.d("1111", follow.getFromId());
                Log.d("1111", follow.getToId());

                new FollowTask().execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/follow/remove.do?fromId=" +follow.getFromId() + "&toId="  + follow.getToId());

            }
        });

            memberId.setText(follows.get(position).getMemberId());
            birthday.setText(follows.get(position).getBirthday());
            email.setText(follows.get(position).getEmail());
//            followCount.setText(follows.get(position).getFollowCount());
//            reviewCount.setText(follows.get(position).getReviewCount());
            new ImageLoadingTask(profileImg).execute(follows.get(position).getProfileImg());

            return convertView;
        }

        public static class ImageLoadingTask extends AsyncTask<String, Void, Bitmap>{
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

    private static Bitmap getRemoteImage(final URL url) {
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
                URL url = new URL((String)params[0]);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = doc.getElementsByTagName("Follow");
                for(int i=0; i<nodeList.getLength(); i++){
                    Follow follow = new Follow();
                    Node node = nodeList.item(i);
                    Element element = (Element)node;
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