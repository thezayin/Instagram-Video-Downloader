package com.bluelock.realdownloader.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.bluelock.realdownloader.db.Database
import com.bluelock.realdownloader.models.FVideo
import com.bluelock.realdownloader.util.bottomsheets.Constants
import com.bluelock.realdownloader.util.bottomsheets.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    var db: Database? = null


    val downloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (Constants.downloadVideos.containsKey(id)) {
                Log.d("receiver", "onReceive: download complete")
                val fVideo: FVideo? = db?.getVideo(id)
                var videoPath: String? = null
                if (fVideo?.getVideoSource() === FVideo.FACEBOOK) {
                    videoPath = Environment.getExternalStorageDirectory().toString() +
                            "/Download" + Utils.RootDirectoryFacebook + fVideo?.getFileName()
                } else if (fVideo?.getVideoSource() === FVideo.INSTAGRAM) {
                    videoPath = Environment.getExternalStorageDirectory().toString() +
                            "/Download" + Utils.RootDirectoryInsta + fVideo?.getFileName()
                } else if (fVideo?.getVideoSource() === FVideo.SNAPCHAT) {
                    videoPath = Environment.getExternalStorageDirectory().toString() +
                            "/Download" + Utils.RootDirectorySnapchat + fVideo?.getFileName()
                } else if (fVideo?.getVideoSource() === FVideo.LIKEE) {
                    videoPath = Environment.getExternalStorageDirectory().toString() +
                            "/Download" + Utils.RootDirectoryLikee + fVideo?.getFileName()
                } else if (fVideo?.getVideoSource() === FVideo.MOZ) {
                    videoPath = Environment.getExternalStorageDirectory().toString() +
                            "/Download" + Utils.RootDirectoryMoz + fVideo?.getFileName()
                }
                Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show()
                db?.updateState(id, FVideo.COMPLETE)
                if (videoPath != null) db?.setUri(id, videoPath)
                Constants.downloadVideos.remove(id)
            }
        }
    }

}