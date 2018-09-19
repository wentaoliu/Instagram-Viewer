package com.instagram.instagram_viewer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class UserFeedFragment extends Fragment {

    TextView fragmentText;

    private TextView tv;
    public View mView;
    private SwipeRefreshLayout swipeContainer;
    private ListView lvPhotos;
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private  String token;

    String stringTemp;
    JSONObject jsonObjectTemp = new JSONObject();


    public static UserFeedFragment newInstance(String name) {

        Bundle args = new Bundle();
        args.putString("name", name);
        UserFeedFragment fragment = new UserFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_photos, container, false);
        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);
        lvPhotos = (ListView) mView.findViewById(R.id.lvPhotos);
        token = (String)getArguments().get("token");
        try {
            initRefreshLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mView;
    }

    private void initRefreshLayout() throws IOException {
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fetchPopularPhotos();
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    fetchPopularPhotos();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetchPopularPhotos() throws IOException {
        photos = new ArrayList<InstagramPhoto>(); // initialize arraylist
        // Create adapter bind it to the data in arraylist
        aPhotos = new InstagramPhotosAdapter(this, photos);
        // Populate the data into the listview

        // Set the adapter to the listview (population of items)
        lvPhotos.setAdapter(aPhotos);
        // https://api.instagram.com/v1/media/popular?client_id=<clientid>
        // { "data" => [x] => "images" => "standard_resolution" => "url" }
        // Setup popular url endpoint
        String popularUrl = "http://imitagram.wnt.io/users/self/feed";

        get(popularUrl,token);

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

    OkHttpClient client = new OkHttpClient();

    void get(String url,String token) throws IOException {
        InstagramPhoto photo = new InstagramPhoto();
        photo.profileUrl = "http://img.tupianzj.com/uploads/allimg/141014/1-1410141AH02K.jpg";
        photo.createdTime = "1522340983";
        photo.imageUrl = "http://img.tupianzj.com/uploads/allimg/141014/1-1410141AH02K.jpg";
        photo.username = "kevin";
        photo.imageHeight = 500;
        photo.likesCount = 100;
        photo.commentsCount = 100;
        photo.id = "1";
        photos.add(photo);
        aPhotos.notifyDataSetChanged();
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Authorization",token)
//                .build();
//        JSONArray photosJSON = null;
//        JSONArray commentsJSON = null;
//        try (Response response = client.newCall(request).execute()) {
//            stringTemp = response.body().string();
//            jsonObjectTemp = new JSONObject(stringTemp);
//            photos.clear();
//            photosJSON = (JSONArray) jsonObjectTemp.get("data");
//            for (int i = 0; i < photosJSON.length(); i++) {
//                JSONObject photoJSON = photosJSON.getJSONObject(i); // 1, 2, 3, 4
//                InstagramPhoto photo = new InstagramPhoto();
//                photo.profileUrl = photoJSON.getJSONObject("user").getString("profile_picture");
//                photo.username = photoJSON.getJSONObject("user").getString("username");
//                // caption may be null
//                if (photoJSON.has("caption") && !photoJSON.isNull("caption")) {
//                    photo.caption = photoJSON.getJSONObject("caption").getString("text");
//                }
//                photo.createdTime = photoJSON.getString("created_time");
//                photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
//                photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
//                photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
//                // Get last 2 comments
//                if (photoJSON.has("comments") && !photoJSON.isNull("comments")) {
//                    photo.commentsCount = photoJSON.getJSONObject("comments").getInt("count");
//                    commentsJSON = photoJSON.getJSONObject("comments").getJSONArray("data");
//                    if (commentsJSON.length() > 0) {
//                        photo.comment1 = commentsJSON.getJSONObject(commentsJSON.length() - 1).getString("text");
//                        photo.user1 = commentsJSON.getJSONObject(commentsJSON.length() - 1).getJSONObject("from").getString("username");
//                        if (commentsJSON.length() > 1) {
//                            photo.comment2 = commentsJSON.getJSONObject(commentsJSON.length() - 2).getString("text");
//                            photo.user2 = commentsJSON.getJSONObject(commentsJSON.length() - 2).getJSONObject("from").getString("username");
//                        }
//                    } else {
//                        photo.commentsCount = 0;
//                    }
//                }
//                photo.id = photoJSON.getString("id");
//                photos.add(photo);
//                aPhotos.notifyDataSetChanged();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        swipeContainer.setRefreshing(false);
    }
    String getToken(){
        return token;
    }
}