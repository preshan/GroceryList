package com.preshan.grocerylist.ads

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.preshan.grocerylist.BuildConfig
import com.preshan.grocerylist.R
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies merged AdMob string resources (debug vs release overlays).
 */
@RunWith(AndroidJUnit4::class)
class AdMobConfigTest {

    private val appIdPattern = Regex("^ca-app-pub-\\d+~\\d+$")
    private val bannerUnitPattern = Regex("^ca-app-pub-\\d+/\\d+$")

    /** Google sample publisher id for test ads. */
    private val googleTestPublisherId = "3940256099942544"

    @Test
    fun admobIds_matchExpectedAdMobFormat() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val appId = context.getString(R.string.admob_app_id)
        val bannerUnit = context.getString(R.string.admob_banner_home)

        assertTrue("App ID format: $appId", appId.matches(appIdPattern))
        assertTrue("Banner unit format: $bannerUnit", bannerUnit.matches(bannerUnitPattern))
        assertTrue(appId.startsWith("ca-app-pub-"))
        assertTrue(bannerUnit.startsWith("ca-app-pub-"))
    }

    @Test
    fun debugBuild_usesGoogleTestPublisherIds() {
        if (!BuildConfig.DEBUG) return

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val appId = context.getString(R.string.admob_app_id)
        val bannerUnit = context.getString(R.string.admob_banner_home)

        assertTrue(appId.contains(googleTestPublisherId))
        assertTrue(bannerUnit.contains(googleTestPublisherId))
    }

    @Test
    fun releaseBuild_usesProductionPublisherIds() {
        if (BuildConfig.DEBUG) return

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val appId = context.getString(R.string.admob_app_id)
        val bannerUnit = context.getString(R.string.admob_banner_home)

        assertTrue(appId.contains("9800341818444573"))
        assertTrue(bannerUnit.contains("9800341818444573"))
        assertTrue(!appId.contains(googleTestPublisherId))
    }
}
