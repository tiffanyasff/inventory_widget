package com.univalle.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.model.Product
import com.univalle.inventorywidget.repository.ListItemRepository
import kotlinx.coroutines.launch

class ListItemViewModel : ViewModel() {

    private val repository = ListItemRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadProducts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getProducts()
                _products.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
