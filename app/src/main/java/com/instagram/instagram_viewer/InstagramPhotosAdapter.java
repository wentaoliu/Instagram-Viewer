package com.instagram.instagram_viewer;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.layout.simple_list_item_1;

/**
 * Created by mvince on 1/25/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> implements View.OnClickListener {
    private UserFeedFragment fragment;
    public InstagramPhotosAdapter(UserFeedFragment context, List<InstagramPhoto> photos) {

        super(context.getActivity(), simple_list_item_1, photos);
        this.fragment = context;
    }

    // getView method (int position)
    // Default, takes the model (InstagramPhoto) toString()

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Take the data source at position (e.g. 0)
        // Get the data item
        final InstagramPhoto photo = getItem(position);

        // Check if we are using a recycled view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        // Lookup the subview within the template
        ImageView imgProfile = (ImageView) convertView.findViewById(R.id.imgProfile);
        ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        TextView tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);

        final ImageView like = (ImageView) convertView.findViewById(R.id.like);
        final TextView tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
        final TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        TextView tvViewAllComments = (TextView) convertView.findViewById(R.id.tvViewAllComments);
        TextView tvComment1 = (TextView) convertView.findViewById(R.id.tvComment1);
        TextView tvComment2 = (TextView) convertView.findViewById(R.id.tvComment2);

        // Populate the subviews (textfield, imageview) with the correct data
        tvUsername.setText(photo.username);
        tvTime.setText(photo.getRelativeTime());
        tvDistance.setText(photo.getRelativeDistance());

        if (photo.caption != null) {
            tvCaption.setText(Html.fromHtml("<font color='#3f729b'><b>" + photo.username + "</b></font> " + photo.caption));
            tvCaption.setVisibility(View.VISIBLE);
        } else {
            tvCaption.setVisibility(View.GONE);
        }
        like.setImageResource(R.drawable.like_release);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationTools.scale(like);
                like.setImageResource(R.drawable.like_focus);
                InstagramPhoto ip = getItem(position);
                String sendsUrl = "http://imitagram.wnt.io/media/" + ip.id + "/likes";
                //send(sendsUrl, fragment.getToken());
                AnimationTools.scale(tvLikes);
                tvLikes.setText(String.format("%d likes",photo.likesCount+1));

            }
        });
        if (photo.likesCount > 0) {
            tvLikes.setText(String.format("%d likes", photo.likesCount));
            // set click handler for view all comments
            tvLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LikesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    InstagramPhoto ip = getItem(position);
                    intent.putExtra("id", ip.id);
                    intent.putExtra("token",fragment.getToken());
                    getContext().startActivity(intent);
                }
            });
            tvLikes.setVisibility(View.VISIBLE);
        } else {
            tvLikes.setVisibility(View.GONE);
        }

        if (photo.commentsCount > 0) {
            tvViewAllComments.setText(String.format("view all %d comments", photo.commentsCount));
            // set click handler for view all comments
            tvViewAllComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    InstagramPhoto ip = getItem(position);
                    intent.putExtra("id", ip.id);
                    intent.putExtra("token",fragment.getToken());
                    getContext().startActivity(intent);
                }
            });
            tvViewAllComments.setVisibility(View.VISIBLE);
        } else {
            tvViewAllComments.setVisibility(View.GONE);
        }

        // use device width for photo height
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        imgPhoto.getLayoutParams().height = displayMetrics.widthPixels;

        // Reset the images from the recycled view
        imgProfile.setImageResource(0);
        imgPhoto.setImageResource(0);

        // Ask for the photo to be added to the imageview based on the photo url
        // Background: Send a network request to the url, download the image bytes, convert into bitmap, insert bitmap into the imageview
        Picasso.with(getContext()).load(photo.profileUrl).into(imgProfile);
        Picasso.with(getContext()).load(photo.imageUrl).placeholder(R.drawable.instagram_glyph_on_white).into(imgPhoto);
        // Return the view for that data item
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        // disables selection
        return false;
    }

    @Override
    public void onClick(View view) {

    }


    public static class AnimationTools {
        public static void scale(View v) {
            ScaleAnimation anim = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            anim.setDuration(300);
            v.startAnimation(anim);
        }}

    OkHttpClient client = new OkHttpClient();

    void send(String url, String token) {
        try {
            RequestBody formBody = new FormBody.Builder()
                    .build();
            post(url, formBody,token);

        } catch (IOException e) {
            return;
        }
    }
    String post(String url, RequestBody body,String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",token)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}
