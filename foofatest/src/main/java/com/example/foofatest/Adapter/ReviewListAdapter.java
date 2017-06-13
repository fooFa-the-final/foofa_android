package com.example.foofatest.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Rating;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.foofatest.R;
import com.example.foofatest.dto.Review;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_review_list, null);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.reviewImage);
        TextView writerId = (TextView) convertView.findViewById(R.id.writerId);
        TextView truckName = (TextView) convertView.findViewById(R.id.reviewTruckName);
        TextView reviewContent = (TextView) convertView.findViewById(R.id.reviewContent);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar1);

        writerId.setText(data.get(position).getReviewId());
        truckName.setText(data.get(position).getFoodtruck().getFoodtruckName());
        reviewContent.setText(data.get(position).getContents());
        ratingBar.setRating(data.get(position).getScore());
        new ImageLoadingTask(image).execute(data.get(position).getImg1());

        return convertView;
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
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
