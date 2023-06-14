package com.example.igreeldownloader.util.bottomsheets

import androidx.fragment.app.Fragment
import com.example.igreeldownloader.R
import com.example.igreeldownloader.databinding.LayoutProgressDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.showProgressBottomSheet(
    // googleManager: GoogleManager,
    text: String?,
    boolean: Boolean
) {

//    fun showInterstitialAd(callback: () -> Unit) {
//        val ad: InterstitialAd? =
//            googleManager.createInterstitialAd(GoogleInterstitialType.MEDIUM)
//
//        if (ad == null) {
//            callback.invoke()
//            return
//        } else {
//            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
//                override fun onAdDismissedFullScreenContent() {
//                    super.onAdDismissedFullScreenContent()
//                    callback.invoke()
//                }
//
//                override fun onAdFailedToShowFullScreenContent(error: AdError) {
//                    super.onAdFailedToShowFullScreenContent(error)
//                    callback.invoke()
//                }
//            }
//            ad.show(requireActivity())
//        }
//    }

    BottomSheetDialog(
        requireContext(),
        R.style.SheetDialog
    ).also { dialog ->
        val binding = LayoutProgressDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.behavior.isDraggable = false
        dialog.setCanceledOnTouchOutside(false)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            if (boolean) {
                tvTitle.text = text
            }


//            var nativeAd: NativeAd? = googleManager.createNativeAdSmall()
//            Log.d("jejeAd", googleManager.createNativeAdSmall().toString())
//            nativeAd?.let {
//                val nativeAdLayoutBinding = NativeAdBannerLayoutBinding.inflate(layoutInflater)
//                nativeAdLayoutBinding.nativeAdView.loadNativeAd(ad = it)
//                binding.nativeView.removeAllViews()
//                binding.nativeView.addView(nativeAdLayoutBinding.root)
//                binding.nativeView.visibility = View.VISIBLE
//            }


        }
        dialog.show()
    }
}