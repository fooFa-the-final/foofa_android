package com.example.foofatest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.foofatest.dto.Foodtruck;
import com.example.foofatest.dto.Member;
import com.example.foofatest.dto.Review;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import static java.lang.System.currentTimeMillis;

public class ReviewCreateActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1; //카메라 촬영으로 사진 가져오기
    private static final int REQUEST_TAKE_PHOTO = 2; //앨범에서 사진 가져오기
    private static final int REQUEST_IMAGE_CROP = 3; //가져온 사진을 자르기 위한 변수

    private Uri photoURI, albumURI = null;
    private Boolean album = false;

    private String clickImageView = null;
    private ImageView imageview1 = null;
    private ImageView imageview2 = null;

    private File file1 = null;
    private File file2 = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create);
        imageview1 = (ImageView) findViewById(R.id.imageView1);
        imageview2 = (ImageView) findViewById(R.id.imageView2);
        final EditText contents = (EditText) findViewById(R.id.contents);
        final RatingBar score = (RatingBar) findViewById(R.id.score);

        imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImageView = "imageview1";

                createDialog();
            }
        });

        imageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImageView = "imageview2";

                createDialog();
            }
        });

        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review review = new Review();
                Member writer = new Member();
                Foodtruck foodtruck = new Foodtruck();
                writer.setMemberId("nayeon");
                foodtruck.setFoodtruckId("F123");
                foodtruck.setFoodtruckName("sampleTruck102");
                review.setScore((int) score.getRating());
                review.setContents(contents.getText().toString());
                review.setWriter(writer);
                review.setFoodtruck(foodtruck);
                HttpAsyncTask task = new HttpAsyncTask();
                task.execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/review/create.do", review);
            }
        });
    }

    // 앨범, 사진 촬영 선택창
    private void createDialog() {
        final DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dispatchTakePictureIntent();
            }
        };
        final DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };
        final DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(ReviewCreateActivity.this)
                .setTitle("업로드할 이미지 선택")
                .setNeutralButton("사진촬영", cameraListener)
                .setNegativeButton("앨범선택", albumListener)
                .setPositiveButton("취소", cancelListener)
                .show();
    }

    // 설문조사 선택
    private void surveyDialog() {
        final DialogInterface.OnClickListener surveyListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doSurveyIntent();
            }
        };
        final DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(ReviewCreateActivity.this)
                .setTitle("설문에 응답하시겠습니까?")
                .setNegativeButton("네", surveyListener)
                .setPositiveButton("아니오", cancelListener)
                .show();
    }

    private void doSurveyIntent(){
        Intent intent = new Intent();

    }
    // 가져온 사진 뿌리기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = null;
        File file = null;
        if (clickImageView.equals("imageview1")) {
            imageView = imageview1;
        }
        else if (clickImageView.equals("imageview2")) {
            imageView = imageview2;
        }

        if (resultCode != RESULT_OK) {
            Toast.makeText(getApplicationContext(), "onActivityResult : RESULT_NOT_OK", Toast.LENGTH_SHORT).show();
        } else {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: // 앨범 이미지 가져오기
                    /*album = true;
                    File albumFile = null;
                    try {
                        albumFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (albumFile != null) {
                        albumURI = Uri.fromFile(albumFile);
                    }
                    mCurrentPhotoPath = albumFile.getPath();

                    photoURI = data.getData();
                    Log.d("log", photoURI.toString());
                    */
                    String path = getPath(data.getData());
                    file = new File(Environment.getExternalStorageDirectory(), path);
                    if (clickImageView.equals("imageview1")) {
                        file1 = file;
                    }
                    else if (clickImageView.equals("imageview2")) {
                        file2 = file;
                    }
                    Log.d("log", "path : " + path);
                    // imageView에 띄우기
                    Bitmap image_Bitmap = null;
                    try {
                        image_Bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(image_Bitmap);


                    /*ImageUploadTask task = new ImageUploadTask();
                    task.execute(albumFile);*/

                    break; // REQUEST_IMAGE_CAPTURE로 전달하여 crop
                case REQUEST_IMAGE_CAPTURE:
                    cropImage();

                    break;
                case REQUEST_IMAGE_CROP:
                    Bitmap photo = BitmapFactory.decodeFile(photoURI.getPath());
                    path = photoURI.toString().substring(photoURI.toString().lastIndexOf("/")+1, photoURI.toString().length());
                    Log.d("log", "photouri : " + path);
                    file = new File(Environment.getExternalStorageDirectory(), path);
                    if (clickImageView.equals("imageview1")) {
                        file1 = file;
                    }
                    else if (clickImageView.equals("imageview2")) {
                        file2 = file;
                    }
                    imageView.setImageBitmap(photo);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); // 동기화
                    if (album == false) {
                        mediaScanIntent.setData(photoURI); // 동기화
                    } else if (album == true) {
                        album = false;
                        mediaScanIntent.setData(albumURI); // 동기화
                    }
                    this.sendBroadcast(mediaScanIntent); // 동기화

                    break;
            }
        }
    }

    public String getPath(Uri data) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(); // 사진 찍은 후 저장할 임시 파일
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "createImageFile failed", Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                photoURI = Uri.fromFile(photoFile);
                Log.d("log", "photoURI : " + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // 저장할 폴더 생성
    private File createImageFile() throws IOException {

        // 특정 경로와 폴더를 지정하지 않고, 메모리 최상위치에 저장함
        String imageFileName = "tmp_" + String.valueOf(currentTimeMillis()) + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory(), imageFileName);
        return storageDir;
    }

    //앨범 호출
    private void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    //사진 편집? 자르기?
    private void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("scale", true);

        if (album == false)
            cropIntent.putExtra("output", photoURI); // 크랍된 이미지를 해당 경로에 저장
        else if (album == true)
            cropIntent.putExtra("output", albumURI);

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    // post 전송

    private class HttpAsyncTask extends AsyncTask<Object, Void, String> {

        private Review review;

        @Override
        protected String doInBackground(Object... objects) {

            review = (Review) objects[1];

            return POST((String) objects[0], review);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("log", "reviewId : " + result);
            Toast.makeText(ReviewCreateActivity.this, "리뷰가 작성되었습니다.", Toast.LENGTH_SHORT);
            Intent intent = new Intent(ReviewCreateActivity.this, ReviewDetailActivity.class);
            review.setReviewId(result);
            intent.putExtra("review", review);
            startActivity(intent);
            finish();
        }
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public String POST(String url, Review review) {
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String json = gson.toJson(review);

            httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("Content-type", "application/json");

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("utf-8"));
            os.flush();
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if (is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                os.close();
                httpCon.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("log", e.getLocalizedMessage());
        }

        if(file1 != null){
            Log.d("log", "file1 is not null");
            imageUpload(file1,result);
        }
        if(file2 != null) {
            Log.d("log", "file2 is not null");
            imageUpload(file2, result);
        }
        if(file1 == null && file2 == null)
            Log.d("log", "both null");
        return result;
    }

    private void imageUpload(Object... params) {
        File file = (File)params[0];
        String reviewId = (String)params[1].toString();
        Log.d("log", "do in : " +file.getPath());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("UTF-8"));
        builder.addPart("file", new FileBody(file));
        builder.addTextBody("reviewId", reviewId);
        //builder.addTextBody("fileName", file.getPath());
        //builder.addTextBody("STRING_KEY", "STRING_VALUE", ContentType.create("Multipart/related", "UTF-8"));

        InputStream inputStream = null;
        HttpClient httpClient = AndroidHttpClient.newInstance("Android");
        HttpPost httpPost = new HttpPost("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/review/image.do");
        httpPost.setEntity(builder.build());

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            long resultSize = httpResponse.getEntity().getContentLength();
            if(resultSize < 0){
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()), 65728);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                Log.d("log", stringBuilder.toString());
                inputStream.close();
                String result = stringBuilder.toString();
                Log.d("log", "result : " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

