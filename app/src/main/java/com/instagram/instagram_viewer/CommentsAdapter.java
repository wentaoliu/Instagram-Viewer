package com.instagram.instagram_viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.List;


public class CommentsAdapter extends BaseAdapter {
    Context context;
    List<Comment> comments;
    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context =context;
        this.comments = comments;
    }

    // getView method (int position)
    // Default, takes the model (InstagramPhoto) toString()

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int i) {
        return comments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Take the data source at position (e.g. 0)
        // Get the data item
        Comment comment = (Comment) getItem(position);


        // Check if we are using a recycled view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
        }

        // Lookup the subview within the template
        ImageView imgProfile = (ImageView) convertView.findViewById(R.id.imgCommentProfile);
        TextView tvComment = (TextView) convertView.findViewById(R.id.tvComment);
        TextView tvCommentTime = (TextView) convertView.findViewById(R.id.tvCommentTime);

        tvComment.setText(Html.fromHtml("<font color='#3f729b'><b>" + comment.username + "</b></font> " + comment.text));
        try {
            tvCommentTime.setText(comment.getRelativeTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Reset the images from the recycled view
        imgProfile.setImageResource(0);

        // Ask for the photo to be added to the imageview based on the photo url
        // Background: Send a network request to the url, download the image bytes, convert into bitmap, insert bitmap into the imageview
        String url = "http://imitagram.wnt.io" + comment.profileUrl;
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
    public void addComment(Comment comment){
        comments.add(comment);
        notifyDataSetChanged();
    }




}
