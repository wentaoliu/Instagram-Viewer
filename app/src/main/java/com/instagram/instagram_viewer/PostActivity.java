package com.instagram.instagram_viewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import Helper.BitmapStore;

public class PostActivity extends AppCompatActivity {

    private Bitmap rawBitmap = null;
    private Bitmap postBitmap = null;
    private ImageView postView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postView = findViewById(R.id.post_view);
        Intent intent = getIntent();
        if (intent != null) {
            rawBitmap = BitmapStore.getBitmap();
            postView.setImageBitmap(rawBitmap);
        }


    }
}
