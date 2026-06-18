package com.jort.stockcontrolpm.ui.navigation

object AppRoutes {
    const val DASHBOARD = "dashboard"
    const val PRODUCTS = "products"
    const val API_INFO = "apiinfo"

    const val PRODUCT_ID_ARGUMENT = "productId"
    const val PRODUCT_DETAIL = "products/{$PRODUCT_ID_ARGUMENT}"
    const val PRODUCT_FORM = "products/form?$PRODUCT_ID_ARGUMENT={$PRODUCT_ID_ARGUMENT}"

    fun productDetail(productId: Long): String = "products/$productId"

    fun productForm(productId: Long? = null): String {
        return if (productId == null) {
            "products/form"
        } else {
            "products/form?$PRODUCT_ID_ARGUMENT=$productId"
        }
    }
}

