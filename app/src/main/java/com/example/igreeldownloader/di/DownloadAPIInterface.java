package com.example.igreeldownloader.di;



import com.example.igreeldownloader.models.FacebookReel;
import com.example.igreeldownloader.models.FacebookVideo;
import com.example.igreeldownloader.models.InstaVideo;
import com.example.igreeldownloader.models.LikeeVideo;
import com.example.igreeldownloader.models.MojVideo;
import com.example.igreeldownloader.models.SnapVideo;


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
