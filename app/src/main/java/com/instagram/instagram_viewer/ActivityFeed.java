package com.instagram.instagram_viewer;
import com.google.gson.annotations.SerializedName;


public class ActivityFeed {

    public int id;

    public String verb;

    public SimpleUser actor;

    public SimpleUser target;

    @SerializedName("created_at")
    public String createdAt;

    public class SimpleUser {

        public int id;

        public String username;

        @SerializedName("full_name")
        public String fullName;

        @SerializedName("profile_picture")
        public String profilePicture;
    }
}