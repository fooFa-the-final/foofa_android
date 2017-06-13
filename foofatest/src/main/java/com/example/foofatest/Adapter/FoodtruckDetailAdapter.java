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
 * Created by kosta on 2017-06-10.
 */

public class FoodtruckDetailAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater inflater;
    private List<Foodtruck> foodtrucks;

    public FoodtruckDetailAdapter(Context context, List<Foodtruck> foodtrucks) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.foodtrucks = foodtrucks;
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_foodtruck_detail, null);
        }


        TextView truckName = (TextView) convertView.findViewById(R.id.truckName);
        TextView truckCategory = (TextView) convertView.findViewById(R.id.truckCategory);
        TextView truckArea = (TextView) convertView.findViewById(R.id.truckArea);
        TextView truckFavorite = (TextView) convertView.findViewById(R.id.truckFavorite);
        TextView truckReviewCount = (TextView) convertView.findViewById(R.id.truckReviewCount);
        TextView truckNotice = (TextView) convertView.findViewById(R.id.truckNotice);
        TextView truckHours = (TextView) convertView.findViewById(R.id.truckHours);
        TextView truckLocation = (TextView) convertView.findViewById(R.id.truckLocation);
        ImageView image = (ImageView) convertView.findViewById(R.id.truckimg);


        truckName.setText(foodtrucks.get(position).getFoodtruckName());
        truckCategory.setText(foodtrucks.get(position).getCategory1());
        truckArea.setText(foodtrucks.get(position).getLocation());
//        truckFavorite.setText(foodtrucks.get(position).getFavoriteCount());
//        truckReviewCount.setText(foodtrucks.get(position).getReviewCount());
        truckNotice.setText(foodtrucks.get(position).getNotice());
        truckHours.setText(foodtrucks.get(position).getOperationTime());
        truckLocation.setText(foodtrucks.get(position).getSpot());

        new ImageLoadingTask(image).execute(foodtrucks.get(position).getFoodtruckImg());
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

