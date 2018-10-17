package com.instagram.instagram_viewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ShareActivity extends AppCompatActivity {
    private ArrayList<Like> likes;
    private LikesAdapter aLikes;
    private String id;
    private String token;
    private ImageView like;
    private EditText username;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        token = getIntent().getStringExtra("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Button wait = (Button) findViewById(R.id.wait);
        Button find = (Button) findViewById(R.id.find);
        wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BluetoothServerActivity.class);
                intent.setFlags(Intent. FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("token",token);
                getApplicationContext().startActivity(intent);
            }
        });
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),BluetoothClientActivity.class);
                intent.setFlags(Intent. FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("token",token);
                getApplicationContext().startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





}

