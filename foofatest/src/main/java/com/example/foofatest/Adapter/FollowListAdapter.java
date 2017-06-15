package com.example.foofatest.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foofatest.MemberFollowActivity;
import com.example.foofatest.R;
import com.example.foofatest.dto.Member;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by kosta on 2017-06-12.
 */

public class FollowListAdapter extends BaseAdapter {

    private Context context;
    private List<Member> follows;
    private LayoutInflater inflater;
    private Fragment followToId;

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
    public View getView(int position, View convertView, ViewGroup parent) {
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
}