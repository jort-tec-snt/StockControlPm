package com.jort.stockcontrolpm.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jort.stockcontrolpm.data.repository.ApiInfoRepository
import com.jort.stockcontrolpm.data.repository.MovementRepository
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.ui.screens.apiinfo.ApiInfoScreen
import com.jort.stockcontrolpm.ui.screens.apiinfo.ApiInfoViewModel
import com.jort.stockcontrolpm.ui.screens.apiinfo.ApiInfoViewModelFactory
import com.jort.stockcontrolpm.ui.screens.login.LoginScreen
import com.jort.stockcontrolpm.ui.screens.login.LoginViewModel
import com.jort.stockcontrolpm.ui.screens.login.LoginViewModelFactory
import com.jort.stockcontrolpm.ui.screens.dashboard.DashboardScreen
import com.jort.stockcontrolpm.ui.screens.dashboard.DashboardViewModel
import com.jort.stockcontrolpm.ui.screens.dashboard.DashboardViewModelFactory
import com.jort.stockcontrolpm.ui.screens.products.ProductDetailScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductDetailViewModel
import com.jort.stockcontrolpm.ui.screens.products.ProductDetailViewModelFactory
import com.jort.stockcontrolpm.ui.screens.products.ProductFormScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductFormViewModel
import com.jort.stockcontrolpm.ui.screens.products.ProductFormViewModelFactory
import com.jort.stockcontrolpm.ui.screens.products.ProductListScreen
import com.jort.stockcontrolpm.ui.screens.products.ProductListViewModel
import com.jort.stockcontrolpm.ui.screens.products.ProductListViewModelFactory
import com.jort.stockcontrolpm.ui.theme.Bg
import com.jort.stockcontrolpm.ui.theme.Divider
import com.jort.stockcontrolpm.ui.theme.Primary
import com.jort.stockcontrolpm.ui.theme.PrimaryLight
import com.jort.stockcontrolpm.ui.theme.Surface
import com.jort.stockcontrolpm.ui.theme.TextMuted
import com.jort.stockcontrolpm.ui.theme.TextSecondary

