package com.jort.stockcontrolpm.ui.screens.apiinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jort.stockcontrolpm.data.repository.ApiInfoRepository
import com.jort.stockcontrolpm.data.repository.ProductRepository

class ApiInfoViewModelFactory(
    private val repository: ApiInfoRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApiInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApiInfoViewModel(repository, productRepository) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}
