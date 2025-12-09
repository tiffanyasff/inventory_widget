package com.univalle.inventorywidget.repository

import com.univalle.inventorywidget.data.InventoryDao
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.model.Product
import com.univalle.inventorywidget.webservice.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val inventoryDao: InventoryDao,
    private val apiService: ApiService
) {

    suspend fun saveInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            inventoryDao.saveInventory(inventory)
        }
    }

    suspend fun getListInventory(): MutableList<Inventory> {
        return withContext(Dispatchers.IO) {
            inventoryDao.getListInventory()
        }
    }

    suspend fun deleteInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            inventoryDao.deleteInventory(inventory)
        }
    }

    suspend fun updateRepositoy(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            inventoryDao.updateInventory(inventory)
        }
    }

    suspend fun getProducts(): MutableList<Product> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getProducts()
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
        }
    }
}