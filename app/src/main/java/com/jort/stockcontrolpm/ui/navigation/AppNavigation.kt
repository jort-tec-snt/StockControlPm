package com.jort.stockcontrolpm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jort.stockcontrolpm.ui.screens.apiinfo.ApiInfoScreen
import com.jort.stockcontrolpm.ui.screens.dashboard.DashboardScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductDetailScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductFormScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductListScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

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

        composable(AppRoutes.PRODUCTS) {
            ProductListScreen(
                onCreateProductClick = { navController.navigate(AppRoutes.productForm()) },
                onProductClick = { productId -> navController.navigate(AppRoutes.productDetail(productId)) }
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
            ProductDetailScreen(
                productId = productId,
                onEditClick = { selectedProductId ->
                    navController.navigate(AppRoutes.productForm(selectedProductId))
                },
                onBackClick = { navController.popBackStack() }
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
            ProductFormScreen(
                productId = productId.takeIf { it != -1L },
                onSaveClick = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.API_INFO) {
            ApiInfoScreen(
                onDashboardClick = { navController.navigate(AppRoutes.DASHBOARD) },
                onProductsClick = { navController.navigate(AppRoutes.PRODUCTS) }
            )
        }
    }
}
