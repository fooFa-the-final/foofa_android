package com.example.foofatest.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Rating;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foofatest.R;
import com.example.foofatest.ReportActivity;
import com.example.foofatest.ReviewListActivity;
import com.example.foofatest.dto.Review;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by BillGates on 2017-06-13.
 */

public class ReviewListAdapter extends BaseAdapter{
    private List<Review> data;
    private Context context;
    private LayoutInflater inflater;
    private String rec;
    public ReviewListAdapter(List<Review> data, Context context) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_review_list, null);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.reviewImage);
        TextView writerId = (TextView) convertView.findViewById(R.id.writerId);
        TextView truckName = (TextView) convertView.findViewById(R.id.reviewTruckName);
        TextView reviewContent = (TextView) convertView.findViewById(R.id.reviewContent);
        final TextView recommandCount = (TextView) convertView.findViewById(R.id.recommendCount);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar1);
        writerId.setText(data.get(position).getReviewId());
        truckName.setText(data.get(position).getFoodtruck().getFoodtruckName());
        String contents = data.get(position).getContents();
        if(contents.length() > 50){
            contents = contents.substring(0, 49);
            contents = contents + "...";
        }
        reviewContent.setText(contents);

        recommandCount.setText("" + data.get(position).getRecommand());
        ratingBar.setRating(data.get(position).getScore());
        new ImageLoadingTask(image).execute(data.get(position).getImages().get(0).getFilename());

        ImageButton btn = (ImageButton)convertView.findViewById(R.id.createRecommand);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecommandTask task = new RecommandTask();
                Log.d("log", data.get(position).getReviewId());
                task.execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/review/recommand.do?memberId=nayeon&reviewId="+data.get(position).getReviewId(), recommandCount);

            }
        });

        return convertView;
    }

    private class RecommandTask extends AsyncTask<Object, Void, String>{
        private TextView textView;
        @Override
        protected String doInBackground(Object... params) {
            HttpURLConnection http = null;
            InputStream is = null;
            String checkStr = null;
            textView = (TextView)params[1];
            try{
                URL url = new URL((String)params[0]);

                http = (HttpURLConnection)url.openConnection();
                http.setRequestMethod("GET");
                http.connect();

                is = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                checkStr = reader.readLine();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return checkStr;
        }
        @Override
        protected void onPostExecute(String s) {
            int recommand = Integer.parseInt(textView.getText().toString());
            if(s.equals("true")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("추천");
                alert.setMessage("추천 되었습니다.");
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();
                recommand += 1;
            }
            else {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("추천");
                alert.setMessage("추천 해제 되었습니다.");
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();
                recommand -= 1;
            }
            textView.setText("" + recommand);
        }
    }
    private class ImageLoadingTask extends AsyncTask<String, Void, Bitmap>{

        private final WeakReference<ImageView> imageViewReference;
        public ImageLoadingTask(ImageView img){
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 16;
            bitmap = BitmapFactory.decodeStream(bis, new Rect(1, 1, 1, 1), options);
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
