package com.univalle.inventorywidget.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.model.Product
import kotlinx.coroutines.tasks.await

class ListItemRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "Articulos"

    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = db.collection(collectionName).get().await()
            snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
