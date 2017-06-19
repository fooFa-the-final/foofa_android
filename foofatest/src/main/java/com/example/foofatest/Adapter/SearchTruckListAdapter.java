package com.example.foofatest.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.foofatest.R;
import com.example.foofatest.dto.Foodtruck;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by kosta on 2017-06-15.
 */

public class SearchTruckListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Foodtruck> foodtrucks;

    public SearchTruckListAdapter(Context context, List<Foodtruck> foodtrucks) {
        this.context = context;
        this.foodtrucks = foodtrucks;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return foodtrucks.size();
    }

    @Override
    public Object getItem(int position) {
        return foodtrucks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView name = (TextView)convertView.findViewById(R.id.listTruckName);
        TextView category = (TextView)convertView.findViewById(R.id.listTruckCategory);
        TextView location = (TextView)convertView.findViewById(R.id.listTruckLocation);
        TextView favCount = (TextView)convertView.findViewById(R.id.listTruckFavCount);
        TextView revCount = (TextView)convertView.findViewById(R.id.listTruckReviewCount);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar4search);

        ImageView image = (ImageView)convertView.findViewById(R.id.listTruckImg);

        name.setText(foodtrucks.get(position).getFoodtruckName());
        category.setText(foodtrucks.get(position).getCategory1());
        location.setText(foodtrucks.get(position).getLocation());
        favCount.setText(foodtrucks.get(position).getFavoriteCount() + "");
        revCount.setText(foodtrucks.get(position).getReviewCount() + "");
        ratingBar.setRating((float) foodtrucks.get(position).getScore());


        new ImageLoadingTask(image).execute(foodtrucks.get(position).getFoodtruckImg());

        return convertView;
    }

    private class ImageLoadingTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewWeakReference;

        public ImageLoadingTask(ImageView img){
            imageViewWeakReference = new WeakReference<ImageView>(img);
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
            ImageView imageView = imageViewWeakReference.get();
            if(imageView != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap getRemoteImage(URL url){
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
