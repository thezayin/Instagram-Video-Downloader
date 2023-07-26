package com.bluelock.realdownloader.di

import com.bluelock.realdownloader.models.FacebookReel
import com.bluelock.realdownloader.models.FacebookVideo
import com.bluelock.realdownloader.models.InstaVideo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DownloadAPIInterface {
    @GET("/link.php")
    fun getFacebookVideos(@Query("video") videoUrl: String?): Call<FacebookVideo?>?

    @GET("/instagram.php")
    fun getInstaVideos(@Query("video") videoUrl: String?): Call<InstaVideo?>?

    @GET("/instagram.php")
    fun getFacebookReels(@Query("video") videoUrl: String?): Call<FacebookReel?>?
}