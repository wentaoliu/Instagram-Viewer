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

import java.io.IOException;
import java.text.ParseException;
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
public class MyPhotosAdapter extends ArrayAdapter<InstagramPhoto> implements View.OnClickListener {
    private Context context;
    public MyPhotosAdapter(Context context, List<InstagramPhoto> photos) {

        super(context, simple_list_item_1, photos);
        this.context = context;
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
        ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto);

        ImageView imgProfile = (ImageView) convertView.findViewById(R.id.imgProfile);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        TextView tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);

        final ImageView like = (ImageView) convertView.findViewById(R.id.like);
        final TextView tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
        TextView tvViewAllComments = (TextView) convertView.findViewById(R.id.tvViewAllComments);
        // use device width for photo height
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        imgPhoto.getLayoutParams().height = displayMetrics.widthPixels;
        imgProfile.setVisibility(View.GONE);
        tvUsername.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvDistance.setVisibility(View.GONE);
        like.setVisibility(View.GONE);
        tvLikes.setVisibility(View.GONE);
        tvViewAllComments.setVisibility(View.GONE);




        // Reset the images from the recycled view
        imgPhoto.setImageResource(0);
        String imgUrl = "http://imitagram.wnt.io"+ photo.imageUrl;
        if(photo.imageUrl != null){
            new LikesAdapter.DownloadImageTask(imgPhoto).execute(imgUrl);

        }else{
            imgPhoto.setImageBitmap(photo.bitmap);
        }


        // Ask for the photo to be added to the imageview based on the photo url
        // Background: Send a network request to the url, download the image bytes, convert into bitmap, insert bitmap into the imageview
        //Picasso.with(getContext()).load(photo.profileUrl).into(imgProfile);
        //Picasso.with(getContext()).load(photo.imageUrl).placeholder(R.drawable.instagram_glyph_on_white).into(imgPhoto);
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




}
