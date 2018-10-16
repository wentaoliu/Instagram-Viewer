package com.instagram.instagram_viewer;

import android.support.v4.util.LongSparseArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by mvince on 1/26/15.
 */
public class Comment {
    public String profileUrl;
    public String username;
    public String text;
    public String createdTime;
    public int userId;

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
    public void setUsername(String username){
        this.username = username;
    }
    public void setContent(String content){
        this.text = content;
    }
    public void setCreatedTime(long time){
        this.createdTime = Long.toString(time);
    }
}
