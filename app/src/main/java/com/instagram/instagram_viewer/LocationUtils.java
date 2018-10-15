package com.instagram.instagram_viewer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import java.util.List;

public class LocationUtils {

    private LocationManager lm;

    LocationListener locationListener;

    private String locationProvider;
    private Location location;
    private Context mContext;

    LocationUtils(UserFeedFragment context) {
        mContext = context.getActivity();
        checkGPSSettings();
    }
    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void startLocalisation() {

        // parameters of location service
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // localisation uses a lot of power, consider your task cycle

        //String provider = lm.getBestProvider(criteria, true);

        // can also use a specific provider

        // cellular or WIFI network can localise me
        String providerNET = LocationManager.NETWORK_PROVIDER;

        // gps signal often naive
        String providerGPS = LocationManager.GPS_PROVIDER;


        // must call this before using getLastKnownLocation
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }



        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (gps_enabled) {
            Log.d("haha", " gps_enabled");

            //requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener)

            lm.requestLocationUpdates(providerGPS, 0, 0, locationListener);
            gps_loc = lm.getLastKnownLocation(providerGPS);
        }
        if (network_enabled){
            Log.d("haha", " net_enabled");
            lm.requestLocationUpdates(providerNET, 0, 0, locationListener);
            net_loc = lm.getLastKnownLocation(providerNET);
        }

        if (gps_loc != null && net_loc != null) {

            Log.d("haha", "both available location");

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)

        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
                Log.d("haha", "gps available location");
            } else if (net_loc != null) {
                finalLoc = net_loc;
                Log.d("haha", "net available location");
            }
        }
//        if (gps_loc != null) {
//                finalLoc = gps_loc;}

        if (finalLoc != null) {
            setLocation( finalLoc );
            double latitude = finalLoc.getLatitude();

            double longitude = finalLoc.getLongitude();

            Log.d("haha", "latitude：" + latitude + "\nlongitude" + longitude);

            // if we are in melbourne, we get negative latitude.
            // it means south part of the earth.

        } else {
            Log.d("haha", "no available location");


            //startLocalisation();
        }


    }

    public void checkGPSSettings() {
        lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener()
        {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                //location.getProvider();
                Log.d("haha", "" + location.getProvider() + " Location latitude " + latitude + "\nlongitude:" + longitude);
            }
        };




        boolean GPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        String[] permissionsArray = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (GPSEnabled) {
            // Android 6.0+
            if (Build.VERSION.SDK_INT >= 23) {
                if (!checkPermissions(mContext, permissionsArray)) {
                    // request code 1

                    Log.d("haha", "request");

                } else {
                    // Permission has already been granted
                    Log.d("haha", "line 52");
                    startLocalisation();
                }


            } else {
                // no runtime check
                Log.d("haha", "line 74");
                startLocalisation();
            }
        } else {
            Log.d("haha", "line 82");
            Toast.makeText(mContext, "GPS Not Enabled", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            // request code 2
        }
    }


    private void setLocation(Location location) {
        this.location = location;
        String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
        System.out.println(address);
    }

    //获取经纬度
    public Location showLocation() {
        return location;
    }


}
