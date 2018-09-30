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
    private ListView lvUser;
    private TextView tvSearch;
    private Button search;
    private ArrayList<User> users;
    private UserAdapter aUsers;

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
        mView = inflater.inflate(R.layout.activity_discover, container, false);
        tvSearch = (TextView) mView.findViewById(R.id.text_search);
        search = (Button) mView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    fetchUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        lvUser = (ListView) mView.findViewById(R.id.lvDiscover);
        token = (String)getArguments().get("token");
        return mView;
    }

    private void fetchUsers() throws IOException {
        users = new ArrayList<User>(); // initialize arraylist
        // Create adapter bind it to the data in arraylist
        aUsers = new UserAdapter(this, users);
        // Populate the data into the listview

        // Set the adapter to the listview (population of items)
        lvUser.setAdapter(aUsers);
        String content = tvSearch.getText().toString();
        // https://api.instagram.com/v1/media/popular?client_id=<clientid>
        // { "data" => [x] => "images" => "standard_resolution" => "url" }
        // Setup popular url endpoint
        String popularUrl = "http://imitagram.wnt.io/users/search?q="+ content;

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
        User user = new User();
        user.profileUrl = "http://img.tupianzj.com/uploads/allimg/141014/1-1410141AH02K.jpg";
        user.username = "kevin";
        user.id = 1;
        users.add(user);
        aUsers.notifyDataSetChanged();

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
//                User user = new User();
//                user.profileUrl = photoJSON.getString("profileUrl")
//                user.id = photoJSON.getString("id");
//                user.username = photoJSON.getString("username);
//                photos.add(photo);
//                aPhotos.notifyDataSetChanged();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
    String getToken(){
        return token;
    }

}