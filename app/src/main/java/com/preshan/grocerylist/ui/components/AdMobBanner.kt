package com.preshan.grocerylist.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.preshan.grocerylist.R

/**
 * Adaptive banner for Compose (home screen). Uses full width; height follows AdMob guidance.
 * https://developers.google.com/admob/android/banner
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String? = null
) {
    if (LocalInspectionMode.current) {
        return
    }
    val context = LocalContext.current
    val resolvedUnitId = adUnitId ?: context.getString(R.string.admob_banner_home)
    val adSize = remember(context) {
        val activity = context as? Activity
        if (activity != null) {
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, AdSize.FULL_WIDTH)
        } else {
            AdSize.BANNER
        }
    }
    val adView = remember(resolvedUnitId, adSize) {
        AdView(context).apply {
            setAdSize(adSize)
            this.adUnitId = resolvedUnitId
        }
    }

    DisposableEffect(adView) {
        adView.loadAd(AdRequest.Builder().build())
        onDispose {
            adView.destroy()
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        factory = { adView }
    )
}
