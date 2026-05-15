package com.preshan.grocerylist.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import com.preshan.grocerylist.R
import com.preshan.grocerylist.ui.components.HomeAdMobBanner
import com.preshan.grocerylist.util.AppTextKey
import com.preshan.grocerylist.util.AppTextProvider
import com.preshan.grocerylist.ui.theme.GroceryListTheme

/** Neutral canvas like the reference mock (not strong mint). */
private val HomeLightPageBackground = Color(0xFFF3F4F2)

private val HomeShortcutFavoritesBg = Color(0xFFE8F5E9)
private val HomeShortcutFrequentBg = Color(0xFFEEF6F0)
private val HomeShortcutManageBg = Color(0xFFFFF3E0)
private val HomeShortcutSettingsBg = Color(0xFFE0F2F1)

private val HomeIconForestGreen = Color(0xFF1B5E20)
/** Darker forest green for the “Prepare Shopping List” CTA only. */
private val HomePrepareShoppingGreen = Color(0xFF0A3D14)
private val HomeManageIconBg = Color(0xFFE65100)
private val HomeSettingsIconBg = Color(0xFF00838F)

private const val ShortcutTileHeightDp = 110

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onPrepareShoppingListClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onFrequentClick: () -> Unit = {},
    onManageItemsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val dark = isSystemInDarkTheme()
    val pageBg = if (dark) MaterialTheme.colorScheme.background else HomeLightPageBackground

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = pageBg,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBg)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HomeHeaderStrip(darkTheme = dark, pageBlendColor = pageBg)

            HomeWelcomeCard(
                darkTheme = dark,
                onPrepareShoppingListClick = onPrepareShoppingListClick
            )

            HomeShortcutsHeading()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HomeShortcutCard(
                        title = AppTextProvider.text(AppTextKey.FAVORITES),
                        subtitle = AppTextProvider.text(AppTextKey.SHORTCUT_FAVORITES_SUB),
                        containerColor = if (dark) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        } else {
                            HomeShortcutFavoritesBg
                        },
                        icon = Icons.Rounded.Star,
                        iconBackground = if (dark) MaterialTheme.colorScheme.primary else HomeIconForestGreen,
                        iconTint = Color.White,
                        onClick = onFavoritesClick,
                        modifier = Modifier.weight(1f)
                    )
                    HomeShortcutCard(
                        title = AppTextProvider.text(AppTextKey.FREQUENT_ITEMS),
                        subtitle = AppTextProvider.text(AppTextKey.SHORTCUT_FREQUENT_SUB),
                        containerColor = if (dark) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
                        } else {
                            HomeShortcutFrequentBg
                        },
                        icon = Icons.Rounded.FormatListNumbered,
                        iconBackground = if (dark) MaterialTheme.colorScheme.primary else HomeIconForestGreen,
                        iconTint = Color.White,
                        onClick = onFrequentClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HomeShortcutCard(
                        title = AppTextProvider.text(AppTextKey.MANAGE_ITEMS),
                        subtitle = AppTextProvider.text(AppTextKey.SHORTCUT_MANAGE_SUB),
                        containerColor = if (dark) {
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                        } else {
                            HomeShortcutManageBg
                        },
                        icon = Icons.AutoMirrored.Rounded.FactCheck,
                        iconBackground = if (dark) MaterialTheme.colorScheme.tertiary else HomeManageIconBg,
                        iconTint = Color.White,
                        onClick = onManageItemsClick,
                        modifier = Modifier.weight(1f)
                    )
                    HomeShortcutCard(
                        title = AppTextProvider.text(AppTextKey.SETTINGS),
                        subtitle = AppTextProvider.text(AppTextKey.SHORTCUT_SETTINGS_SUB),
                        containerColor = if (dark) {
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                        } else {
                            HomeShortcutSettingsBg
                        },
                        icon = Icons.Rounded.Settings,
                        iconBackground = if (dark) MaterialTheme.colorScheme.secondary else HomeSettingsIconBg,
                        iconTint = Color.White,
                        onClick = onSettingsClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HomeAdMobBannerSection()
        }
    }
}

/** App title row only — separate from the welcome card below. */
@Composable
private fun HomeHeaderStrip(
    darkTheme: Boolean,
    pageBlendColor: Color
) {
    val shape = RoundedCornerShape(22.dp)
    val gradient = if (darkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE8F7EE),
                Color(0xFFEDF4F0),
                pageBlendColor
            )
        )
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape),
        shape = shape,
        color = Color.Transparent,
        shadowElevation = if (darkTheme) 1.dp else 2.dp,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(gradient)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                HomeHeader(darkTheme = darkTheme, trailingBagGraphic = true)
            }
        }
    }
}

@Composable
private fun HomeHeader(darkTheme: Boolean, trailingBagGraphic: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (darkTheme) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f)
            } else {
                Color(0xFFE6FBE9)
            },
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingCart,
                    contentDescription = null,
                    tint = if (darkTheme) MaterialTheme.colorScheme.primary else HomeIconForestGreen,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = AppTextProvider.text(AppTextKey.HOME_TITLE),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = AppTextProvider.text(AppTextKey.HOME_SUBTITLE),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (trailingBagGraphic) {
            Image(
                painter = painterResource(R.drawable.home_hero_bag),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .height(84.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun HomeWelcomeCard(
    darkTheme: Boolean,
    onPrepareShoppingListClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (darkTheme) 2.dp else 6.dp,
            pressedElevation = if (darkTheme) 2.dp else 5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(92.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.home_welcome_art),
                        contentDescription = AppTextProvider.text(AppTextKey.HOME_WELCOME_ILLUSTRATION_CD),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = AppTextProvider.text(AppTextKey.HOME_WELCOME_HEADING),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = AppTextProvider.text(AppTextKey.HOME_WELCOME_BODY),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Button(
                onClick = onPrepareShoppingListClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HomePrepareShoppingGreen,
                    contentColor = Color.White
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.22f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = AppTextProvider.text(AppTextKey.PREPARE_SHOPPING_LIST),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeShortcutsHeading() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = AppTextProvider.text(AppTextKey.HOME_SHORTCUTS),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .padding(top = 26.dp, start = 1.dp)
                .width(26.dp)
                .height(5.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(3.dp)
                )
        )
    }
}

@Composable
private fun HomeShortcutCard(
    title: String,
    subtitle: String,
    containerColor: Color,
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(ShortcutTileHeightDp.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = iconBackground,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
private fun HomeAdMobBannerSection() {
    HomeAdMobBanner()
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun HomeScreenPreviewLight() {
    GroceryListTheme(darkTheme = false) { HomeScreen() }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun HomeScreenPreviewDark() {
    GroceryListTheme(darkTheme = true) { HomeScreen() }
}
