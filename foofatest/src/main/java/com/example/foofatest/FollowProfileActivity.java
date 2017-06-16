package com.example.foofatest;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foofatest.Adapter.FollowListAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FollowProfileActivity extends AppCompatActivity {

    private Fragment extras;
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_profile);


        TextView memberId = (TextView) findViewById(R.id.myId);
        TextView birthday = (TextView) findViewById(R.id.mybirthday);
        TextView email = (TextView) findViewById(R.id.myEmail);
        ImageView img = (ImageView) findViewById(R.id.myProfile);
        Button follow = (Button)findViewById(R.id.follow);
        Intent intent = getIntent();
        memberId.setText(intent.getStringExtra("memberId"));
        birthday.setText(intent.getStringExtra("birthday"));
        email.setText(intent.getStringExtra("email"));
        Bundle extras = getIntent().getExtras();
        Uri myUri = Uri.parse(extras.getString("profileImg"));
        img.setImageURI(myUri);
        new FollowListAdapter.ImageLoadingTask(img).execute(extras.getString("profileImg"));

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MemberModifyActivity.class);
                startActivity(intent);
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
}
