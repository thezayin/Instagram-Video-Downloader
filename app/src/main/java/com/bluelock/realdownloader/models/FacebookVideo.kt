package com.bluelock.realdownloader.models

import com.google.gson.annotations.SerializedName

class FacebookVideo {
    @SerializedName("error")
    var error = false

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("data")
    var data: ArrayList<Data>? = null

    class Data {
        @SerializedName("url")
        var url: String? = null

        @SerializedName("format")
        var format: String? = null

        @SerializedName("ext")
        var ext: String? = null

        @SerializedName("format_id")
        var format_id: String? = null
    }
}