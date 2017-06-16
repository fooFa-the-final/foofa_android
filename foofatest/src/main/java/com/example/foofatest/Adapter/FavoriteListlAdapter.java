package com.example.foofatest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.foofatest.Json.JsonParsingControl;
import com.example.foofatest.MemberFavListActivity;
import com.example.foofatest.R;
import com.example.foofatest.dto.Favorite;
import com.example.foofatest.dto.Foodtruck;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static android.R.attr.targetId;

/**
 * Created by kosta on 2017-06-10.
 */

public class FavoriteListlAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater inflater;
    private List<Foodtruck> favorites;


    public FavoriteListlAdapter(Context context, List<Foodtruck> favorites) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.favorites = favorites;
    }


    @Override
    public int getCount() {
        return favorites.size();
    }

    @Override
    public Object getItem(int position) {
        return favorites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_search_foodtruck, null);
        }

        Button btn = (Button) convertView.findViewById(R.id.followRemove);


        btn.setTag(position);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Foodtruck food = favorites.get(position);


                Log.d("1111", String.valueOf(v.getTag()));
                Log.d("1111", food.toString());

            }
        });

        TextView truckName = (TextView) convertView.findViewById(R.id.truckName);
        TextView truckCategory = (TextView) convertView.findViewById(R.id.truckCategory);
        TextView truckArea = (TextView) convertView.findViewById(R.id.truckArea);
        TextView truckFavorite = (TextView) convertView.findViewById(R.id.truckFavorite);
        TextView truckReviewCount = (TextView) convertView.findViewById(R.id.truckReviewCount);
        TextView truckNotice = (TextView) convertView.findViewById(R.id.truckNotice);
        TextView truckHours = (TextView) convertView.findViewById(R.id.truckHours);
        TextView truckLocation = (TextView) convertView.findViewById(R.id.truckLocationText);
        ImageView image = (ImageView) convertView.findViewById(R.id.truckimg);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar2);


        truckName.setText(favorites.get(position).getFoodtruckName());
        truckCategory.setText(favorites.get(position).getCategory1());
        truckArea.setText(favorites.get(position).getLocation());
        truckFavorite.setText(String.valueOf(favorites.get(position).getFavoriteCount()));
        truckReviewCount.setText(String.valueOf(favorites.get(position).getReviewCount()));
//        truckNotice.setText(favorites.get(position).getNotice());
//        truckHours.setText(favorites.get(position).getOperationTime());
//        truckLocation.setText(favorites.get(position).getSpot());
        ratingBar.setRating((float) favorites.get(position).getScore());
        new ImageLoadingTask(image).execute(favorites.get(position).getFoodtruckImg());
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

    private static String getTagValue(String tag, Element element){

        if(element.getElementsByTagName(tag).item(0)==null){
            return "";
        }

        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node)nodeList.item(0);
        return  node.getNodeValue();
    }

    private class FavoriteTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            Favorite favorite = (Favorite) params[1];
            return JsonParsingControl.POST((String) params[0], favorite);
        }
        //
        @Override
        protected void onPostExecute(String result) {
            Intent intent = null;
        }

    }

}
