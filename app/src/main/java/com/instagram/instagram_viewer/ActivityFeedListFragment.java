package com.instagram.instagram_viewer;

import android.os.Bundle;
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
import java.util.ArrayList;


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

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            fetchData();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                final ActivityFeed[] res = gson.fromJson(myResponse, ActivityFeed[].class);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAll(res);
                    }
                });

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
            TextView tvActorName = (TextView) convertView.findViewById(R.id.tvActorName);
            TextView tvVerb = (TextView) convertView.findViewById(R.id.tvVerb);
            TextView tvTargetName = (TextView) convertView.findViewById(R.id.tvTargetName);
            TextView tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);

            //imgActorImage.setImageURI(feed.actor.profile_picture);
            tvActorName.setText(feed.actor.username);

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
            
            tvCreatedAt.setText(feed.createdAt);


            return convertView;
        }
    }


    private String getToken() {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "null");
        return token;
    }
}