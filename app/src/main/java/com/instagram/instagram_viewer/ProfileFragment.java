package com.instagram.instagram_viewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
import java.io.InputStream;


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

//        gridView.setAdapter(new ProfileGridViewAdapter(this.getContext(), UploadedImage));

        try {
            fetchProfileData();
            fetchRecentPictures();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


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
                if(user.profile_picture != null) {
                    new DownloadImageFromInternet(imgProfilePicture)
                            .execute("http://imitagram.wnt.io" + user.profile_picture);

                }

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
        });
    }

    void fetchRecentPictures() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://imitagram.wnt.io/users/self/media/recent")
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
                final SelfImage[] images = gson.fromJson(myResponse, SelfImage[].class);
                final String[] imageUrls = new String[images.length];
                for( int i=0;i<images.length; i++) {
                    imageUrls[i] = "http://imitagram.wnt.io" + images[i].image.standard_resolution;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        gridView.setAdapter(new ProfileGridViewAdapter(getContext(), imageUrls));
                    }
                });

            }
        });
    }


    private String getToken() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String token = sharedPref.getString("token", "null");
        return token;
    }


    public class SelfImage {
        public int id;

        public SimpleImage image;

        public class SimpleImage {
            public String standard_resolution;
        }
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
