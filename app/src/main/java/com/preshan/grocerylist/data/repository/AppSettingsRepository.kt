package com.preshan.grocerylist.data.repository

import com.preshan.grocerylist.data.AppSettingKeys
import com.preshan.grocerylist.data.locale.CountryLanguageDefaults
import com.preshan.grocerylist.data.local.dao.AppSettingDao
import com.preshan.grocerylist.data.local.entity.AppSettingEntity
import com.preshan.grocerylist.data.seed.DefaultSeedData

class AppSettingsRepository(
    private val appSettingDao: AppSettingDao
) {
    suspend fun getSetting(key: String): AppSettingEntity? = appSettingDao.getByKey(key)

    /**
     * Existing installs that already ran v4 seed but have no onboarding keys are treated as completed
     * with default International / English metadata (user keeps existing catalogue data).
     */
    suspend fun migrateLegacyInstallIfNeeded() {
        if (getSetting(AppSettingKeys.FIRST_LAUNCH_COMPLETED) != null) return
        val seeded =
            getSetting(DefaultSeedData.SEED_VERSION_KEY)?.value == DefaultSeedData.SEED_VERSION_VALUE
        if (seeded) {
            upsertSetting(AppSettingKeys.FIRST_LAUNCH_COMPLETED, "true")
            upsertSetting(
                AppSettingKeys.SELECTED_COUNTRY_REGION,
                CountryLanguageDefaults.DEFAULT_COUNTRY_REGION
            )
            upsertSetting(
                AppSettingKeys.SELECTED_LANGUAGE,
                CountryLanguageDefaults.DEFAULT_LANGUAGE
            )
        }
    }

    suspend fun upsertSetting(key: String, value: String?) {
        appSettingDao.upsert(
            AppSettingEntity(
                key = key,
                value = value,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
