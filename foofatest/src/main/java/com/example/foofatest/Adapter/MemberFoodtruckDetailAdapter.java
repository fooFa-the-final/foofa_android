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

import com.example.foofatest.MemberFavListActivity;
import com.example.foofatest.R;
import com.example.foofatest.dto.Favorite;
import com.example.foofatest.dto.Foodtruck;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by kosta on 2017-06-16.
 */

public class MemberFoodtruckDetailAdapter extends BaseAdapter{


    private Context context;
    private LayoutInflater inflater;
    private List<Foodtruck> foodtrucks;

    public MemberFoodtruckDetailAdapter(Context context, List<Foodtruck> foodtrucks) {
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_mem_truckdetail, null);
        }

//        Button btn = (Button) convertView.findViewById(R.id.registerFavorite);

//        btn.setTag(position);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent=new Intent(parent.getContext(), MemberFavListActivity.class);
//                parent.getContext().startActivity(intent);
//                Foodtruck food = foodtrucks.get(position);
//                Favorite favorite = new Favorite();
//                favorite.setFoodtruckId(food.getFoodtruckId());
//                favorite.setMemberId(food.getCategory3());
//                new FavoriteTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/favorite/remove.do?memberId=" + food.getCategory3() + "&foodtruckId="  + food.getFoodtruckId());
//            }
//        });



        TextView truckName = (TextView) convertView.findViewById(R.id.truckName);
        TextView truckCategory = (TextView) convertView.findViewById(R.id.truckCategory);
        TextView truckArea = (TextView) convertView.findViewById(R.id.truckArea);
        TextView truckFavorite = (TextView) convertView.findViewById(R.id.truckFavorite);
        TextView truckReviewCount = (TextView) convertView.findViewById(R.id.truckReviewCount);
        TextView truckNotice = (TextView) convertView.findViewById(R.id.truckNotice);
        TextView truckHours = (TextView) convertView.findViewById(R.id.truckHours);
//        TextView truckLocation = (Linerla) convertView.findViewById(R.id.truckLocationText);
        ImageView image = (ImageView) convertView.findViewById(R.id.truckimg);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar2);

        TextView truckCard = (TextView) convertView.findViewById(R.id.truckCard);
        TextView truckAlchol = (TextView) convertView.findViewById(R.id.truckAlchol);
        TextView truckParking = (TextView) convertView.findViewById(R.id.truckParking);
        TextView truckCatering = (TextView) convertView.findViewById(R.id.truckCatering);

        truckName.setText(foodtrucks.get(position).getFoodtruckName());
        truckCategory.setText(foodtrucks.get(position).getCategory1());
        truckArea.setText(foodtrucks.get(position).getLocation());
        truckFavorite.setText(String.valueOf(foodtrucks.get(position).getFavoriteCount()));
        truckReviewCount.setText(String.valueOf(foodtrucks.get(position).getReviewCount()));
        truckNotice.setText(foodtrucks.get(position).getNotice());
        truckHours.setText(foodtrucks.get(position).getOperationTime());
//        truckLocation.setText(foodtrucks.get(position).getSpot());
        ratingBar.setRating((float) foodtrucks.get(position).getScore());
        if(foodtrucks.get(position).isCard() == true) {
            truckCard.setText("가능");
        } else {
            truckCard.setText("불가");
        }

        if(foodtrucks.get(position).isCatering() == true) {
            truckCatering.setText("가능");
        } else {
            truckCatering.setText("불가");
        }
        if(foodtrucks.get(position).isDrinking() == true) {
            truckAlchol.setText("가능");
        } else {
            truckAlchol.setText("불가");
        }
        if(foodtrucks.get(position).isParking() == true) {
            truckParking.setText("가능");
        } else {
            truckParking.setText("불가");
        }


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
