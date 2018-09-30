package com.instagram.instagram_viewer;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.OkHttpClient;


public class DiscoverFragment extends Fragment {

    TextView fragmentText;

    private TextView tv;
    public View mView;
    private SwipeRefreshLayout swipeContainer;
    private Button sortByTime;
    private Button sortByDistance;
    private ListView lvPhotos;
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private InstagramPhotosAdapter bPhotos;
    private  String token;

    String stringTemp;
    JSONObject jsonObjectTemp = new JSONObject();


    public static DiscoverFragment newInstance(String name) {

        Bundle args = new Bundle();
        args.putString("name", name);
        DiscoverFragment fragment = new DiscoverFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_photos, container, false);
        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);
        sortByTime = (Button)mView.findViewById(R.id.sortByTime);
        sortByDistance = (Button)mView.findViewById(R.id.sortByDistance);
        lvPhotos = (ListView) mView.findViewById(R.id.lvPhotos);
        token = (String)getArguments().get("token");
//        try {
////            initRefreshLayout();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return mView;
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
        photo.lat = 121.62;
        photo.lng = 38.92;
        photo.distance = 1000;
        System.out.println(photo.distance);
        photos.add(photo);
        aPhotos.notifyDataSetChanged();
        InstagramPhoto photo1 = new InstagramPhoto();
        photo1.profileUrl = "http://img.tupianzj.com/uploads/allimg/141014/1-1410141AH02K.jpg";
        photo1.createdTime = "1532340983";
        photo1.imageUrl = "http://img.tupianzj.com/uploads/allhuoimg/141014/1-1410141AH02K.jpg";
        photo1.username = "robin";
        photo1.imageHeight = 500;
        photo1.likesCount = 100;
        photo1.commentsCount = 100;
        photo1.id = "1";
        photo1.lat = 121.62;
        photo1.lng = 38.92;
        photo1.distance = 5000;
        System.out.println(photo.distance);
        photos.add(photo1);
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
//                photo.profileUrl = photoJSON.getJSONObject("uploader").getString("profile_picture");
//                photo.username = photoJSON.getJSONObject("uploader").getString("username");
//                // caption may be null
//                if (photoJSON.has("caption") && !photoJSON.isNull("caption")) {
//                    photo.caption = photoJSON.getJSONObject("caption").getString("text");
//                }
//                photo.createdTime = photoJSON.getString("created_at");
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