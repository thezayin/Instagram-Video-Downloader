package com.bluelock.realdownloader.models

class FacebookReel {
    var error = false
    var msg: String? = null
    var data: ArrayList<Data>? = null

    class Data {
        var url: String? = null
        var format: String? = null
        var ext: String? = null
        var format_id: String? = null
    }
}