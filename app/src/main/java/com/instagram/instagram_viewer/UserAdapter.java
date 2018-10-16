package com.instagram.instagram_viewer;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mvince on 1/26/15.
 */
public class UserAdapter extends BaseAdapter {
    DiscoverFragment context;
    List<User> users;
    public UserAdapter(DiscoverFragment context, List<User> users) {
        this.context =context;
        this.users = users;
    }

    // getView method (int position)
    // Default, takes the model (InstagramPhoto) toString()

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Take the data source at position (e.g. 0)
        // Get the data item
        final User user = (User) getItem(position);


        // Check if we are using a recycled view
        if (convertView == null) {
            convertView = LayoutInflater.from(context.getActivity()).inflate(R.layout.item_user, null);
        }

        // Lookup the subview within the template
        ImageView imgProfile = (ImageView) convertView.findViewById(R.id.imgCommentProfile);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        Button follow = (Button) convertView.findViewById(R.id.follow);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestBody formBody = new FormBody.Builder()
                        .add("follow", String.valueOf(user.id))
                        .build();
                try {
                    post("http://imitagram.wnt.io/relationships/follow", formBody);
                    Toast.makeText(context.getActivity(), "follow successfully!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tvUsername.setText(Html.fromHtml("<font color='#3f729b'><b>" + user.username + "</b></font> "));

        // Reset the images from the recycled view
        imgProfile.setImageResource(0);
        String url = "http://imitagram.wnt.io" + user.profileUrl;
        // Ask for the photo to be added to the imageview based on the photo url
        // Background: Send a network request to the url, download the image bytes, convert into bitmap, insert bitmap into the imageview
        new LikesAdapter.DownloadImageTask(imgProfile).execute(url);
        // Return the view for that data item
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        // disables selection
        return false;
    }
    OkHttpClient client = new OkHttpClient();

    String post(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",context.getToken())
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


}
