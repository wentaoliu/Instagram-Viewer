package com.instagram.instagram_viewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity ;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
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


public class CommentsActivity extends AppCompatActivity {
    private ArrayList<Comment> comments;
    private CommentsAdapter aComments;
    private String id;
    private String token;
    private ImageView comment;
    private TextView hide_down;
    private EditText comment_content;
    private Button comment_send;

    private LinearLayout rl_enroll;
    private RelativeLayout rl_comment;


    String stringTemp;
    JSONObject jsonObjectTemp = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getStringExtra("id");
        token = getIntent().getStringExtra("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        try {
            fetchComments();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initView();
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
        get(commentsUrl, token);

    }

    OkHttpClient client = new OkHttpClient();

    void get(String url, String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();
        JSONArray commentsJSON = null;
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
            commentsJSON = new JSONArray(stringTemp);
            // put newest at the top
            for (int i = commentsJSON.length() - 1; i >= 0; i--) {
                JSONObject commentJSON = commentsJSON.getJSONObject(i);
                Comment comment = new Comment();
                comment.profileUrl = commentJSON.getJSONObject("user").getString("profile_picture");
                comment.username = commentJSON.getJSONObject("user").getString("username");
                comment.text = commentJSON.getString("text");
                comment.userId = commentJSON.getJSONObject("user").getInt("id");
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

    private void initView() {


//        comment = (ImageView) findViewById(R.id.comment);
//        hide_down = (TextView) findViewById(R.id.hide_down);
        comment_content = (EditText) findViewById(R.id.comment_content);
        comment_send = (Button) findViewById(R.id.comment_send);

//        rl_enroll = (LinearLayout) findViewById(R.id.rl_enroll);
//        rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);

        comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment();
            }
        });


    }

    /**
     * 发送评论
     */
    public void sendComment() {
        if(comment_content.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "comment cannot be empty!", Toast.LENGTH_SHORT).show();
        }else{
            Comment comment = new Comment();
            comment.setUsername("me");
            comment.setContent(comment_content.getText().toString());
            comment.setCreatedTime(System.currentTimeMillis() / 1000);
            System.out.println(comment.createdTime);
            aComments.addComment(comment);
            String sendsUrl = "http://imitagram.wnt.io/media/" + id + "/comments";
            send(sendsUrl, token,comment_content.getText().toString());

            Toast.makeText(getApplicationContext(), "comment successfully!", Toast.LENGTH_SHORT).show();

        }
    }
    OkHttpClient client1 = new OkHttpClient();

    void send(String url, String token,String comment) {
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("text", comment)
                    .build();
            post(url, formBody,token);

        } catch (IOException e) {
            return;
        }
    }
   void post(String url, RequestBody body,String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",token)
                .post(body)
                .build();
        try (Response response = client1.newCall(request).execute()) {
            return;
        }
    }

}

