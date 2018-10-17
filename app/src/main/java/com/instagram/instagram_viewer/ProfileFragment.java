package com.instagram.instagram_viewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import okhttp3.*;
import com.google.gson.Gson;
import java.io.IOException;


public class ProfileFragment extends Fragment {
    private GridView gridView;
    private ImageView imgProfilePicture;
    private TextView tvUsername;
    private TextView tvPosts;
    private TextView tvFollows;
    private TextView tvFollowed;

    private static final String url = "http://imitagram.wnt.io/users/self";

    static ProfileFragment newInstance() {
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        /*set personal information*/
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvPosts = (TextView) view.findViewById(R.id.tvPosts);
        tvFollows = (TextView) view.findViewById(R.id.tvFollows);
        tvFollowed = (TextView) view.findViewById(R.id.tvFollowed);
        imgProfilePicture = (ImageView) view.findViewById(R.id.imgProfilePicture);
        gridView = view.findViewById(R.id.profile_gridView);

        Glide.with(this).load(R.drawable.profile).into(imgProfilePicture);

        gridView.setAdapter(new ProfileGridViewAdapter(this.getContext(), UploadedImage));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            fetchProfileData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//




    OkHttpClient client = new OkHttpClient();

    void fetchProfileData() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new Gson();
                final UserProfile user = gson.fromJson(myResponse, UserProfile.class);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvUsername.setText(user.username);
                        tvPosts.setText(String.valueOf(user.media));
                        tvFollows.setText(String.valueOf(user.follows));
                        tvFollowed.setText(String.valueOf(user.followed_by));
                    }
                });

            }
        });}



    public static String[] UploadedImage = {
            "https://cdn.shopify.com/s/files/1/0787/5255/products/bando-il-all_around_giant_circle_towel-watermelon-02.jpg?v=1520470527",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRG1C3EZxYiCPNKf7mi3lXE33eFF71v-SKTvxyuKwFRrwlm_T7o",
            "https://www.houseofparty.com.au/wp-content/uploads/2016/06/balloon-large-foil-watermelon.jpg",
            "https://leoandbella.com.au/wp-content/uploads/2017/11/wally-the-watermelon.jpg",
            "https://img.purch.com/w/660/aHR0cDovL3d3dy5zcGFjZS5jb20vaW1hZ2VzL2kvMDAwLzAwNS82NDQvb3JpZ2luYWwvbW9vbi13YXRjaGluZy1uaWdodC0xMDA5MTYtMDIuanBn",
            "https://leoandbella.com.au/wp-content/uploads/2017/11/wally-the-watermelon.jpg",
            "https://leoandbella.com.au/wp-content/uploads/2017/11/wally-the-watermelon.jpg",
            "https://leoandbella.com.au/wp-content/uploads/2017/11/wally-the-watermelon.jpg",
            "https://leoandbella.com.au/wp-content/uploads/2017/11/wally-the-watermelon.jpg",
            "https://leoandbella.com.au/wp-content/uploads/2017/11/wally-the-watermelon.jpg",

    };



    private String getToken() {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "null");
        return token;
    }
}
