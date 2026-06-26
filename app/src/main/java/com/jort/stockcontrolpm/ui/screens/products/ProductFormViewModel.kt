package com.jort.stockcontrolpm.ui.screens.products

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.domain.model.Product
import com.jort.stockcontrolpm.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductFormViewModel(
    private val repository: ProductRepository,
    private val appContext: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductFormUiState())
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    private var createdAt: Long = 0L

    fun loadProduct(productId: Long?) {
        if (productId == null) {
            _uiState.value = ProductFormUiState()
            createdAt = 0L
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, wasSaved = false) }

            runCatching { repository.getProductById(productId) }
                .onSuccess { product ->
                    if (product == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Producto no encontrado.") }
                        return@onSuccess
                    }
                    createdAt = product.createdAt
                    _uiState.update {
                        ProductFormUiState(
                            isLoading     = false,
                            productId     = product.id,
                            name          = product.name,
                            category      = product.category,
                            stock         = product.stock.toString(),
                            minStock      = product.minStock.toString(),
                            unitPrice     = product.unitPrice.toString(),
                            expirationDate = product.expirationDate.orEmpty(),
                            sku           = product.sku,
                            supplier      = product.supplier.orEmpty(),
                            purchasePrice = product.purchasePrice?.toString().orEmpty(),
                            description   = product.description.orEmpty()
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "No se pudo cargar el producto."
                    ) }
                }
        }
    }

    fun onNameChange(value: String)           = _uiState.update { it.copy(name = value,           wasSaved = false) }
    fun onCategoryChange(value: String)        = _uiState.update { it.copy(category = value,        wasSaved = false, showCategorySheet = false) }
    fun onStockChange(value: String)           = _uiState.update { it.copy(stock = value,            wasSaved = false) }
    fun onMinStockChange(value: String)        = _uiState.update { it.copy(minStock = value,         wasSaved = false) }
    fun onUnitPriceChange(value: String)       = _uiState.update { it.copy(unitPrice = value,        wasSaved = false) }
    fun onExpirationDateChange(value: String)  = _uiState.update { it.copy(expirationDate = value,   wasSaved = false) }
    fun onSkuChange(value: String)             = _uiState.update { it.copy(sku = value,              wasSaved = false) }
    fun onSupplierChange(value: String)        = _uiState.update { it.copy(supplier = value,         wasSaved = false) }
    fun onPurchasePriceChange(value: String)   = _uiState.update { it.copy(purchasePrice = value,    wasSaved = false) }
    fun onDescriptionChange(value: String)     = _uiState.update { it.copy(description = value,      wasSaved = false) }
    fun onShowCategorySheet()                  = _uiState.update { it.copy(showCategorySheet = true) }
    fun onDismissCategorySheet()               = _uiState.update { it.copy(showCategorySheet = false) }

    fun saveProduct() {
        val state   = _uiState.value
        val stock   = state.stock.toIntOrNull()
        val minStock = state.minStock.toIntOrNull()
        val unitPrice = state.unitPrice.toDoubleOrNull()

        when {
            state.name.isBlank()           -> { error("El nombre es obligatorio."); return }
            state.category.isBlank()       -> { error("La categoría es obligatoria."); return }
            stock == null || stock < 0     -> { error("El stock debe ser un número válido."); return }
            minStock == null || minStock < 0 -> { error("El stock mínimo debe ser un número válido."); return }
            unitPrice == null || unitPrice < 0.0 -> { error("El precio de venta debe ser un número válido."); return }
        }

        val now = System.currentTimeMillis()
        val product = Product(
            id            = state.productId ?: 0L,
            name          = state.name.trim(),
            category      = state.category.trim(),
            stock         = stock!!,
            minStock      = minStock!!,
            unitPrice     = unitPrice!!,
            expirationDate = state.expirationDate.trim().ifBlank { null },
            createdAt     = createdAt.takeIf { it > 0L } ?: now,
            updatedAt     = now,
            sku           = state.sku.trim(),
            supplier      = state.supplier.trim().ifBlank { null },
            purchasePrice = state.purchasePrice.toDoubleOrNull(),
            description   = state.description.trim().ifBlank { null }
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, wasSaved = false) }
            runCatching { repository.saveProduct(product) }
                .onSuccess {
                    // Notificación local: si el stock guardado es crítico, avisar al usuario
                    if (product.stock in 1..product.minStock) {
                        NotificationHelper.showLowStockNotification(
                            context       = appContext,
                            productName   = product.name,
                            currentStock  = product.stock,
                            minStock      = product.minStock
                        )
                    }
                    _uiState.update { it.copy(isSaving = false, wasSaved = true, errorMessage = null) }
                }
                .onFailure { throwable -> _uiState.update { it.copy(
                    isSaving = false,
                    errorMessage = throwable.message ?: "No se pudo guardar el producto."
                ) } }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    private fun error(message: String) {
        _uiState.update { it.copy(errorMessage = message, wasSaved = false) }
    }
}
