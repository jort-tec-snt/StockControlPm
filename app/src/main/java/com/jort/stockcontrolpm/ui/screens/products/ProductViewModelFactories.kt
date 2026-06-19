package com.jort.stockcontrolpm.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jort.stockcontrolpm.data.repository.ApiInfoRepository
import com.jort.stockcontrolpm.data.repository.ProductRepository

class ProductListViewModelFactory(
    private val repository: ProductRepository,
    private val apiInfoRepository: ApiInfoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductListViewModel(repository, apiInfoRepository) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}

class ProductDetailViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}

class ProductFormViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductFormViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}
