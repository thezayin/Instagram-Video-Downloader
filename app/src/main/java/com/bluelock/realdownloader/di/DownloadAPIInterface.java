package com.bluelock.realdownloader.di;


import com.bluelock.realdownloader.models.FacebookReel;
import com.bluelock.realdownloader.models.FacebookVideo;
import com.bluelock.realdownloader.models.InstaVideo;
import com.bluelock.realdownloader.models.LikeeVideo;
import com.bluelock.realdownloader.models.MojVideo;
import com.bluelock.realdownloader.models.SnapVideo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DownloadAPIInterface {
    @GET("/link.php")
    Call<FacebookVideo> getFacebookVideos(@Query("video") String videoUrl);

    @GET("/instagram.php")
    Call<InstaVideo> getInstaVideos(@Query("video") String videoUrl);

    @GET("/snap.php")
    Call<SnapVideo> getSnapVideos(@Query("video") String videoUrl);

    @GET("/instagram.php")
    Call<LikeeVideo> getLikeeVideos(@Query("video") String videoUrl);

    @GET("/instagram.php")
    Call<MojVideo> getMojVideos(@Query("video") String videoUrl);

    @GET("/instagram.php")
    Call<FacebookReel> getFacebookReels(@Query("video") String videoUrl);
}
