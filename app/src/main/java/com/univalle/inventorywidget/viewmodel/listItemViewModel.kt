package com.univalle.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.model.Product
import com.univalle.inventorywidget.repository.InventoryRepository
import kotlinx.coroutines.launch

class ListItemViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Instanciamos el repositorio que SÍ funciona
    private val repository = InventoryRepository(application)

    private val _products = MutableLiveData<MutableList<Product>>()
    val products: LiveData<MutableList<Product>> = _products

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadProducts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                // 2. Usamos la función getProducts() del repositorio
                // Esta función TIENE la corrección 'product.id = document.id'
                val result = repository.getProducts()

                _products.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                // En caso de error, enviamos lista vacía para no romper la UI
                _products.value = mutableListOf()
            } finally {
                _loading.value = false
            }
        }
    }
}
