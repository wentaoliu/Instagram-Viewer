package com.instagram.instagram_viewer;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mvince on 1/26/15.
 */
public class LikesAdapter extends BaseAdapter {
    Context context;
    List<Like> likes;
    public LikesAdapter(Context context, List<Like> likes) {
        this.context =context;
        this.likes = likes;
    }

    // getView method (int position)
    // Default, takes the model (InstagramPhoto) toString()

    @Override
    public int getCount() {
        return likes.size();
    }

    @Override
    public Object getItem(int i) {
        return likes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Take the data source at position (e.g. 0)
        // Get the data item
        Like like = (Like) getItem(position);


        // Check if we are using a recycled view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_like, null);
        }

        // Lookup the subview within the template
        ImageView imgProfile = (ImageView) convertView.findViewById(R.id.imgCommentProfile);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);

        tvUsername.setText(Html.fromHtml("<font color='#3f729b'><b>" + like.username + "</b></font> "));

        // Reset the images from the recycled view
        imgProfile.setImageResource(0);

        // Ask for the photo to be added to the imageview based on the photo url
        // Background: Send a network request to the url, download the image bytes, convert into bitmap, insert bitmap into the imageview
        Picasso.with(context).load(like.profileUrl).into(imgProfile);

        // Return the view for that data item
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        // disables selection
        return false;
    }


}
