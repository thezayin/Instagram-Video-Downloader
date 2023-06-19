package com.bluelock.realdownloader.models;

import com.google.gson.annotations.SerializedName;

public class TopBanner{
    @SerializedName("data")
    public Data data;
    @SerializedName("msg")
    public String msg;
    @SerializedName("error")
    public boolean error;

    public class Data{
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("image_url")
        public String image_url;
        @SerializedName("action_url")
        public String action_url;
        @SerializedName("status")
        public int status;
    }
}