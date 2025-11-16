package com.univalle.inventorywidget.repository

import android.content.Context
import com.univalle.inventorywidget.data.InventoryDB
import com.univalle.inventorywidget.model.Product

class InventoryRepository(context: Context) {

    private val inventoryDao = InventoryDB.getDatabase(context).inventoryDao()

    // Obtener todos los productos
    suspend fun getAllProducts(): List<Product> {
        return
        inventoryDao.getAllInventory()
    }

    // Insertar producto
    suspend fun insertProduct(product: Product) {
        inventoryDao.insert(product)
    }

    // Actualizar producto
    suspend fun updateProduct(product: Product) {
        inventoryDao.update(product)
    }

    // Eliminar producto
    suspend fun deleteProduct(product: Product) {
        inventoryDao.delete(product)
    }

    // Obtener producto por ID
    suspend fun getProductById(id: Int): Product? {
        return inventoryDao.getProductById(id)
    }
}
