package com.jort.stockcontrolpm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jort.stockcontrolpm.ui.navigation.AppNavigation
import com.jort.stockcontrolpm.ui.theme.StockControlPmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockControlPmTheme {
                AppNavigation()
            }
        }
    }
}
