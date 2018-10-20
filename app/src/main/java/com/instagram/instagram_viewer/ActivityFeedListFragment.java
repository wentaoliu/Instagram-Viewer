package com.instagram.instagram_viewer;

import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.IOException;
import okhttp3.*;
import com.google.gson.Gson;
import android.widget.ArrayAdapter;

import android.content.SharedPreferences;
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;
import java.util.Date;


public class ActivityFeedListFragment extends Fragment {
    TextView tv;
    ActivityFeedAdapter adapter;

    private static final String ARG_MODE = "mode";
    private String mode;
    private static final String baseUrl = "http://imitagram.wnt.io/activities/";
    private String url;

    /**
     * Create a new instance of ActivityFeedListFragment
     */
    static ActivityFeedListFragment newInstance(String mode) {
        ActivityFeedListFragment f = new ActivityFeedListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, mode);
        f.setArguments(args);
        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mode = getArguments().getString(ARG_MODE);
            url = baseUrl + mode;
        }
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activity_feed_list, container, false);
        // Construct the data source
        ArrayList<ActivityFeed> arrayOfFeeds = new ArrayList<ActivityFeed>();
        // Create the adapter to convert the array to views
        adapter = new ActivityFeedAdapter(this, arrayOfFeeds);
        // Attach the adapter to a ListView
        ListView listView = (ListView) v.findViewById(R.id.lvFeed);
        listView.setAdapter(adapter);
        try {
            fetchData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
//
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        //Log.i("FragmentList", "Item clicked: " + id);
//    }

    private void fetchData() throws IOException {
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
                final ActivityFeed[] res;

                try {
                    res = gson.fromJson(myResponse, ActivityFeed[].class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addAll(res);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public class ActivityFeedAdapter extends ArrayAdapter<ActivityFeed> {
        public ActivityFeedAdapter(ActivityFeedListFragment context, ArrayList<ActivityFeed> activities) {
            super(context.getActivity(), 0, activities);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ActivityFeed feed = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_activity_feed, parent, false);
            }

            ImageView imgActorImage = (ImageView) convertView.findViewById(R.id.imgActorImage);
            ImageView imgObj = (ImageView) convertView.findViewById(R.id.imgObj);
            TextView tvActorName = (TextView) convertView.findViewById(R.id.tvActorName);
            TextView tvVerb = (TextView) convertView.findViewById(R.id.tvVerb);
            TextView tvTargetName = (TextView) convertView.findViewById(R.id.tvTargetName);
            TextView tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);

            //imgActorImage.setImageURI(feed.actor.profile_picture);
            tvActorName.setText(feed.actor.username);
            if(feed.actor.profilePicture != null) {
                new DownloadImageFromInternet(imgActorImage)
                        .execute("http://imitagram.wnt.io" + feed.actor.profilePicture);

            }

            if(feed.verb.equals("follow")) {
                tvVerb.setText("followed");

                if(mode.equals("following")) {
                    tvTargetName.setText(feed.target.username);
                } else {
                    tvTargetName.setText("you");
                }
            } else { // like
                tvVerb.setText("liked");

                if(mode.equals("following")) {
                    tvTargetName.setText(feed.target.username + "'s post");
                } else {
                    tvTargetName.setText("your post");
                }
            }

            Date date;
            String strDate = "";
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            try {
                date = df.parse(feed.createdAt);
                strDate = sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvCreatedAt.setText(strDate);

            if(feed.obj != null && feed.verb.equals("like")) {
                new DownloadImageFromInternet(imgObj)
                        .execute("http://imitagram.wnt.io" + feed.obj.image.standard_resolution);
            }


            return convertView;
        }
    }


    private String getToken() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String token = sharedPref.getString("token", "null");
        return token;
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