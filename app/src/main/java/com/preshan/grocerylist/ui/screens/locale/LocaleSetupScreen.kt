package com.preshan.grocerylist.ui.screens.locale

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.preshan.grocerylist.data.locale.CountryLanguageDefaults
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocaleSetupScreen(
    fromSettings: Boolean,
    viewModel: LocaleSetupViewModel,
    onContinue: () -> Unit,
    onBackFromSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = AppTextProvider.LocalAppLanguage.current
    LaunchedEffect(fromSettings) {
        if (fromSettings) {
            viewModel.reloadFromSettings()
        }
    }

    if (!fromSettings) {
        BackHandler { /* first-launch onboarding is mandatory */ }
    }

    var countryMenuExpanded by remember { mutableStateOf(false) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val cardShape = RoundedCornerShape(22.dp)
    val countryDisplay = when (viewModel.countryRegion) {
        "International" -> AppTextProvider.getText(AppTextKey.INTERNATIONAL, lang)
        else -> viewModel.countryRegion
    }
    val languageDisplay = when (viewModel.language) {
        "English" -> AppTextProvider.getText(AppTextKey.ENGLISH, lang)
        "Sinhala" -> AppTextProvider.getText(AppTextKey.SINHALA, lang)
        "Tamil" -> AppTextProvider.getText(AppTextKey.TAMIL, lang)
        "Hindi" -> AppTextProvider.getText(AppTextKey.HINDI, lang)
        "German" -> AppTextProvider.getText(AppTextKey.GERMAN, lang)
        "French" -> AppTextProvider.getText(AppTextKey.FRENCH, lang)
        "Spanish" -> AppTextProvider.getText(AppTextKey.SPANISH, lang)
        "Arabic" -> AppTextProvider.getText(AppTextKey.ARABIC, lang)
        "Portuguese" -> AppTextProvider.getText(AppTextKey.PORTUGUESE, lang)
        "Indonesian" -> AppTextProvider.getText(AppTextKey.INDONESIAN, lang)
        else -> viewModel.language
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (fromSettings) {
                TopAppBar(
                    title = { Text(AppTextProvider.text(AppTextKey.COUNTRY_REGION_LANGUAGE)) },
                    navigationIcon = {
                        TextButton(onClick = onBackFromSettings) {
                            Text(AppTextProvider.text(AppTextKey.CANCEL))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            LocaleSetupIllustrationBanner()

            ElevatedCard(
                shape = cardShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = AppTextProvider.text(AppTextKey.SETUP_TITLE),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = AppTextProvider.text(AppTextKey.SETUP_SUBTITLE),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ElevatedCard(
                shape = cardShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = countryMenuExpanded,
                        onExpandedChange = { countryMenuExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            readOnly = true,
                            value = countryDisplay,
                            onValueChange = {},
                            label = { Text(AppTextProvider.text(AppTextKey.COUNTRY_REGION)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryMenuExpanded)
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = countryMenuExpanded,
                            onDismissRequest = { countryMenuExpanded = false }
                        ) {
                            CountryLanguageDefaults.countryRegions.forEach { option ->
                                val display = if (option == "International") {
                                    AppTextProvider.getText(AppTextKey.INTERNATIONAL, lang)
                                } else {
                                    option
                                }
                                DropdownMenuItem(
                                    text = { Text(display) },
                                    onClick = {
                                        viewModel.onCountryRegionSelected(option)
                                        countryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = languageMenuExpanded,
                        onExpandedChange = { languageMenuExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            readOnly = true,
                            value = languageDisplay,
                            onValueChange = {},
                            label = { Text(AppTextProvider.text(AppTextKey.LANGUAGE)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageMenuExpanded)
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = languageMenuExpanded,
                            onDismissRequest = { languageMenuExpanded = false }
                        ) {
                            CountryLanguageDefaults.languages.forEach { option ->
                                val display = when (option) {
                                    "English" -> AppTextProvider.getText(AppTextKey.ENGLISH, lang)
                                    "Sinhala" -> AppTextProvider.getText(AppTextKey.SINHALA, lang)
                                    "Tamil" -> AppTextProvider.getText(AppTextKey.TAMIL, lang)
                                    "Hindi" -> AppTextProvider.getText(AppTextKey.HINDI, lang)
                                    "German" -> AppTextProvider.getText(AppTextKey.GERMAN, lang)
                                    "French" -> AppTextProvider.getText(AppTextKey.FRENCH, lang)
                                    "Spanish" -> AppTextProvider.getText(AppTextKey.SPANISH, lang)
                                    "Arabic" -> AppTextProvider.getText(AppTextKey.ARABIC, lang)
                                    "Portuguese" -> AppTextProvider.getText(AppTextKey.PORTUGUESE, lang)
                                    "Indonesian" -> AppTextProvider.getText(AppTextKey.INDONESIAN, lang)
                                    else -> option
                                }
                                DropdownMenuItem(
                                    text = { Text(display) },
                                    onClick = {
                                        viewModel.onLanguageSelected(option)
                                        languageMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Text(
                            text = if (fromSettings) {
                                AppTextProvider.text(AppTextKey.SAVE)
                            } else {
                                AppTextProvider.text(AppTextKey.CONTINUE)
                            },
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Text(
                        text = AppTextProvider.text(AppTextKey.SETUP_HELPER),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
