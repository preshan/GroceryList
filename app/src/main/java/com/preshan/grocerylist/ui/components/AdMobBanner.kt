package com.preshan.grocerylist.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.preshan.grocerylist.R
import com.preshan.grocerylist.ads.AdConsentManager
import com.preshan.grocerylist.ads.AdConsentManager.ConsentUiState

/**
 * Adaptive banner for Compose. Loads only after UMP allows ad requests.
 * Reports load success via [onAdLoadedChange]; no error UI is shown on failure.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String? = null,
    onAdLoadedChange: (Boolean) -> Unit = {}
) {
    if (LocalInspectionMode.current) {
        return
    }
    val consentState by AdConsentManager.state.collectAsState()
    if (consentState != ConsentUiState.CanRequestAds) {
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
    DisposableEffect(adView, consentState, resolvedUnitId) {
        onAdLoadedChange(false)

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                onAdLoadedChange(true)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                onAdLoadedChange(false)
            }
        }
        adView.loadAd(AdConsentManager.buildAdRequest())

        onDispose {
            adView.destroy()
            onAdLoadedChange(false)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { adView }
    )
}

/**
 * Home-screen banner with card styling. Hides the card until an ad loads;
 * zero layout height when offline or load fails (no error message).
 */
@Composable
fun HomeAdMobBanner(modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        return
    }
    val consentState by AdConsentManager.state.collectAsState()
    if (consentState != ConsentUiState.CanRequestAds) {
        return
    }

    val context = LocalContext.current
    val resolvedUnitId = context.getString(R.string.admob_banner_home)
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
            adUnitId = resolvedUnitId
        }
    }
    var isAdLoaded by remember(resolvedUnitId) { mutableStateOf(false) }

    DisposableEffect(adView, consentState, resolvedUnitId) {
        isAdLoaded = false

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                isAdLoaded = true
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                isAdLoaded = false
            }
        }
        adView.loadAd(AdConsentManager.buildAdRequest())

        onDispose {
            adView.destroy()
            isAdLoaded = false
        }
    }

    val bannerModifier = Modifier
        .fillMaxWidth()
        .then(
            if (isAdLoaded) {
                Modifier.wrapContentHeight().padding(vertical = 4.dp)
            } else {
                Modifier.height(0.dp)
            }
        )

    if (isAdLoaded) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            AndroidView(
                modifier = bannerModifier,
                factory = { adView }
            )
        }
    } else {
        AndroidView(
            modifier = bannerModifier,
            factory = { adView }
        )
    }
}
