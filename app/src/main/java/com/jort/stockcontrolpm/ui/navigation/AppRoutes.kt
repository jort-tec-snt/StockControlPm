package com.jort.stockcontrolpm.ui.navigation

object AppRoutes {
    // ── Tabs del BottomNav ──────────────────────────────────────────────────
    const val DASHBOARD = "dashboard"
    const val INVENTORY = "inventory"
    const val ALERTS    = "alerts"
    const val PROFILE   = "profile"

    // ── Pantallas fuera del BottomNav ───────────────────────────────────────
    const val LOGIN    = "login"
    const val POS      = "pos"
    const val API_INFO = "apiinfo"

    // ── Producto (con argumentos) ────────────────────────────────────────────
    const val PRODUCT_ID_ARGUMENT = "productId"
    const val PRODUCT_DETAIL = "products/{$PRODUCT_ID_ARGUMENT}"
    const val PRODUCT_FORM   = "products/form?$PRODUCT_ID_ARGUMENT={$PRODUCT_ID_ARGUMENT}"

    fun productDetail(productId: Long): String = "products/$productId"
    fun productForm(productId: Long? = null): String =
        if (productId == null) "products/form"
        else "products/form?$PRODUCT_ID_ARGUMENT=$productId"

    // Alias para no romper referencias existentes
    const val PRODUCTS = INVENTORY
}

// Ítems del BottomNav en el orden visual correcto
enum class BottomNavItem(
    val route: String,
    val label: String
) {
    DASHBOARD("dashboard", "Inicio"),
    INVENTORY("inventory", "Inventario"),
    ALERTS   ("alerts",    "Alertas"),
    PROFILE  ("profile",   "Perfil")
}
