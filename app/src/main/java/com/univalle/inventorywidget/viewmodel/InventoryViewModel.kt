package com.univalle.inventorywidget.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.model.Product
import com.univalle.inventorywidget.repository.InventoryRepository
import com.univalle.inventorywidget.repository.LoginRepository
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    val context = getApplication<Application>()

    // Instancia del repositorio unificado
    private val inventoryRepository = InventoryRepository(context)

    // LiveData para Inventario (Room / Local)
    private val _listInventory = MutableLiveData<MutableList<Inventory>>()
    val listInventory: LiveData<MutableList<Inventory>> get() = _listInventory

    // Estado de carga general
    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> = _progresState

    // LiveData para navegación del Login
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

    // LiveData para saber si la EDICIÓN fue exitosa
    private val _editProductStatus = MutableLiveData<Boolean>()
    val editProductStatus: LiveData<Boolean> get() = _editProductStatus

    private val loginRepository = LoginRepository(context)

    fun cerrarSesion() {
        viewModelScope.launch {
            try {
                loginRepository.signOut()
                _navigateToLogin.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onLoginNavigationComplete() {
        _navigateToLogin.value = false
    }

    // LiveData para Productos (Firestore / Nube)
    private val _listProducts = MutableLiveData<MutableList<Product>>()
    val listProducts: LiveData<MutableList<Product>> = _listProducts


    // --- FUNCIONES LOCALES (ROOM) ---

    fun saveInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                inventoryRepository.saveInventory(inventory)
                _progresState.value = false
            } catch (e: Exception) {
                _progresState.value = false
            }
        }
    }

    fun getListInventory() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                _listInventory.value = inventoryRepository.getListInventory()
                _progresState.value = false
            } catch (e: Exception) {
                _progresState.value = false
            }
        }
    }

    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                inventoryRepository.deleteInventory(inventory)
                _progresState.value = false
            } catch (e: Exception) {
                _progresState.value = false
            }
        }
    }

    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                inventoryRepository.updateRepositoy(inventory)
                _progresState.value = false
            } catch (e: Exception) {
                _progresState.value = false
            }
        }
    }

    // --- FUNCIONES NUBE (FIRESTORE) ---

    fun getProducts() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                _listProducts.value = inventoryRepository.getProducts()
                _progresState.value = false
            } catch (e: Exception) {
                Log.e("ViewModel", "Error al obtener productos: ${e.message}")
                _progresState.value = false
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _progresState.value = true

            if (product.id.isNullOrEmpty()) {
                Log.e("ViewModel", "ERROR CRÍTICO: El ID del producto es null o vacío.")
                _progresState.value = false
                return@launch
            }

            try {
                Log.d("ViewModel", "Solicitando eliminar producto con ID: ${product.id}")
                val deleted = inventoryRepository.deleteProduct(product.id)

                if (deleted) {
                    Log.d("ViewModel", "Eliminación exitosa. Recargando lista...")
                    getProducts()
                } else {
                    _progresState.value = false
                }

            } catch (e: Exception) {
                _progresState.value = false
            }
        }
    }


    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _progresState.value = true // Cargando...
            try {
                val success = inventoryRepository.updateProduct(product)
                if (success) {

                    getProducts()
                    _editProductStatus.value = true
                } else {
                    _editProductStatus.value = false
                }
            } catch (e: Exception) {
                _editProductStatus.value = false
            } finally {
                _progresState.value = false
            }
        }
    }

    // Función auxiliar para resetear el estado de edición (evita que se dispare solo)
    fun resetEditStatus() {
        _editProductStatus.value = false
    }

    fun totalProducto(price: Int, quantity: Int): Double {
        return (price * quantity).toDouble()
    }
}
