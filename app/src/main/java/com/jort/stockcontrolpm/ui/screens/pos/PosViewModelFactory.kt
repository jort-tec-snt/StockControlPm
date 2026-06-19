package com.jort.stockcontrolpm.ui.screens.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jort.stockcontrolpm.data.repository.ProductRepository

class PosViewModelFactory(
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PosViewModel(productRepository) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}
