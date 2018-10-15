package com.instagram.instagram_viewer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class UserFeedFragment extends Fragment {

    TextView fragmentText;

    private TextView tv;
    public View mView;
    private View view;
    private SwipeRefreshLayout swipeContainer;
    private Button sortByTime;
    private Button sortByDistance;
    private ListView lvPhotos;
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private InstagramPhotosAdapter bPhotos;
    private  String token;
    private double latitude;
    private double longitude;

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
    @SuppressLint("NewApi")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mView = inflater.inflate(R.layout.activity_photos, container, false);
        swipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);
        sortByTime = (Button)mView.findViewById(R.id.sortByTime);
        sortByDistance = (Button)mView.findViewById(R.id.sortByDistance);
        sortByTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(photos,new SortByTime());
                lvPhotos.setAdapter(aPhotos);
            }
        });
        sortByDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(photos,new SortByDistance());
                lvPhotos.setAdapter(aPhotos);
            }
        });
        lvPhotos = (ListView) mView.findViewById(R.id.lvPhotos);
        token = (String)getArguments().get("token");
        latitude = (double)getArguments().get("latitude");
        longitude = (double)getArguments().get("longitude");
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
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",token)
                .build();
        JSONArray photosJSON = null;
        JSONArray commentsJSON = null;
        try (Response response = client.newCall(request).execute()) {
            stringTemp = response.body().string();
            photos.clear();
            photosJSON = new JSONArray(stringTemp);
            for (int i = 0; i < photosJSON.length(); i++) {
                JSONObject photoJSON = photosJSON.getJSONObject(i); // 1, 2, 3, 4
                InstagramPhoto photo = new InstagramPhoto();
                photo.profileUrl = photoJSON.getJSONObject("user").getString("profile_picture");
                photo.username = photoJSON.getJSONObject("user").getString("username");
                photo.createdTime = photoJSON.getString("created_time");
                photo.imageUrl = photoJSON.getJSONObject("image").getString("standard_resolution");
                photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                photo.commentsCount = photoJSON.getJSONObject("comments").getInt("count");
                photo.lat =  photoJSON.getJSONObject("location").getDouble("latitude");
                photo.lng =  photoJSON.getJSONObject("location").getDouble("longitude");
                photo.id = photoJSON.getString("id");
                photos.add(photo);
                aPhotos.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        swipeContainer.setRefreshing(false);
    }
    String getToken(){
        return token;
    }
    public double getDistance(double latitude1, double longitude1) {
        double lng_dest = (Math.PI / 180) * longitude1;
        double lat_dest = (Math.PI / 180) * latitude1;


        double lng_cur = (Math.PI / 180) *  longitude;
        double lat_cur = (Math.PI / 180) * latitude;

        double R = 6371;

        double d = Math.acos(Math.sin(lat_cur) * Math.sin(lat_dest) + Math.cos(lat_cur) * Math.cos(lat_dest) * Math.cos(lng_dest - lng_cur))* R;
        return d;
    }
    class SortByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            InstagramPhoto s1 = (InstagramPhoto) o1;
            InstagramPhoto s2 = (InstagramPhoto) o2;
            long s1t = 0,s2t = 0;
            try {
                s1t = simpleFormat.parse(s1.createdTime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                s2t = simpleFormat.parse(s2.createdTime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (s1t < s2t)
                return 1;
            return -1;
        }
    }
    class SortByDistance implements Comparator {
        public int compare(Object o1, Object o2) {
            InstagramPhoto s1 = (InstagramPhoto) o1;
            InstagramPhoto s2 = (InstagramPhoto) o2;
            if (s1.distance > s2.distance)
                return 1;
            return -1;
        }
    }

}