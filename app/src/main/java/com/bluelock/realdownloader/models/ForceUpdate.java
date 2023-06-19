package com.bluelock.realdownloader.models;

public class ForceUpdate {
    public Data data;
    public String msg;
    public boolean error;

    public class Data {
        public int id;
        public int version_code;
        public String image_url;
        public String refer_link;
        public int status;
    }

}
