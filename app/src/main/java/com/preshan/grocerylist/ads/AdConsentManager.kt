package com.preshan.grocerylist.ads

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Google UMP consent for AdMob. Core app features work without ads if consent cannot load.
 * https://developers.google.com/admob/android/privacy
 */
object AdConsentManager {

    sealed interface ConsentUiState {
        /** Waiting for [requestConsentInfoUpdate] / form (ads not loaded yet). */
        data object Pending : ConsentUiState

        /** [ConsentInformation.canRequestAds] is true — banner may load. */
        data object CanRequestAds : ConsentUiState

        /** Consent does not allow ad requests (lists and offline flows still work). */
        data object AdsNotPermitted : ConsentUiState
    }

    enum class PrivacyOptionsResult {
        Shown,
        NotAvailable,
        Error,
    }

    private val _state = MutableStateFlow<ConsentUiState>(ConsentUiState.Pending)
    val state: StateFlow<ConsentUiState> = _state.asStateFlow()

    private var consentInformation: ConsentInformation? = null
    private var mobileAdsInitialized = false
    private var consentGatheringStarted = false

    fun start(activity: Activity) {
        if (consentGatheringStarted) return
        consentGatheringStarted = true

        val info = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation = info

        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        info.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    finishConsentGathering(activity)
                }
            },
            {
                finishConsentGathering(activity)
            }
        )
    }

    fun isPrivacyOptionsRequired(): Boolean {
        val info = consentInformation ?: return false
        return info.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    fun showPrivacyOptions(activity: Activity, onResult: (PrivacyOptionsResult) -> Unit) {
        val info = consentInformation
        if (info == null) {
            onResult(PrivacyOptionsResult.NotAvailable)
            return
        }
        if (info.privacyOptionsRequirementStatus !=
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        ) {
            onResult(PrivacyOptionsResult.NotAvailable)
            return
        }
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError: FormError? ->
            if (formError != null) {
                onResult(PrivacyOptionsResult.Error)
            } else {
                finishConsentGathering(activity)
                onResult(PrivacyOptionsResult.Shown)
            }
        }
    }

    fun buildAdRequest(): AdRequest = AdRequest.Builder().build()

    private fun finishConsentGathering(activity: Activity) {
        val info = consentInformation ?: run {
            _state.value = ConsentUiState.AdsNotPermitted
            return
        }
        if (info.canRequestAds()) {
            _state.value = ConsentUiState.CanRequestAds
            initializeMobileAdsIfNeeded(activity)
        } else {
            _state.value = ConsentUiState.AdsNotPermitted
        }
    }

    private fun initializeMobileAdsIfNeeded(activity: Activity) {
        if (mobileAdsInitialized) return
        mobileAdsInitialized = true
        MobileAds.initialize(activity) {}
    }

}
