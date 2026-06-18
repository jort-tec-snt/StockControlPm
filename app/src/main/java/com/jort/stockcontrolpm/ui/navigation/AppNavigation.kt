package com.jort.stockcontrolpm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jort.stockcontrolpm.data.local.database.AppDatabase
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.ui.screens.apiinfo.ApiInfoScreen
import com.jort.stockcontrolpm.ui.screens.dashboard.DashboardScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductDetailScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductDetailViewModel
import com.jort.stockcontrolpm.ui.screens.products.ProductDetailViewModelFactory
import com.jort.stockcontrolpm.ui.screens.products.ProductFormScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductFormViewModel
import com.jort.stockcontrolpm.ui.screens.products.ProductFormViewModelFactory
import com.jort.stockcontrolpm.ui.screens.products.ProductListScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductListViewModel
import com.jort.stockcontrolpm.ui.screens.products.ProductListViewModelFactory

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = remember(context) { AppDatabase.getInstance(context) }
    val productRepository = remember(database) { ProductRepository(database.productDao()) }

    fun navigateToDashboard() {
        navController.navigate(AppRoutes.DASHBOARD) {
            popUpTo(AppRoutes.DASHBOARD) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.DASHBOARD,
        modifier = modifier
    ) {
        composable(AppRoutes.DASHBOARD) {
            DashboardScreen(
                onProductsClick = { navController.navigate(AppRoutes.PRODUCTS) },
                onApiInfoClick = { navController.navigate(AppRoutes.API_INFO) }
            )
        }

        composable(AppRoutes.PRODUCTS) { backStackEntry ->
            val listViewModel = remember(backStackEntry, productRepository) {
                ViewModelProvider(
                    backStackEntry,
                    ProductListViewModelFactory(productRepository)
                )[ProductListViewModel::class.java]
            }
            val uiState by listViewModel.uiState.collectAsState()

            ProductListScreen(
                uiState = uiState,
                onCreateProductClick = { navController.navigate(AppRoutes.productForm()) },
                onProductClick = { productId -> navController.navigate(AppRoutes.productDetail(productId)) },
                onSearchQueryChange = listViewModel::onSearchQueryChange,
                onClearError = listViewModel::clearError,
                onDashboardClick = { navigateToDashboard() }
            )
        }

        composable(
            route = AppRoutes.PRODUCT_DETAIL,
            arguments = listOf(
                navArgument(AppRoutes.PRODUCT_ID_ARGUMENT) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong(AppRoutes.PRODUCT_ID_ARGUMENT) ?: 0L
            val detailViewModel = remember(backStackEntry, productRepository) {
                ViewModelProvider(
                    backStackEntry,
                    ProductDetailViewModelFactory(productRepository)
                )[ProductDetailViewModel::class.java]
            }
            val uiState by detailViewModel.uiState.collectAsState()

            LaunchedEffect(productId) {
                detailViewModel.loadProduct(productId)
            }
            LaunchedEffect(uiState.wasDeleted) {
                if (uiState.wasDeleted) {
                    navController.popBackStack()
                }
            }

            ProductDetailScreen(
                productId = productId,
                uiState = uiState,
                onEditClick = { selectedProductId ->
                    navController.navigate(AppRoutes.productForm(selectedProductId))
                },
                onDeleteClick = detailViewModel::deleteProduct,
                onClearError = detailViewModel::clearError,
                onBackClick = { navController.popBackStack() },
                onDashboardClick = { navigateToDashboard() }
            )
        }

        composable(
            route = AppRoutes.PRODUCT_FORM,
            arguments = listOf(
                navArgument(AppRoutes.PRODUCT_ID_ARGUMENT) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong(AppRoutes.PRODUCT_ID_ARGUMENT) ?: -1L
            val formProductId = productId.takeIf { it != -1L }
            val formViewModel = remember(backStackEntry, productRepository) {
                ViewModelProvider(
                    backStackEntry,
                    ProductFormViewModelFactory(productRepository)
                )[ProductFormViewModel::class.java]
            }
            val uiState by formViewModel.uiState.collectAsState()

            LaunchedEffect(formProductId) {
                formViewModel.loadProduct(formProductId)
            }
            LaunchedEffect(uiState.wasSaved) {
                if (uiState.wasSaved) {
                    navController.popBackStack()
                }
            }

            ProductFormScreen(
                uiState = uiState,
                onNameChange = formViewModel::onNameChange,
                onCategoryChange = formViewModel::onCategoryChange,
                onStockChange = formViewModel::onStockChange,
                onMinStockChange = formViewModel::onMinStockChange,
                onUnitPriceChange = formViewModel::onUnitPriceChange,
                onExpirationDateChange = formViewModel::onExpirationDateChange,
                onSaveClick = formViewModel::saveProduct,
                onClearError = formViewModel::clearError,
                onBackClick = { navController.popBackStack() },
                onDashboardClick = { navigateToDashboard() }
            )
        }

        composable(AppRoutes.API_INFO) {
            ApiInfoScreen(
                onDashboardClick = { navigateToDashboard() },
                onProductsClick = { navController.navigate(AppRoutes.PRODUCTS) }
            )
        }
    }
}
