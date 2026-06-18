package com.jort.stockcontrolpm.ui.screens.apiinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jort.stockcontrolpm.data.repository.ApiInfoRepository

class ApiInfoViewModelFactory(
    private val repository: ApiInfoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApiInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApiInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}

