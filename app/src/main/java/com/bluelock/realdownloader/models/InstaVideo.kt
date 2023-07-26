package com.bluelock.realdownloader.models

class InstaVideo {
    var error = false
    var msg: String? = null
    var data: ArrayList<Data>? = null

    inner class Data {
        var url: String? = null
        var format: String? = null
        var ext: String? = null
        var format_id: String? = null
    }
}