// Los repositorios se reciben como parámetros: AppNavigation no construye
// ni conoce la base de datos ni el cliente de red. Esa responsabilidad
// pertenece a MainActivity, que es un componente Android, no una vista.
@Composable
fun AppNavigation(
    productRepository: ProductRepository,
    movementRepository: MovementRepository,
    apiInfoRepository: ApiInfoRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Rutas que muestran el BottomNav (Login y pantallas de detalle lo ocultan)
    val bottomNavRoutes = setOf(
        AppRoutes.DASHBOARD,
        AppRoutes.INVENTORY,
        AppRoutes.ALERTS,
        AppRoutes.PROFILE
    )

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            // El BottomNav solo aparece en las 4 pantallas tab
            if (currentRoute in bottomNavRoutes) {
                StockBottomNav(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.LOGIN,
            modifier = modifier.padding(innerPadding)
        ) {
            // ── Login ────────────────────────────────────────────────────────
            composable(AppRoutes.LOGIN) { backStackEntry ->
                val vm = remember(backStackEntry) {
                    ViewModelProvider(
                        backStackEntry,
                        LoginViewModelFactory()
                    )[LoginViewModel::class.java]
                }
                val uiState by vm.uiState.collectAsState()
                LoginScreen(
                    uiState                  = uiState,
                    onEmailChange            = vm::onEmailChange,
                    onPasswordChange         = vm::onPasswordChange,
                    onRoleChange             = vm::onRoleChange,
                    onTogglePasswordVisibility = vm::onTogglePasswordVisibility,
                    onLoginClick             = vm::login,
                    onLoginSuccess           = {
                        // Navega al dashboard y elimina Login del backstack
                        navController.navigate(AppRoutes.DASHBOARD) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            // ── Dashboard ────────────────────────────────────────────────────
            composable(AppRoutes.DASHBOARD) { backStackEntry ->
                val vm = remember(backStackEntry, productRepository) {
                    ViewModelProvider(
                        backStackEntry,
                        DashboardViewModelFactory(productRepository)
                    )[DashboardViewModel::class.java]
                }
                val uiState by vm.uiState.collectAsState()
                DashboardScreen(
                    uiState    = uiState,
                    onClearError       = vm::clearError,
                    onProductsClick    = { navController.navigate(AppRoutes.INVENTORY) },
                    onApiInfoClick     = { navController.navigate(AppRoutes.API_INFO) }
                )
            }

            // ── Inventario (lista de productos) ──────────────────────────────
            composable(AppRoutes.INVENTORY) { backStackEntry ->
                val vm = remember(backStackEntry, productRepository) {
                    ViewModelProvider(
                        backStackEntry,
                        ProductListViewModelFactory(productRepository)
                    )[ProductListViewModel::class.java]
                }
                val uiState by vm.uiState.collectAsState()
                ProductListScreen(
                    uiState              = uiState,
                    onCreateProductClick = { navController.navigate(AppRoutes.productForm()) },
                    onProductClick       = { id -> navController.navigate(AppRoutes.productDetail(id)) },
                    onSearchQueryChange  = vm::onSearchQueryChange,
                    onCategoryChange     = vm::onCategoryChange,
                    onClearError         = vm::clearError,
                    onDashboardClick     = { navController.navigate(AppRoutes.DASHBOARD) }
                )
            }

            // ── Detalle de producto ───────────────────────────────────────────
            composable(
                route = AppRoutes.PRODUCT_DETAIL,
                arguments = listOf(
                    navArgument(AppRoutes.PRODUCT_ID_ARGUMENT) { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong(AppRoutes.PRODUCT_ID_ARGUMENT) ?: 0L
                val vm = remember(backStackEntry, productRepository) {
                    ViewModelProvider(
                        backStackEntry,
                        ProductDetailViewModelFactory(productRepository)
                    )[ProductDetailViewModel::class.java]
                }
                val uiState by vm.uiState.collectAsState()
                LaunchedEffect(productId) { vm.loadProduct(productId) }
                LaunchedEffect(uiState.wasDeleted) {
                    if (uiState.wasDeleted) navController.popBackStack()
                }
                ProductDetailScreen(
                    productId      = productId,
                    uiState        = uiState,
                    onEditClick    = { id -> navController.navigate(AppRoutes.productForm(id)) },
                    onDeleteClick  = vm::deleteProduct,
                    onClearError   = vm::clearError,
                    onBackClick    = { navController.popBackStack() },
                    onDashboardClick = { navController.navigate(AppRoutes.DASHBOARD) }
                )
            }

            // ── Formulario crear / editar producto ───────────────────────────
            composable(
                route = AppRoutes.PRODUCT_FORM,
                arguments = listOf(
                    navArgument(AppRoutes.PRODUCT_ID_ARGUMENT) {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val rawId = backStackEntry.arguments?.getLong(AppRoutes.PRODUCT_ID_ARGUMENT) ?: -1L
                val formProductId = rawId.takeIf { it != -1L }
                val vm = remember(backStackEntry, productRepository) {
                    ViewModelProvider(
                        backStackEntry,
                        ProductFormViewModelFactory(productRepository)
                    )[ProductFormViewModel::class.java]
                }
                val uiState by vm.uiState.collectAsState()
                LaunchedEffect(formProductId) { vm.loadProduct(formProductId) }
                LaunchedEffect(uiState.wasSaved) {
                    if (uiState.wasSaved) navController.popBackStack()
                }
                ProductFormScreen(
                    uiState              = uiState,
                    onNameChange         = vm::onNameChange,
                    onCategoryChange     = vm::onCategoryChange,
                    onStockChange        = vm::onStockChange,
                    onMinStockChange     = vm::onMinStockChange,
                    onUnitPriceChange    = vm::onUnitPriceChange,
                    onExpirationDateChange = vm::onExpirationDateChange,
                    onSaveClick          = vm::saveProduct,
                    onClearError         = vm::clearError,
                    onBackClick          = { navController.popBackStack() },
                    onDashboardClick     = { navController.navigate(AppRoutes.DASHBOARD) }
                )
            }

            // ── API Info (consumo Retrofit) ───────────────────────────────────
            composable(AppRoutes.API_INFO) { backStackEntry ->
                val vm = remember(backStackEntry, apiInfoRepository) {
                    ViewModelProvider(
                        backStackEntry,
                        ApiInfoViewModelFactory(apiInfoRepository)
                    )[ApiInfoViewModel::class.java]
                }
                val uiState by vm.uiState.collectAsState()
                ApiInfoScreen(
                    uiState        = uiState,
                    onRetryClick   = vm::loadExternalProducts,
                    onClearError   = vm::clearError,
                    onDashboardClick = { navController.navigate(AppRoutes.DASHBOARD) },
                    onProductsClick  = { navController.navigate(AppRoutes.INVENTORY) }
                )
            }

            // ── Alertas (tab) ─────────────────────────────────────────────────
            // Pantalla placeholder hasta Pieza 10
            composable(AppRoutes.ALERTS) {
                AlertsPlaceholderScreen()
            }

            // ── Perfil (tab) ──────────────────────────────────────────────────
            // Pantalla placeholder hasta Pieza 11
            composable(AppRoutes.PROFILE) {
                ProfilePlaceholderScreen()
            }

            // ── POS / Caja ────────────────────────────────────────────────────
            // Pantalla placeholder hasta Pieza 9
            composable(AppRoutes.POS) {
                PosPlaceholderScreen()
            }
        }
    }
}

// ── BottomNav ─────────────────────────────────────────────────────────────────
@Composable
private fun StockBottomNav(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.DASHBOARD to Icons.Outlined.Home,
        BottomNavItem.INVENTORY to Icons.Outlined.Inventory2,
        BottomNavItem.ALERTS    to Icons.Outlined.Notifications,
        BottomNavItem.PROFILE   to Icons.Outlined.Person
    )

    NavigationBar(
        containerColor = Surface,
        tonalElevation = androidx.compose.ui.unit.Dp.Unspecified
    ) {
        items.forEach { (item, icon) ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick  = { onTabSelected(item.route) },
                icon     = { Icon(icon, contentDescription = item.label) },
                label    = { Text(item.label) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Primary,
                    selectedTextColor   = Primary,
                    indicatorColor      = PrimaryLight,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}

// ── Placeholders para piezas pendientes ──────────────────────────────────────
@Composable
private fun AlertsPlaceholderScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.then(Modifier),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Alertas — Pieza 10")
    }
}

@Composable
private fun ProfilePlaceholderScreen() {
    androidx.compose.foundation.layout.Box(
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Perfil — Pieza 11")
    }
}

@Composable
private fun PosPlaceholderScreen() {
    androidx.compose.foundation.layout.Box(
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("POS / Caja — Pieza 9")
    }
}
