package com.jort.stockcontrolpm.ui.screens.products

import com.jort.stockcontrolpm.domain.model.Product

data class ProductListUiState(
    val isLoading: Boolean = false,
    val allProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null
) {
    // Filtrado por categoría y búsqueda aplicados en memoria
    val products: List<Product>
        get() = allProducts
            .let { list -> if (selectedCategory != null) list.filter { it.category == selectedCategory } else list }
            .let { list -> if (searchQuery.isBlank()) list else list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }}

    // Categorías presentes en el inventario actual (para chips dinámicos)
    val availableCategories: List<String>
        get() = allProducts.map { it.category }.distinct().sorted()

    val isEmpty: Boolean
        get() = !isLoading && products.isEmpty() && errorMessage == null
}
