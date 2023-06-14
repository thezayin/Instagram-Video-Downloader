package com.example.igreeldownloader.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SnapVideo {
    @SerializedName("error")
    public boolean error;
    @SerializedName("msg")
    public String msg;
    @SerializedName("data")
    public Data data;


    public class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("title")
        public String title;
        @SerializedName("timestamp")
        public int timestamp;
        @SerializedName("description")
        public String description;
        @SerializedName("thumbnail")
        public String thumbnail;
        @SerializedName("age_limit")
        public int age_limit;
        @SerializedName("_type")
        public String _type;
        @SerializedName("url")
        public String url;
        @SerializedName("ext")
        public String ext;
        @SerializedName("thumbnails")
        public ArrayList<Thumbnail> thumbnails;
        @SerializedName("duration")
        public double duration;
        @SerializedName("view_count")
        public int view_count;
        @SerializedName("original_url")
        public String original_url;
        @SerializedName("webpage_url")
        public String webpage_url;
        @SerializedName("webpage_url_basename")
        public String webpage_url_basename;
        @SerializedName("webpage_url_domain")
        public String webpage_url_domain;
        @SerializedName("extractor")
        public String extractor;
        @SerializedName("extractor_key")
        public String extractor_key;
        @SerializedName("playlist")
        public Object playlist;
        @SerializedName("playlist_index")
        public Object playlist_index;
        @SerializedName("display_id")
        public String display_id;
        @SerializedName("fulltitle")
        public String fulltitle;
        @SerializedName("duration_string")
        public String duration_string;
        @SerializedName("upload_date")
        public String upload_date;
        @SerializedName("requested_subtitles")
        public Object requested_subtitles;
        @SerializedName("_has_drm")
        public Object _has_drm;
        @SerializedName("protocol")
        public String protocol;
        @SerializedName("resolution")
        public Object resolution;
        @SerializedName("dynamic_range")
        public String dynamic_range;
        @SerializedName("aspect_ratio")
        public Object aspect_ratio;
        @SerializedName("http_headers")
        public HttpHeaders http_headers;
        @SerializedName("video_ext")
        public String video_ext;
        @SerializedName("audio_ext")
        public String audio_ext;
        @SerializedName("format_id")
        public String format_id;
        @SerializedName("format")
        public String format;

    }

    public class HttpHeaders {
        @SerializedName("User-Agent")
        public String user_Agent;
        @SerializedName("accept")
        public String accept;
        @SerializedName("Accept-Language")
        public String accept_Language;
        @SerializedName("Sec-Fetch-Mode")
        public String sec_Fetch_Mode;
    }

    public class Thumbnail {
        @SerializedName("url")
        public String url;
        @SerializedName("id")
        public String id;
    }
}
