package com.example.foofatest.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.example.foofatest.Json.JsonParsingControl;
import com.example.foofatest.MemberFavListActivity;
import com.example.foofatest.R;
import com.example.foofatest.TruckInfoActivity;
import com.example.foofatest.TruckModifyActivity;
import com.example.foofatest.dto.Favorite;
import com.example.foofatest.dto.Foodtruck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.R.attr.targetId;

/**
 * Created by kosta on 2017-06-10.
 */

public class FavoriteListlAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater inflater;
    private List<Foodtruck> favorites;
    private SharedPreferences pref;





    public FavoriteListlAdapter(Context context, List<Foodtruck> favorites) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.favorites = favorites;
    }

    public SharedPreferences getPref() {
        return pref;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_search_foodtruck, null);
        }

        Button btn = (Button) convertView.findViewById(R.id.followRemove);

        btn.setTag(position);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(parent.getContext(), MemberFavListActivity.class);
                parent.getContext().startActivity(intent);
                Foodtruck food = favorites.get(position);
                Favorite favorite = new Favorite();
                favorite.setFoodtruckId(food.getFoodtruckId());
                favorite.setMemberId(food.getCategory3());
                new FavoriteTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/favorite/remove.do?memberId=" + food.getCategory3() + "&foodtruckId="  + food.getFoodtruckId());
                Log.d("1111", String.valueOf(v.getTag()));
                Log.d("1111", favorite.toString());
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




    private class FavoriteTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String)params[0]);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = doc.getElementsByTagName("foodtruck");
                for(int i=0; i<nodeList.getLength(); i++){
                    Foodtruck foodtruck = new Foodtruck();
                    Node node = nodeList.item(i);
                    Element element = (Element)node;
                    foodtruck.setFoodtruckId(getTagValue("foodtruckId", element));
                    foodtruck.setSellerId(getTagValue("sellerId", element));
                    foodtruck.setFoodtruckName(getTagValue("foodtruckName", element));
                    foodtruck.setOperationTime(getTagValue("operationTime", element));
                    foodtruck.setSpot(getTagValue("spot", element));
                    foodtruck.setNotice(getTagValue("notice", element));
                    foodtruck.setLocation(getTagValue("location", element));
                    foodtruck.setCategory1(getTagValue("category1", element));
                    foodtruck.setCategory2(getTagValue("category2", element));
//                    foodtruck.setCategory3(loginUserId);
                    foodtruck.setCard(Boolean.parseBoolean(getTagValue("card", element)));
                    foodtruck.setParking(Boolean.parseBoolean(getTagValue("parking", element)));
                    foodtruck.setDrinking(Boolean.parseBoolean(getTagValue("drinking", element)));
                    foodtruck.setCatering(Boolean.parseBoolean(getTagValue("catering", element)));
                    foodtruck.setState(Boolean.parseBoolean(getTagValue("state", element)));
                    foodtruck.setFavoriteCount(Integer.parseInt(getTagValue("favoriteCount", element)));
                    foodtruck.setReviewCount(Integer.parseInt(getTagValue("reviewCount", element)));
                    foodtruck.setScore(Double.parseDouble(getTagValue("score", element)));
//                    List<Menu> menus1 = new ArrayList<>();
//                    NodeList list1 = element.getElementsByTagName("menus").item(i).getChildNodes();
//                    Log.d("1111", String.valueOf(list1.getLength()));
                    foodtruck.setFoodtruckImg("http://106.242.203.67:8888/FoodtruckFinderProject/resources/img/food/"+getTagValue("foodtruckImg",element));
//                    foodtrucks.add(foodtruck);
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
