package com.univalle.inventorywidget.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.model.Product
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AddItemRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    private val collectionName = "Articulos"

    suspend fun saveProduct(product: Product): Boolean {
        return try {
            // CORRECCIÓN: Usamos .add() en lugar de .document().set()
            // .add() crea un ID único automático (ej: "Xy7z9...") para que no se sobrescriban.

            db.collection(collectionName)
                .add(product)
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}