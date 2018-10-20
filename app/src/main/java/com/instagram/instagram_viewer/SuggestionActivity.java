package com.instagram.instagram_viewer;

import android.content.Intent;
import android.os.Bundle;
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


public class SuggestionActivity extends AppCompatActivity {
    private ArrayList<User> users;
    private UserAdapter1 aUsers;
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
        token = getIntent().getStringExtra("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        try {
            fetchUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void fetchUsers() throws IOException {
        users = new ArrayList<User>(); // initialize arraylist
        // Create adapter bind it to the data in arraylist
        aUsers = new UserAdapter1(this, users);
        // Populate the data into the listview
        ListView lvUser = (ListView) findViewById(R.id.lvSuggestion);
        // Set the adapter to the listview (population of items)
        lvUser.setAdapter(aUsers);
        // https://api.instagram.com/v1/media/popular?client_id=<clientid>
        // { "data" => [x] => "images" => "standard_resolution" => "url" }
        // Setup popular url endpoint
        String popularUrl = "http://imitagram.wnt.io/users/self/suggest";

        get(popularUrl,token);

    }

    OkHttpClient client = new OkHttpClient();

    void get(String url, String token) throws IOException {
//        User user = new User();
//        user.profileUrl = "http://img.tupianzj.com/uploads/allimg/141014/1-1410141AH02K.jpg";
//        user.username = "kevin";
//        user.id = 1;
//        users.add(user);
//        aUsers.notifyDataSetChanged();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",token)
                .build();
        JSONArray photosJSON = null;
        try (Response response = client.newCall(request).execute()) {
            stringTemp = response.body().string();
            users.clear();
            photosJSON = new JSONArray(stringTemp);
            for (int i = 0; i < photosJSON.length(); i++) {
                JSONObject photoJSON = photosJSON.getJSONObject(i); // 1, 2, 3, 4
                User user = new User();
                user.profileUrl = photoJSON.getString("profile_picture");
                user.id = photoJSON.getInt("id");
                user.username = photoJSON.getString("username");
                users.add(user);
                aUsers.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    String getToken(){
        return token;
    }

}

