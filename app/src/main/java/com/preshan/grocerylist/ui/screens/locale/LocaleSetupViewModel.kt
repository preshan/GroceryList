package com.preshan.grocerylist.ui.screens.locale

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.preshan.grocerylist.data.AppSettingKeys
import com.preshan.grocerylist.data.locale.CountryLanguageDefaults
import com.preshan.grocerylist.data.local.database.DatabaseProvider
import com.preshan.grocerylist.data.repository.AppSettingsRepository
import kotlinx.coroutines.launch

class LocaleSetupViewModel(application: Application) : AndroidViewModel(application) {

    private val appSettingsRepository =
        AppSettingsRepository(DatabaseProvider.getDatabase(application).appSettingDao())

    var countryRegion by mutableStateOf(CountryLanguageDefaults.DEFAULT_COUNTRY_REGION)
        private set

    var language by mutableStateOf(CountryLanguageDefaults.DEFAULT_LANGUAGE)
        private set

    fun reloadFromSettings() {
        viewModelScope.launch {
            countryRegion =
                appSettingsRepository.getSetting(AppSettingKeys.SELECTED_COUNTRY_REGION)?.value
                    ?: CountryLanguageDefaults.DEFAULT_COUNTRY_REGION
            language =
                appSettingsRepository.getSetting(AppSettingKeys.SELECTED_LANGUAGE)?.value
                    ?: CountryLanguageDefaults.DEFAULT_LANGUAGE
        }
    }

    fun onCountryRegionSelected(value: String) {
        countryRegion = value
        language = CountryLanguageDefaults.defaultLanguageForCountry(value)
    }

    fun onLanguageSelected(value: String) {
        language = value
    }

    fun saveAndCompleteFirstLaunch(onSaved: () -> Unit) {
        viewModelScope.launch {
            persist(completedFirstLaunch = true)
            onSaved()
        }
    }

    fun saveEdits(onSaved: () -> Unit) {
        viewModelScope.launch {
            persist(completedFirstLaunch = false)
            onSaved()
        }
    }

    private suspend fun persist(completedFirstLaunch: Boolean) {
        appSettingsRepository.upsertSetting(AppSettingKeys.SELECTED_COUNTRY_REGION, countryRegion)
        appSettingsRepository.upsertSetting(AppSettingKeys.SELECTED_LANGUAGE, language)
        if (completedFirstLaunch) {
            appSettingsRepository.upsertSetting(AppSettingKeys.FIRST_LAUNCH_COMPLETED, "true")
        }
    }
}
