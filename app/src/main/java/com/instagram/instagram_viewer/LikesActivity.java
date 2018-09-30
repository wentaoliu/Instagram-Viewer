package com.instagram.instagram_viewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LikesActivity extends AppCompatActivity {
    private ArrayList<Like> likes;
    private LikesAdapter aLikes;
    private String id;
    private String token;
    private ImageView like;
    private EditText username;


    String stringTemp;
    JSONObject jsonObjectTemp = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getStringExtra("id");
        token = getIntent().getStringExtra("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        try {
            fetchLikes();
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

    private void fetchLikes() throws IOException {
        likes = new ArrayList<Like>(); // initialize arraylist
        // Create adapter bind it to the data in arraylist
        aLikes = new LikesAdapter(this, likes);
        // Populate the data into the listview
        ListView lvLikes = (ListView) findViewById(R.id.lvLikes);
        // Set the adapter to the listview (population of items)
        lvLikes.setAdapter(aLikes);
        // https://api.instagram.com/v1/media/<id>/comments?client_id=<clientid>
        // Setup comments url endpoint
        String commentsUrl = "http://imitagram.wnt.io/media/" + id + "/likes";

        // Create the network client
        get(commentsUrl, token);

    }

    OkHttpClient client = new OkHttpClient();

    void get(String url, String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();
        JSONArray likesJSON = null;
        Like like = new Like();
        like.profileUrl = " http://img.tupianzj.com/uploads/allimg/141014/1-1410141AH02K.jpg";
        like.username = "huo";
        likes.add(like);
        aLikes.notifyDataSetChanged();
//        try (Response response = client.newCall(request).execute()) {
//            stringTemp = response.body().string();
//            jsonObjectTemp = new JSONObject(stringTemp);
//            likes.clear();
//            likesJSON = (JSONArray) jsonObjectTemp.get("data");
//            // put newest at the top
//            for (int i = likesJSON.length() - 1; i >= 0; i--) {
//                JSONObject likeJSON = likesJSON.getJSONObject(i);
//                Like like = new Like();
//                like.profileUrl = commentJSON.getJSONObject("from").getString("profile_picture");
//                like.username = commentJSON.getJSONObject("from").getString("username");
//                likes.add(like);
//            }
//            // Notified the adapter that it should populate new changes into the listview
//            aComments.notifyDataSetChanged();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}

