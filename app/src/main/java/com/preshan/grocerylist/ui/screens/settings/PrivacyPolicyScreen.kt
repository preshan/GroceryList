package com.preshan.grocerylist.ui.screens.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.preshan.grocerylist.ui.components.AppSectionCard
import com.preshan.grocerylist.ui.theme.GroceryListTheme
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider
import com.preshan.grocerylist.util.LegalUrls

private const val DeveloperName = "Preshan Pradeepa Kariyawasam"
private const val DeveloperEmail = "preshanpradeepa@gmail.com"
private const val DeveloperPhone = "+94774424778"
private const val DeveloperCountry = "Sri Lanka"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = AppTextProvider.LocalAppLanguage.current
    val versionName = remember {
        try {
            val pm = context.packageManager
            val pkg = context.packageName
            if (Build.VERSION.SDK_INT >= 33) {
                pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0)).versionName
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(pkg, 0).versionName
            }
        } catch (_: PackageManager.NameNotFoundException) {
            null
        } ?: "—"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(AppTextProvider.getText(AppTextKey.PRIVACY_TITLE, lang)) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(AppTextProvider.getText(AppTextKey.BACK, lang))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = AppTextProvider.getText(AppTextKey.PRIVACY_INTRO, lang),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                AppSectionCard(title = AppTextProvider.getText(AppTextKey.PRIVACY_SECTION_HANDLED, lang)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_LOCAL_DATA, lang))
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_NO_LOGIN, lang))
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_NO_UPLOAD, lang))
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_NO_ANALYTICS, lang))
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_ADS_FUTURE, lang))
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_ADS_DATA, lang))
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_ADS_CONSENT, lang))
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_MANUAL_SHARE, lang))
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_NO_AUTO_SHARE, lang))
                        PolicyBullet(AppTextProvider.getText(AppTextKey.PRIVACY_MANUAL_IMPORT_EXPORT, lang))
                    }
                }
            }

            item {
                AppSectionCard(title = AppTextProvider.getText(AppTextKey.PRIVACY_POLICY, lang)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AboutLinkRow(
                            label = AppTextProvider.getText(AppTextKey.PRIVACY_VIEW_ONLINE, lang),
                            value = LegalUrls.PRIVACY_POLICY,
                            onClick = { openLegalUrl(context, LegalUrls.PRIVACY_POLICY, lang) }
                        )
                        AboutLinkRow(
                            label = AppTextProvider.getText(AppTextKey.TERMS_VIEW_ONLINE, lang),
                            value = LegalUrls.TERMS_OF_USE,
                            onClick = { openLegalUrl(context, LegalUrls.TERMS_OF_USE, lang) }
                        )
                    }
                }
            }

            item {
                AppSectionCard(title = AppTextProvider.getText(AppTextKey.DEVELOPER_APP_TITLE, lang)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = DeveloperName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = DeveloperCountry,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        AboutLinkRow(
                            label = AppTextProvider.getText(AppTextKey.EMAIL, lang),
                            value = DeveloperEmail,
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:$DeveloperEmail")
                                }
                                val chooserTitle = AppTextProvider.getText(AppTextKey.SEND_EMAIL, lang)
                                runCatching { context.startActivity(Intent.createChooser(intent, chooserTitle)) }
                            }
                        )
                        AboutLinkRow(
                            label = AppTextProvider.getText(AppTextKey.PHONE, lang),
                            value = DeveloperPhone,
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${Uri.encode(DeveloperPhone)}")
                                }
                                runCatching { context.startActivity(intent) }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = AppTextProvider.getText(AppTextKey.APP_VERSION, lang),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = versionName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun openLegalUrl(
    context: android.content.Context,
    url: String,
    lang: com.preshan.grocerylist.util.AppLanguage
) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    val chooserTitle = AppTextProvider.getText(AppTextKey.OPEN_IN_BROWSER, lang)
    runCatching { context.startActivity(Intent.createChooser(intent, chooserTitle)) }
}

@Composable
private fun PolicyBullet(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 1.dp, end = 10.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AboutLinkRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun PrivacyPolicyScreenPreviewLight() {
    GroceryListTheme(darkTheme = false) {
        PrivacyPolicyScreen(onBackClick = {})
    }
}
