package com.instagram.instagram_viewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import Helper.BitmapStore;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {

    private Bitmap rawBitmap = null;
    private Bitmap postBitmap = null;
    private ImageView postView;
    private Button btnPost;

    private String getToken() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sharedPref.getString("token", "null");
        return token;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postView = findViewById(R.id.post_view);
        btnPost = findViewById(R.id.button_post);

        final OkHttpClient client = new OkHttpClient();

        Intent intent = getIntent();
        if (intent != null) {
            rawBitmap = BitmapStore.getBitmap();
            postView.setImageBitmap(rawBitmap);
        }

        btnPost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.d("Post Activity", "post button clicked");

                //create a file to write bitmap data
                File f = new File(getApplicationContext().getCacheDir(), "instatmp.jpg");
                try {
                    f.createNewFile();
                    //Convert bitmap to byte array
                    Bitmap bitmap = rawBitmap;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();


                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", f.getName(), reqFile);


                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("lat", "0")
                            .addFormDataPart("lng", "0")
                            .addFormDataPart("file", f.getName(), reqFile)
                            .build();

                    Log.d("token", getToken());


                    Request request = new Request.Builder()
                            .header("Authorization", getToken())
                            .url("http://imitagram.wnt.io/media/upload")
                            .post(requestBody)
                            .build();

                    Log.d("Post ", "req ready");

                    Response response = client.newCall(request).execute();
                    Log.d("Post finished", response.toString());


                    Intent intent = new Intent();
                    intent.setClass(PostActivity.this, MainActivity.class);

                    intent.putExtra("token",getToken());

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });


    }
}
