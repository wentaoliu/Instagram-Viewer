package com.instagram.instagram_viewer;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by mvince on 1/25/15.
 */
public class InstagramPhoto {
    // username, caption, image_url, height, likes_count
    public String username;
    public String caption;
    public String createdTime;
    public String imageUrl;
    public String profileUrl;
    public String comment1;
    public String user1;
    public String comment2;
    public String user2;
    public String id;
    public int imageHeight;
    public int likesCount;
    public int commentsCount;
    public double lat;
    public double lng;
    public double distance;

    public String getRelativeTime() throws ParseException {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");//如2016-08-10 20:40
        long ct = simpleFormat.parse(createdTime).getTime()/1000;
        //long ct = System.currentTimeMillis()-1000;
        long now = System.currentTimeMillis()/1000;
        long elapsedSeconds = now - ct;


        if (elapsedSeconds < 60) { // less than a minute
            return "just now";
        } else if (elapsedSeconds < 3600) { // less than an hour
            return String.format("%.0fm", Math.floor(elapsedSeconds / 60));
        } else if (elapsedSeconds < 86400) { // less than a day
            return String.format("%.0fh", Math.floor(elapsedSeconds / 3600));
        } else {
            return String.format("%.0fd", Math.floor(elapsedSeconds / 86400));
        }
    }
    public String getRelativeDistance(){
        if (distance < 1000){
            return String.format("%.0fm",distance);
        }else{
            return String.format("%.0fkm",distance/1000);
        }
    }
}
