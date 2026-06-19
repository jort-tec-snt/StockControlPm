package com.jort.stockcontrolpm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jort.stockcontrolpm.data.local.database.AppDatabase
import com.jort.stockcontrolpm.data.remote.client.RetrofitClient
import com.jort.stockcontrolpm.data.repository.ApiInfoRepository
import com.jort.stockcontrolpm.data.repository.MovementRepository
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.ui.navigation.AppNavigation
import com.jort.stockcontrolpm.ui.theme.StockControlPmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Los repositorios se construyen aquí, en la capa de componentes Android,
        // para que ningún Composable acceda directamente a la base de datos o red.
        val database = AppDatabase.getInstance(this)
        val productRepository  = ProductRepository(database.productDao())
        val movementRepository = MovementRepository(database.movementDao())
        val apiInfoRepository  = ApiInfoRepository(RetrofitClient.fakeStoreApiService)

        setContent {
            StockControlPmTheme {
                AppNavigation(
                    productRepository  = productRepository,
                    movementRepository = movementRepository,
                    apiInfoRepository  = apiInfoRepository
                )
            }
        }
    }
}
