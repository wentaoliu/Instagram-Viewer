package com.instagram.instagram_viewer;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SwipeShareActivity extends AppCompatActivity {
    private ArrayList<InstagramPhoto> comments;
    private MyPhotosAdapter aComments;
    private String token;
    private SwipeRefreshLayout swipeContainer;

    String stringTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        token = getIntent().getStringExtra("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_photos);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.mySwipeContainer);
        try {
            initRefreshLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void initRefreshLayout() throws IOException {
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fetchPhotos();
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    fetchPhotos();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private void fetchPhotos() throws IOException {
        comments = new ArrayList<InstagramPhoto>(); // initialize arraylist
        // Create adapter bind it to the data in arraylist
        aComments = new MyPhotosAdapter(this, comments);
        // Populate the data into the listview
        ListView lvComments = (ListView) findViewById(R.id.lvMyPhotos);
        // Set the adapter to the listview (population of items)
        lvComments.setAdapter(aComments);
        // https://api.instagram.com/v1/media/<id>/comments?client_id=<clientid>
        // Setup comments url endpoint
        String commentsUrl = "http://imitagram.wnt.io/users/self/media/recent";

        // Create the network client
        get(commentsUrl, token);

    }

    OkHttpClient client = new OkHttpClient();

    void get(String url, String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();
        JSONArray photosJSON = null;
//        Comment comment = new Comment();
//        comment.profileUrl = " http://img.tupianzj.com/uploads/allimg/141014/1-1410141AH02K.jpg";
//        comment.username = "huo";
//        comment.text = "helloWorld!helloWorld!helloWorld!helloWorld!helloWorld!helloWorld!helloWorld!";
//        comment.createdTime = "1279340983";
//        comments.add(comment);
//        aComments.notifyDataSetChanged();

        try (Response response = client.newCall(request).execute()) {
            stringTemp = response.body().string();
            comments.clear();
            photosJSON = new JSONArray(stringTemp);
            // put newest at the top
            for (int i = photosJSON.length() - 1; i >= 0; i--) {
                JSONObject photoJSON = photosJSON.getJSONObject(i);
                InstagramPhoto photo = new InstagramPhoto();
                photo.imageUrl = photoJSON.getJSONObject("image").getString("standard_resolution");
                comments.add(photo);
            }
            // Notified the adapter that it should populate new changes into the listview
            aComments.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        swipeContainer.setRefreshing(false);

    }


}

