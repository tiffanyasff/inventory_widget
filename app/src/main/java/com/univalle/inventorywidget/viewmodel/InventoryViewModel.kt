package com.univalle.inventorywidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventoryRepository(application)

    fun insert(product: Product) {
        viewModelScope.launch {
            repository.insert(product)
        }
    }

    fun update(product: Product) {
        viewModelScope.launch {
            repository.update(product)
        }
    }

    fun delete(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }

    fun getAll(onResult: (List<Product>) -> Unit) {
        viewModelScope.launch {
            val data = repository.getAll()
            onResult(data)
        }
    }

    fun getById(id: Int, onResult: (Product?) -> Unit) {
        viewModelScope.launch {
            val product = repository.getById(id)
            onResult(product)
        }
    }

    // ⭐ Agregado desde develop (criterio del equipo)
    fun totalProducto(price: Int, quantity: Int): Double {
        return (price * quantity).toDouble()
    }
}
