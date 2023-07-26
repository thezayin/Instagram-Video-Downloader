package com.bluelock.realdownloader.util;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.bluelock.realdownloader.R;
import com.bluelock.realdownloader.db.Database;
import com.bluelock.realdownloader.models.FVideo;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
    private static final String TAG = "Utils";
    private static final String INSTA_REGEX = "(https?:\\/\\/(?:www\\.)?instagram\\.com\\/([^/?#&]+)).*";
    private static final String SNAPCHAT_REGEX = "^https:\\/\\/t\\.snapchat\\.com\\/[a-zA-Z0-9]+$";
    private static final String LIKEE_REGEX = "^https?:\\/\\/l\\.likee\\.video\\/v\\/[a-zA-Z0-9]{6,}$";
    private static final String MOJAPP_REGEX = "^https?:\\/\\/mojapp\\.in\\/@[\\w.-]+\\/video\\/\\d+\\?referrer=[\\w-]+$";
    public static BottomSheetDialog customDialog;
    public static String RootDirectoryFacebook = "/Smart_Downloader/facebook/";
    public static String RootDirectoryInsta = "/Smart_Downloader/instagram/";
    public static String RootDirectorySnapchat = "/Smart_Downloader/snapchat/";
    public static String RootDirectoryLikee = "/Smart_Downloader/likee/";
    public static String RootDirectoryMoz = "/Smart_Downloader/moj/";
    public static File RootDirectoryFacebookShow = new File(Environment.getExternalStorageDirectory() + "/Download" + RootDirectoryFacebook);
    public static File RootDirectoryInstaShow = new File(Environment.getExternalStorageDirectory() + "/Download" + RootDirectoryInsta);
    private final Context context;

    public Utils(Context mContext) {
        context = mContext;
    }

    public static void setToast(Context mContext, String str) {
        Toast toast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Create directory facebook video downloader in download directory
     */
    public static void createFacebookFolder() {
        if (!RootDirectoryFacebookShow.exists()) {
            RootDirectoryFacebookShow.mkdirs();
        }
    }

    public static void createInstaFolder() {
        if (!RootDirectoryInstaShow.exists()) {
            RootDirectoryInstaShow.mkdirs();
        }
    }


    public static void showProgressDialog(Activity activity) {
        System.out.println("Show");
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
        customDialog = new BottomSheetDialog(activity, R.style.SheetDialog);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View mView = inflater.inflate(R.layout.layout_progress_dialog, null);
        customDialog.setCancelable(false);
        customDialog.setContentView(mView);
        if (!customDialog.isShowing() && !activity.isFinishing()) {
            customDialog.show();
        }
    }

    public static void hideProgressDialog(Activity activity) {
        System.out.println("Hide");
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }


    public static FVideo startDownload(Context context, String videoUrl, int urlType) {

        setToast(context, context.getResources().getString(R.string.download_started));

        String fileName;

        if (urlType == Constants.FACEBOOK_URL) {
            fileName = "facebook_" + System.currentTimeMillis() + ".mp4";
        } else {
            fileName = "inst_" + System.currentTimeMillis() + ".mp4";
        }
        String downloadLocation = RootDirectoryInsta + fileName;

        Uri uri = Uri.parse(videoUrl); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);  // This will show notification on top when downloading the file.
        request.setTitle(fileName + ""); // Title for notification.
        request.setVisibleInDownloadsUi(true);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadLocation);// Storage directory path
        long downloadId = ((DownloadManager) context.getSystemService(DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading



        FVideo video = new FVideo(Environment.getExternalStorageDirectory() +
                downloadLocation, fileName, downloadId, false, System.currentTimeMillis());
        video.setState(FVideo.DOWNLOADING);

        if (urlType == Constants.FACEBOOK_URL)
            video.setVideoSource(FVideo.FACEBOOK);
        else
            video.setVideoSource(FVideo.INSTAGRAM);

        Database db = Database.init(context);
        db.addVideo(video);


        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + fileName).getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("jeje_videoProcess", "onScanCompleted: " + path);
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }

        FVideo fVideo = new FVideo(Environment.getExternalStorageDirectory() +
                downloadLocation + fileName,
                fileName, downloadId, false, System.currentTimeMillis());
        fVideo.setVideoSource(FVideo.FACEBOOK);
        return fVideo;
    }


    public static FVideo downloadVideoInsta(Context context, String videoUrl, String fileName) {
        setToast(context, context.getResources().getString(R.string.download_started));
        Uri uri = Uri.parse(videoUrl); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);  // This will show notification on top when downloading the file.
        request.setTitle(fileName + ""); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, RootDirectoryInsta + fileName);// Storage directory path
        long downloadId = ((DownloadManager) context.getSystemService(DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

        //Creating a video object to track download is completed
        FVideo video = new FVideo(Environment.getExternalStorageDirectory() +
                "/Download" + RootDirectoryInsta, fileName, downloadId, false, System.currentTimeMillis());
        video.setState(FVideo.DOWNLOADING);
        video.setVideoSource(FVideo.INSTAGRAM);


        Database db = Database.init(context);
        db.addVideo(video);

        Log.d(TAG, "startDownload: " + Environment.getDataDirectory().getPath() + RootDirectoryFacebook + fileName);

        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + fileName).getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("videoProcess", "onScanCompleted: " + path);
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
        FVideo fVideo = new FVideo(Environment.getExternalStorageDirectory() +
                "/Download" + RootDirectoryInsta + fileName,
                fileName, downloadId, false, System.currentTimeMillis());
        fVideo.setVideoSource(FVideo.INSTAGRAM);

        return fVideo;

    }

    public static boolean isFacebookReelsUrl(String url) {
        return url.startsWith("https://www.facebook.com/reel");
    }

    public static boolean isInstaUrl(String url) {
        return url.matches(INSTA_REGEX);
    }

    public static boolean isSnapChatUrl(String url) {
        return url.matches(SNAPCHAT_REGEX);
    }

    public static boolean isLikeeUrl(String url) {
        return url.matches(LIKEE_REGEX);
    }

    public static boolean isMojUrl(String url) {
        return url.matches(MOJAPP_REGEX);
    }

    public static boolean isFacebookUrl(String url) {
        return url.contains("facebook") || url.contains("fb");
    }

    public static boolean appInstalledOrNot(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static void deleteVideoFromFile(Context context, FVideo video) {
        if (video.getState() == FVideo.COMPLETE) {

            File file = new File(Objects.requireNonNull(video.getFileUri()));
            Database db = Database.init(context);

            if (file.exists()) {
                new AlertDialog.Builder(context)
                        .setTitle("Want to delete this file?")
                        .setMessage("This will delete file from your Disk")
                        .setPositiveButton("Yes", (dialog, which) -> {

                            boolean isDeleted = file.delete();
                            if (isDeleted)
                                Toast.makeText(context, "Video deleted", Toast.LENGTH_SHORT).show();


                            db.deleteAVideo(video.getDownloadId());
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                        .show();
            } else {
                db.deleteAVideo((video.getDownloadId()));
                Toast.makeText(context, "video not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "File downloading...", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isVideoFile(Context context, String path) {
        if (path.startsWith("content")) {
            DocumentFile fromTreeUri = DocumentFile.fromSingleUri(context, Uri.parse(path));
            String mimeType = fromTreeUri.getType();
            return mimeType != null && mimeType.startsWith("video");
        } else {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("video");
        }
    }
}