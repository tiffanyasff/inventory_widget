package com.univalle.inventorywidget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.model.Product
import com.univalle.inventorywidget.repository.AddItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SaveState {
    object Idle : SaveState()
    object Loading : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val repository: AddItemRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }


    fun saveInventory(rawCode: String, rawName: String, rawPrice: String, rawQty: String) {

        if (rawCode.isEmpty() || rawName.isEmpty() || rawPrice.isEmpty() || rawQty.isEmpty()) {
            _saveState.value = SaveState.Error("Por favor llena todos los campos")
            return
        }

        try {
            val product = Product(
                productCode = rawCode.toInt(),
                name = rawName,
                price = rawPrice.toInt(),
                quantity = rawQty.toInt()
            )

            viewModelScope.launch {
                _saveState.value = SaveState.Loading


                val success = repository.saveProduct(product)

                if (success) {
                    _saveState.update { SaveState.Success }
                } else {
                    _saveState.update { SaveState.Error("Error al guardar el artículo en la base de datos.") }
                }
            }
        } catch (e: NumberFormatException) {
            _saveState.value = SaveState.Error("Asegúrate de que Código, Precio y Cantidad sean números válidos.")
        }
    }
}
