package com.example.foofatest.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.foofatest.R;
import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Review;

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

public class TruckReviewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Review> reviews;

    public TruckReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        this.inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
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

        writerId.setText(reviews.get(position).getReviewId());
        truckName.setText(reviews.get(position).getFoodtruck().getFoodtruckId());
        reviewContent.setText(reviews.get(position).getContents());
        ratingBar.setRating(reviews.get(position).getScore());
        new ImageLoadingTask(image).execute(reviews.get(position).getImages().get(0).getFilename());
        
        return convertView;
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
