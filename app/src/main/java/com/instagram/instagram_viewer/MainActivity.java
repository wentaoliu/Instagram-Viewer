package com.instagram.instagram_viewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private String token;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        token = getIntent().getStringExtra("token");
        saveToken(token);
        longitude = getIntent().getDoubleExtra("longitude",0);
        latitude = getIntent().getDoubleExtra("latitude",0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = (ViewPager) findViewById(R.id.vp);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        List<Fragment> list = new ArrayList<>();
        list.add(getInstance1(token,latitude,longitude));
        list.add(getInstance2(token));
        //list.add(TestFragment.newInstance("UserFeed"));
        //list.add(TestFragment.newInstance("Discover"));
        list.add(TestFragment.newInstance("UploadPhoto"));
        list.add(ActivityFeedFragment.newInstance());
        list.add(TestFragment.newInstance("Profile"));
        viewPagerAdapter.setList(list);

    }


    private void saveToken(String token) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            menuItem = item;
            switch (item.getItemId()) {
                case R.id.navigation_UserFeed:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_Discover:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_UploadPhoto:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_Activity:
                    viewPager.setCurrentItem(3);
                    return true;
                case R.id.navigation_Profile:
                    viewPager.setCurrentItem(4);
                    return true;
            }
            return false;
        }
    };
    public static UserFeedFragment getInstance1(String arg1,double longitude,double latitude){

        Bundle bundle = new Bundle();

        bundle.putString("token",arg1);
        bundle.putDouble("longitude",longitude);
        bundle.putDouble("latitude",latitude);

        UserFeedFragment fragment = new UserFeedFragment();

        fragment.setArguments(bundle);

        return fragment;

    }
    public static DiscoverFragment getInstance2(String arg1){

        Bundle bundle = new Bundle();

        bundle.putString("token",arg1);


        DiscoverFragment fragment = new DiscoverFragment();

        fragment.setArguments(bundle);

        return fragment;

    }
}
