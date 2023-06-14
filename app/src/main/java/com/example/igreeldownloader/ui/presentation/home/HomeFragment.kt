package com.example.igreeldownloader.ui.presentation.home


import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ads.GoogleManager
import com.example.ads.databinding.NativeAdBannerLayoutBinding
import com.example.ads.newStrategy.types.GoogleInterstitialType
import com.example.ads.ui.binding.loadNativeAd
import com.example.igreeldownloader.R
import com.example.igreeldownloader.databinding.FragmentHomeBinding
import com.example.igreeldownloader.db.Database
import com.example.igreeldownloader.di.ApiClient
import com.example.igreeldownloader.di.DownloadAPIInterface
import com.example.igreeldownloader.models.FVideo
import com.example.igreeldownloader.models.FacebookReel
import com.example.igreeldownloader.models.FacebookVideo
import com.example.igreeldownloader.models.InstaVideo
import com.example.igreeldownloader.ui.presentation.base.BaseFragment
import com.example.igreeldownloader.util.adapters.ListAdapter
import com.example.igreeldownloader.util.bottomsheets.Constants.FACEBOOK_URL
import com.example.igreeldownloader.util.bottomsheets.Constants.INSTA_URL
import com.example.igreeldownloader.util.bottomsheets.Constants.LIKEE_url
import com.example.igreeldownloader.util.bottomsheets.Constants.MOZ_URL
import com.example.igreeldownloader.util.bottomsheets.Constants.SNAPCHAT_URL
import com.example.igreeldownloader.util.bottomsheets.Constants.downloadVideos
import com.example.igreeldownloader.util.bottomsheets.Utils
import com.example.igreeldownloader.util.bottomsheets.Utils.RootDirectoryFacebook
import com.example.igreeldownloader.util.bottomsheets.Utils.RootDirectoryInsta
import com.example.igreeldownloader.util.bottomsheets.Utils.createFacebookFolder
import com.example.igreeldownloader.util.bottomsheets.Utils.createInstaFolder
import com.example.igreeldownloader.util.bottomsheets.Utils.isFacebookReelsUrl
import com.example.igreeldownloader.util.bottomsheets.Utils.isInstaUrl
import com.example.igreeldownloader.util.bottomsheets.Utils.isLikeeUrl
import com.example.igreeldownloader.util.bottomsheets.Utils.isMojUrl
import com.example.igreeldownloader.util.bottomsheets.Utils.isSnapChatUrl
import com.example.igreeldownloader.util.bottomsheets.Utils.startDownload
import com.example.igreeldownloader.util.bottomsheets.showVideoNotFoundBottomSheet
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URL
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private var nativeAd: NativeAd? = null

    @Inject
    lateinit var googleManager: GoogleManager

    lateinit var db: Database

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var urlType = 0

    private val strName = "facebook"
    private val strNameSecond = "fb"
    private lateinit var downloadAPIInterface: DownloadAPIInterface
    var videos: ArrayList<FVideo>? = null


    private val TOKEN_SUBSCRIBED_KEY = "token_subscribed"
    var adapter: ListAdapter? = null

    var sharedPref: SharedPreferences? = null
    private val topBannerActionUrl: String? = null
    private val activity: Activity? = null
    private var bannerLoadingPb: ProgressBar? = null
    private val drawerLayout: DrawerLayout? = null
    private var bannerCardView: CardView? = null
    private var bannerIv: ImageView? = null
    private val navigationView: NavigationView? = null
    private val imageMenu: ImageView? = null
    private var linkEt: EditText? = null
    private var downloadBtn: TextView? = null
    private var pasteBtn: TextView? = null
    private var wappBtn: ImageView? = null
    private var emptyList: ConstraintLayout? = null
    private var clipBoard: ClipboardManager? = null
    private val onCreateIsCalled = false

    override fun onCreatedView() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.finish()
        }

        binding.apply {
            observe()
            uiStateObserver()
            showNativeAd()
            askWritePermission()
            askReadPermission()


            //Database initialize and set callback
            db = Database.init(requireActivity())
            db.setCallback {
                Log.d("jeje_db", "this")
                updateListData()
            }

            requireActivity().registerReceiver(
                downloadComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        }
    }

    private fun initViews() {
        binding.apply {
            adapter = ListAdapter(requireActivity()
            ) { video ->
                when (video.state) {
                    FVideo.DOWNLOADING ->                         //video is in download state
                        Toast.makeText(
                            getApplicationContext(),
                            "Video Downloading",
                            Toast.LENGTH_LONG
                        ).show()

                    FVideo.PROCESSING ->                         //Video is processing
                        Toast.makeText(
                            getApplicationContext(),
                            "Video Processing",
                            Toast.LENGTH_LONG
                        ).show()

                    FVideo.COMPLETE -> {
                        val location = video.fileUri
                        val file = File(location)
                        if (file.exists()) {
                            val uri = Uri.parse(location)
                            val intent1 = Intent(Intent.ACTION_VIEW)
                            if (Utils.isVideoFile(
                                    getApplicationContext(),
                                    video.fileUri
                                )
                            ) intent1.setDataAndType(
                                uri,
                                "video/*"
                            ) else intent1.setDataAndType(uri, "image/*")
                            if (intent1.resolveActivity(requireActivity().getPackageManager()) != null) startActivity(
                                intent1
                            ) else Toast.makeText(
                                getApplicationContext(),
                                "No application can view this",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                getApplicationContext(),
                                "File doesn't exists",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("jeje_path", "onItemClickListener: file " + file.path)
                            db.deleteAVideo(video.downloadId)
                        }
                    }
                }
            }
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            updateListData()
            recyclerView.adapter = adapter
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapterPosition = viewHolder.adapterPosition
                    db.deleteAVideo(videos!![adapterPosition].downloadId)
                }
            }).attachToRecyclerView(recyclerView)
        }
    }

    fun updateListData() {
        binding.apply {
            Log.d("MainActivity.TAG", "updateListData: is called")
            videos = db.recentVideos
            if (videos != null && videos!!.size != 0) {
                adapter!!.setVideos(videos)
                emptyList!!.visibility = View.GONE
                recyclerView.setVisibility(View.VISIBLE)
            } else {
                recyclerView.setVisibility(View.GONE)
                emptyList!!.visibility = View.VISIBLE
            }
        }
    }
    
    private fun uiStateObserver() {
        binding.apply {
            btnDrawer.setOnClickListener {
                myDrawerLayout.openDrawer(GravityCompat.START)
            }
            btnGallery.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGalleryFragment())
            }
        }
    }

    private fun observe() {
        binding.apply {


            //Database initialize and set callback


            //Database initialize and set callback
            db = Database.init(requireContext())
            db.setCallback {
                Log.d("MainActivity.TAG", "onUpdateDatabase: MainActivity")
                updateListData()
            }

            requireActivity().registerReceiver(
                downloadComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )




            downloadAPIInterface = ApiClient.getInstance(
                resources
                    .getString(R.string.download_api_base_url)
            )
                .create(DownloadAPIInterface::class.java)

            edLink.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.toString().trim { it <= ' ' }.isEmpty()) {
                        btnDownload.visibility = View.GONE
                    } else {
                        btnDownload.visibility = View.VISIBLE
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int,
                    after: Int
                ) {
                    Log.d("jejeText", "before")
                }

                override fun afterTextChanged(s: Editable) {
                    Log.d("jejeYes", "after")
                }
            })


            btnDownload.setOnClickListener {
                val ll: String = edLink.text.toString().trim { it <= ' ' }
                if (ll == "") {
                    Utils.setToast(requireActivity(), resources.getString(R.string.enter_url))
                } else if (!Patterns.WEB_URL.matcher(ll).matches()) {
                    Utils.setToast(requireActivity(), resources.getString(R.string.enter_valid_url))
                } else {
                    if (urlType == 0) {
                        urlType = if (isInstaUrl(ll)) INSTA_URL else if (isSnapChatUrl(ll)) {
                            SNAPCHAT_URL
                        } else if (isLikeeUrl(ll)) {
                            LIKEE_url
                        } else if (isMojUrl(ll)) {
                            MOZ_URL
                        } else FACEBOOK_URL
                    }
                    when (urlType) {
                        FACEBOOK_URL -> getFacebookData()
                        INSTA_URL -> getInstaData()
                        SNAPCHAT_URL -> {}
                        LIKEE_url -> {}
                        MOZ_URL -> {}
                    }
                }

            }
        }
    }



    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }


    /**
     * this function called when download button is clicked and the url type is Facebook Url
     * this function call API in a background thread fetch video and download it
     */
    private fun getFacebookData() {
        binding.apply {
            try {
                createFacebookFolder()
                if (isFacebookReelsUrl(edLink.text.toString().trim { it <= ' ' })) {
                    getFacebookReelsData()
                    return
                }
                val url = URL(edLink.text.toString())
                val host = url.host

                if (host.contains(strName) || host.contains(strNameSecond)) {
                    Utils.showProgressDialog(requireActivity())

                    val videoLink: String = edLink.text.toString().trim { it <= ' ' }
                    val video = downloadAPIInterface.getFacebookVideos(videoLink)
                    video.enqueue(object : Callback<FacebookVideo?> {
                        override fun onResponse(
                            call: Call<FacebookVideo?>,
                            response: Response<FacebookVideo?>
                        ) {
                            Utils.hideProgressDialog(activity)
                            if (response.isSuccessful) {
                                val facebookVideo = response.body()
                                if (facebookVideo == null) {
                                    showStartDownloadDialogR("", FACEBOOK_URL)
                                    return
                                }
                                if (!facebookVideo.error) {
                                    val dataArrayList = facebookVideo.data
                                    val length = dataArrayList.size
                                    val map: MutableMap<String, String> = HashMap()
                                    for (i in 0 until length) {
                                        val data = dataArrayList[i]
                                        if (data.format_id == "hd" || data.format_id == "sd"
                                        ) {
                                            map[data.format_id] = data.url
                                        }
                                    }
                                    if (map.containsKey("hd")) {
                                        showStartDownloadDialogR(map["hd"], FACEBOOK_URL)
                                    } else if (map.containsKey("sd")) {
                                        showStartDownloadDialogR(map["sd"], FACEBOOK_URL)
                                    } else Log.d("MainActivity.TAG", "onResponse: map is null")
                                } else {
                                    showStartDownloadDialogR("", FACEBOOK_URL)
                                }
                            }
                        }

                        override fun onFailure(call: Call<FacebookVideo?>, t: Throwable) {
                            Utils.hideProgressDialog(activity)
                            showStartDownloadDialogR("", FACEBOOK_URL)
                        }
                    })
                } else {
                    Utils.setToast(requireActivity(), resources.getString(R.string.enter_valid_url))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getFacebookReelsData() {
        binding.apply {
            Utils.showProgressDialog(requireActivity())
            val videoLink: String = edLink.text.toString().trim { it <= ' ' }
            val video = downloadAPIInterface.getFacebookReels(videoLink)
            video.enqueue(object : Callback<FacebookReel?> {
                override fun onResponse(
                    call: Call<FacebookReel?>,
                    response: Response<FacebookReel?>
                ) {
                    Utils.hideProgressDialog(activity)
                    if (response.isSuccessful) {
                        val facebookVideo = response.body()
                        if (facebookVideo == null) {
                            showStartDownloadDialogR("", FACEBOOK_URL)
                            return
                        }
                        if (!facebookVideo.error) {
                            val dataArrayList = facebookVideo.data
                            val length = dataArrayList.size
                            val map: MutableMap<String, String> = HashMap()
                            for (i in 0 until length) {
                                val data = dataArrayList[i]
                                if (data.format_id == "hd" || data.format_id == "sd"
                                ) {
                                    map[data.format_id] = data.url
                                }
                            }
                            if (map.containsKey("hd")) {
                                showStartDownloadDialogR(map["hd"], FACEBOOK_URL)
                            } else if (map.containsKey("sd")) {
                                showStartDownloadDialogR(map["sd"], FACEBOOK_URL)
                            } else Log.d("MainActivity.TAG", "onResponse: map is null")
                        } else {
                            showStartDownloadDialogR("", FACEBOOK_URL)
                        }
                    }
                }

                override fun onFailure(call: Call<FacebookReel?>, t: Throwable) {
                    Utils.hideProgressDialog(activity)
                    showStartDownloadDialogR("", FACEBOOK_URL)
                }
            })
        }
    }


    private fun getInstaData() {
        binding.apply {
            createInstaFolder()
            Utils.showProgressDialog(requireActivity())
            val videoLink: String = edLink.text.toString().trim { it <= ' ' }
            val video = downloadAPIInterface.getInstaVideos(videoLink)
            video.enqueue(object : Callback<InstaVideo?> {
                override fun onResponse(call: Call<InstaVideo?>, response: Response<InstaVideo?>) {
                    Utils.hideProgressDialog(activity)
                    if (response.isSuccessful) {
                        val instaVideo = response.body()
                        if (instaVideo == null) {
                            showStartDownloadDialogR("", FACEBOOK_URL)
                            return
                        }
                        if (!instaVideo.error) {
                            val dataArrayList = instaVideo.data
                            val length = dataArrayList.size
                            var linkIndex = -1
                            for (i in 0 until length) {
                                val format = dataArrayList[i].format
                                if (!format.startsWith("dash")) {
                                    try {
                                        val formatSArray =
                                            format.split(" ".toRegex())
                                                .dropLastWhile { it.isEmpty() }
                                                .toTypedArray()
                                        val array =
                                            formatSArray[formatSArray.size - 1].split("x".toRegex())
                                                .dropLastWhile { it.isEmpty() }
                                                .toTypedArray()
                                        val res = array[0].toInt()
                                        if (res > linkIndex) {
                                            linkIndex = i
                                        }
                                    } catch (e: Exception) {
                                        showStartDownloadDialogR("", INSTA_URL)
                                    }
                                }
                            }
                            if (linkIndex != -1) {
                                showStartDownloadDialogR(dataArrayList[linkIndex].url, INSTA_URL)
                            } else {
                                showStartDownloadDialogR("", INSTA_URL)
                            }
                        } else {
                            showStartDownloadDialogR("", INSTA_URL)
                        }
                    }
                }

                override fun onFailure(call: Call<InstaVideo?>, t: Throwable) {
                    Utils.hideProgressDialog(activity)
                    showStartDownloadDialogR("", INSTA_URL)
                }
            })
        }
    }

    private fun showStartDownloadDialogR(link: String?, urlType: Int) {
        try {

            if (link == null || link == "") {
                showVideoNotFoundBottomSheet(googleManager) {}
                return
            }

            val dialog = BottomSheetDialog(requireActivity(), R.style.SheetDialog)
            dialog.setContentView(R.layout.dialog_bottom_start_download)
            val videoQualityTv = dialog.findViewById<Button>(R.id.btn_clear)
            videoQualityTv?.setOnClickListener {
                showInterstitialAd {
                    videoDownloadR(link, urlType)
                    dialog.dismiss()
                }
            }
            dialog.show()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun videoDownloadR(videoUrl: String?, urlType: Int) {
        binding.apply {
            //Log.d(TAG, "onPostExecute: " + result);
            if (videoUrl == null || videoUrl == "") {
                Toast.makeText(activity, "This video quality is not available", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            val fVideo: FVideo = startDownload(activity, videoUrl, urlType) ?: return

            downloadVideos[fVideo.downloadId] = fVideo
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGalleryFragment())
            edLink.setText("")
        }
    }

    private fun askWritePermission() {
        val result =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (Build.VERSION.SDK_INT < 32 && result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
    }

    private fun askReadPermission() {
        val result =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        if (Build.VERSION.SDK_INT < 32 && result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
    }

    private fun showNativeAd() {
        nativeAd = googleManager.createNativeAdSmall()
        nativeAd?.let {
            val nativeAdLayoutBinding = NativeAdBannerLayoutBinding.inflate(layoutInflater)
            nativeAdLayoutBinding.nativeAdView.loadNativeAd(ad = it)
            binding.nativeView.removeAllViews()
            binding.nativeView.addView(nativeAdLayoutBinding.root)
            binding.nativeView.visibility = View.VISIBLE
        }
    }

    private fun showInterstitialAd(callback: () -> Unit) {
        val ad: InterstitialAd? =
            googleManager.createInterstitialAd(GoogleInterstitialType.MEDIUM)

        if (ad == null) {
            callback.invoke()
            return
        } else {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    callback.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    super.onAdFailedToShowFullScreenContent(error)
                    callback.invoke()
                }
            }
            ad.show(requireActivity())
        }
    }


    private val downloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadVideos.containsKey(id)) {
                Log.d("receiver", "onReceive: download complete")
                val fVideo = db.getVideo(id)
                var videoPath: String? = null
                if (fVideo.videoSource === FVideo.FACEBOOK) {
                    videoPath = Environment.getExternalStorageDirectory().toString() +
                            "/Download" + RootDirectoryFacebook + fVideo.fileName
                } else if (fVideo.videoSource === FVideo.INSTAGRAM) {
                    videoPath = Environment.getExternalStorageDirectory().toString() +
                            "/Download" + RootDirectoryInsta + fVideo.fileName
                }
                Toast.makeText(getApplicationContext(), "Download complete", Toast.LENGTH_SHORT)
                    .show()
                db.updateState(id, FVideo.COMPLETE)
                if (videoPath != null) db.setUri(id, videoPath)
                downloadVideos.remove(id)
            }
        }
    }
}

