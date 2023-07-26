package com.bluelock.realdownloader.util

import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.bluelock.realdownloader.db.Database
import com.bluelock.realdownloader.models.FVideo

class BroadcastService : Service() {
    var db: Database? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: broadcastService")
        db = Database.init(applicationContext)
        registerBroadcastRecerver()
    }

    private fun registerBroadcastRecerver() {
        Log.d(TAG, "registerBroadcastRecerver: broadcast service")
        val downloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (Constants.downloadVideos.containsKey(id)) {
                    Log.d("receiver", "onReceive: download complete")
                    val fVideo = db!!.getVideo(id)
                    var videoPath: String? = null
                    if (fVideo.videoSource == FVideo.FACEBOOK) {
                        videoPath = Environment.getExternalStorageDirectory().toString() +
                                "/Download" + Utils.RootDirectoryFacebook + fVideo.fileName
                    } else if (fVideo.videoSource == FVideo.INSTAGRAM) {
                        videoPath = Environment.getExternalStorageDirectory().toString() +
                                "/Download" + Utils.RootDirectoryInsta + fVideo.fileName
                    } else if ((fVideo as FVideo).videoSource == FVideo.SNAPCHAT) {
                        videoPath = Environment.getExternalStorageDirectory().toString() +
                                "/Download" + Utils.RootDirectorySnapchat + fVideo.fileName
                    } else if (fVideo.videoSource == FVideo.LIKEE) {
                        videoPath = Environment.getExternalStorageDirectory().toString() +
                                "/Download" + Utils.RootDirectoryLikee + fVideo.fileName
                    } else if (fVideo.videoSource == FVideo.MOZ) {
                        videoPath = Environment.getExternalStorageDirectory().toString() +
                                "/Download" + Utils.RootDirectoryMoz + fVideo.fileName
                    }
                    Toast.makeText(applicationContext, "Download complete", Toast.LENGTH_SHORT)
                        .show()
                    db!!.updateState(id, FVideo.COMPLETE)
                    if (videoPath != null) db!!.setUri(id, videoPath)
                    Constants.downloadVideos.remove(id)
                }
            }
        }
        registerReceiver(
            downloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    companion object {
        private const val TAG = "BroadcastService"
    }
}