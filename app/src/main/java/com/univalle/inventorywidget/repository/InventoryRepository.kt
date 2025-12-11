package com.univalle.inventorywidget.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.data.InventoryDB
import com.univalle.inventorywidget.data.InventoryDao
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class InventoryRepository(val context: Context){
    private var inventoryDao: InventoryDao = InventoryDB.getDatabase(context).inventoryDao()

    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()
    // Confirmado según tu AddItemRepository
    private val collectionName = "Articulos"

    suspend fun saveInventory(inventory: Inventory) = withContext(Dispatchers.IO){
        inventoryDao.saveInventory(inventory)
    }

    suspend fun getListInventory(): MutableList<Inventory> = withContext(Dispatchers.IO){
        inventoryDao.getListInventory()
    }

    suspend fun deleteInventory(inventory: Inventory) = withContext(Dispatchers.IO){
        inventoryDao.deleteInventory(inventory)
    }

    suspend fun updateRepositoy(inventory: Inventory) = withContext(Dispatchers.IO){
        inventoryDao.updateInventory(inventory)
    }


    suspend fun getProducts(): MutableList<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = db.collection(collectionName).get().await()

                val productList = snapshot.documents.map { document ->

                    val product = document.toObject(Product::class.java)!!

                    product.id = document.id

                    product
                }.toMutableList()

                productList

            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
        }
    }


    suspend fun deleteProduct(productId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("InventoryRepo", "Intentando eliminar producto con ID: $productId")

                if (productId.isEmpty()) {
                    Log.e("InventoryRepo", "Error: El ID del producto está vacío.")
                    return@withContext false
                }

                db.collection(collectionName)
                    .document(productId)
                    .delete()
                    .await()

                Log.d("InventoryRepo", "Eliminación exitosa en Firestore")
                true
            } catch (e: Exception) {
                Log.e("InventoryRepo", "Error eliminando: ${e.message}")
                e.printStackTrace()
                false
            }
        }
    }
}
