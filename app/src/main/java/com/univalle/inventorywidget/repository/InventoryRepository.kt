package com.univalle.inventorywidget.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.data.InventoryDao
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val inventoryDao: InventoryDao,
    private val db: FirebaseFirestore
) {

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
                    product.id = document.id // Asignar ID
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
                if (productId.isEmpty()) return@withContext false

                db.collection(collectionName)
                    .document(productId)
                    .delete()
                    .await()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    // PARA ACTUALIZAR EN FIRESTORE
    suspend fun updateProduct(product: Product): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (product.id.isEmpty()) {
                    Log.e("InventoryRepo", "No se puede actualizar: ID vacío")
                    return@withContext false
                }

                // Creamos un mapa con los datos a actualizar
                val updates = hashMapOf<String, Any>(
                    "name" to product.name,
                    "price" to product.price,
                    "quantity" to product.quantity,
                    "productCode" to product.productCode
                )

                db.collection(collectionName)
                    .document(product.id)
                    .update(updates) // update modifica solo los campos, set sobrescribe todo
                    .await()

                Log.d("InventoryRepo", "Producto actualizado correctamente")
                true
            } catch (e: Exception) {
                Log.e("InventoryRepo", "Error al actualizar: ${e.message}")
                e.printStackTrace()
                false
            }
        }
    }
}