package com.example.igreeldownloader.ui.presentation.gallery

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ads.GoogleManager
import com.example.ads.databinding.NativeAdBannerLayoutBinding
import com.example.ads.ui.binding.loadNativeAd
import com.example.igreeldownloader.databinding.FragmentGalleryBinding
import com.example.igreeldownloader.db.Database
import com.example.igreeldownloader.models.FVideo
import com.example.igreeldownloader.ui.presentation.base.BaseFragment
import com.example.igreeldownloader.util.adapters.ListAdapter
import com.example.igreeldownloader.util.bottomsheets.Utils
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : BaseFragment<FragmentGalleryBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGalleryBinding
        get() = FragmentGalleryBinding::inflate

    private var nativeAd: NativeAd? = null

    @Inject
    lateinit var googleManager: GoogleManager

    private var adapter: ListAdapter? = null
    private var videos: ArrayList<FVideo>? = null
    private val db: Database? = null
    override fun onCreatedView() {
        observer()
        showNativeAd()

    }

    private fun observer() {
        binding.apply {

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            initViews()

        }
    }

    private fun updateListData() {
        binding.apply {
            Log.d("MainActivity.TAG", "updateListData: is called")
            videos = db?.recentVideos
            if (videos != null && videos!!.size != 0) {
                adapter!!.setVideos(videos)
                clEmpty.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.GONE
                clEmpty.visibility = View.VISIBLE
            }
        }
    }


    private fun initViews() {
        binding.apply {

            adapter = ListAdapter(requireActivity(),
                ListAdapter.ItemClickListener { video ->
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
                            //complete download and processing ready to use
                            val location = video.fileUri

                            //Downloaded video play into video player
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
                                if (intent1.resolveActivity(requireActivity().packageManager) != null) startActivity(
                                    intent1
                                ) else Toast.makeText(
                                    getApplicationContext(),
                                    "No application can view this",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {

                                //File doesn't exists
                                Toast.makeText(
                                    getApplicationContext(),
                                    "File doesn't exists",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.d("MainActivity.TAG", "onItemClickListener: file " + file.path)

                                //Delete the video instance from the list
                                db!!.deleteAVideo(video.downloadId)
                            }
                        }
                    }
                })
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            updateListData()
            recyclerView.setAdapter(adapter)
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
                    db!!.deleteAVideo(videos!![adapterPosition].downloadId)
                }
            }).attachToRecyclerView(recyclerView)


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
}