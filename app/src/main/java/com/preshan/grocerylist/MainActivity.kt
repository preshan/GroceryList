package com.preshan.grocerylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.preshan.grocerylist.ads.AdConsentManager
import com.preshan.grocerylist.ui.navigation.GroceryListNavHost
import com.preshan.grocerylist.ui.theme.GroceryListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroceryListTheme {
                GroceryListNavHost()
            }
        }
        // Non-blocking: UI is shown first; ads load only after UMP allows requests.
        AdConsentManager.start(this)
    }
}

@Composable
fun GroceryListAppPreview() {
    GroceryListNavHost()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GroceryListTheme {
        GroceryListAppPreview()
    }
}