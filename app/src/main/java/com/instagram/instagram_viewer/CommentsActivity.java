package com.instagram.instagram_viewer;

import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CommentsActivity extends AppCompatActivity  {
    private ArrayList<Comment> comments;
    private CommentsAdapter aComments;
    private String id;
    private String token;

    String stringTemp;
    JSONObject jsonObjectTemp = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getStringExtra("id");
        token= getIntent().getStringExtra("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        try {
            fetchComments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
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

    private void fetchComments() throws IOException {
        comments = new ArrayList<Comment>(); // initialize arraylist
        // Create adapter bind it to the data in arraylist
        aComments = new CommentsAdapter(this, comments);
        // Populate the data into the listview
        ListView lvComments = (ListView) findViewById(R.id.lvComments);
        // Set the adapter to the listview (population of items)
        lvComments.setAdapter(aComments);
        // https://api.instagram.com/v1/media/<id>/comments?client_id=<clientid>
        // Setup comments url endpoint
        String commentsUrl = "http://imitagram.wnt.io/media/" + id + "/comments";

        // Create the network client
        get(commentsUrl,token);

    }
    OkHttpClient client = new OkHttpClient();

    void get(String url,String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",token)
                .build();
        JSONArray commentsJSON = null;
        try (Response response = client.newCall(request).execute()) {
            stringTemp = response.body().string();
            jsonObjectTemp = new JSONObject(stringTemp);
            comments.clear();
            commentsJSON = (JSONArray) jsonObjectTemp.get("data");
            // put newest at the top
            for (int i = commentsJSON.length() - 1; i >= 0; i--) {
                JSONObject commentJSON = commentsJSON.getJSONObject(i);
                Comment comment = new Comment();
                comment.profileUrl = commentJSON.getJSONObject("from").getString("profile_picture");
                comment.username = commentJSON.getJSONObject("from").getString("username");
                comment.text = commentJSON.getString("text");
                comment.createdTime = commentJSON.getString("created_time");
                comments.add(comment);
            }
            // Notified the adapter that it should populate new changes into the listview
            aComments.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
