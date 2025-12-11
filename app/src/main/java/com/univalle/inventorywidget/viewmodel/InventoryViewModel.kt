package com.univalle.inventorywidget.viewmodel

import android.app.Application
import android.util.Log // Importante para los Logs
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

    // Estado de carga
    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> = _progresState

    // LiveData para navegación del Login
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

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
                // Opcional: recargar la lista local aquí si es necesario
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
                // Aquí obtenemos la lista y se asignan los IDs en el repositorio
                _listProducts.value = inventoryRepository.getProducts()
                _progresState.value = false
            } catch (e: Exception) {
                Log.e("ViewModel", "Error al obtener productos: ${e.message}")
                _progresState.value = false
            }
        }
    }

    /**
     * Función corregida para eliminar producto en Firestore
     */
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _progresState.value = true // Mostramos cargando

            // 1. Verificación de ID
            if (product.id.isNullOrEmpty()) {
                Log.e("ViewModel", "ERROR CRÍTICO: El ID del producto es null o vacío. No se puede borrar.")
                _progresState.value = false
                return@launch
            }

            try {
                // 2. Llamada al repositorio (usando la variable correcta 'inventoryRepository')
                Log.d("ViewModel", "Solicitando eliminar producto con ID: ${product.id}")

                // CORRECCIÓN: Usar 'inventoryRepository' (la variable), no 'InventoryRepository' (la clase)
                val deleted = inventoryRepository.deleteProduct(product.id)

                if (deleted) {
                    Log.d("ViewModel", "Eliminación exitosa. Recargando lista...")
                    // 3. IMPORTANTE: Recargar la lista para que la UI se actualice
                    getProducts()
                } else {
                    Log.e("ViewModel", "El repositorio devolvió false. No se pudo eliminar.")
                    _progresState.value = false
                }

            } catch (e: Exception) {
                Log.e("ViewModel", "Excepción al eliminar: ${e.message}")
                _progresState.value = false
            }
            // Nota: Si getProducts() se llama, él se encargará de poner _progresState en false al terminar.
        }
    }

    fun totalProducto(price: Int, quantity: Int): Double {
        return (price * quantity).toDouble()
    }
}
