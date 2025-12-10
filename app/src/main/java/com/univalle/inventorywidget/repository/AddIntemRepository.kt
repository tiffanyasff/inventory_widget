package com.univalle.inventorywidget.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.model.Product
import kotlinx.coroutines.tasks.await

class AddItemRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "Articulos"

    suspend fun saveProduct(product: Product): Boolean {
        return try {
            db.collection(collectionName)
                .document(product.productCode.toString())
                .set(product)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}