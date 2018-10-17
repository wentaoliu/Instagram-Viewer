package comshaoqingliu.httpsgithub.Instagram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;

import comshaoqingliu.httpsgithub.helloworld.R;

public class ProfileActivity extends AppCompatActivity {
    private GridView gridView;
    private ImageView profileImageIv;
    private TextView numPostTv;
    private TextView numFollowingTv;
    private TextView numFollowerTv;
    private TextView userNameTv;

    private String userName;
    private String profileImage;
    private String numPost;
    private String numFollowing;
    private String numFollower;
    private String stringTemp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*set personal information*/
        userNameTv = findViewById(R.id.profile_name);
        numPostTv = findViewById(R.id.tvPost);
        numFollowingTv = findViewById(R.id.tvFollowing);
        numFollowerTv = findViewById(R.id.tvFollower);
        profileImageIv = findViewById(R.id.profile_image);
        gridView = findViewById(R.id.profile_gridView);

        userNameTv.setText("user name");
        numPostTv.setText("99");
        numFollowingTv.setText("88");
        numFollowerTv.setText("77");
        Glide.with(this).load(R.drawable.profile).into(profileImageIv);

        gridView.setAdapter(new ProfileGridViewAdapter(ProfileActivity.this, UploadedImage));
    }




//    OkHttpClient client = new OkHttpClient();

//    void get(String url,String token) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(url)
//                .header("Authorization", "Token " + getToken())
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                call.cancel();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String myResponse = response.body().string();
//                Gson gson = new Gson();
//                final UserProfile userProfile = gson.fromJson(myResponse, UserProfile.class);
//
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.addAll(res);
//                    }
//                });
//
//            }
//        });}



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

}
