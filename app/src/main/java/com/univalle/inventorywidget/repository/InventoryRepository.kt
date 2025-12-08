package com.univalle.inventorywidget.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.model.Product
import com.univalle.inventorywidget.webservice.ApiService
import com.univalle.inventorywidget.webservice.ApiUtils
import kotlinx.coroutines.tasks.await

class InventoryRepository(context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val apiService: ApiService = ApiUtils.getApiService()

    private fun inventoryRef() =
        firestore.collection("users")
            .document(auth.currentUser?.uid ?: "no-user")
            .collection("inventory")

    // -----------------------------
    //       CRUD FIRESTORE
    // -----------------------------

    suspend fun saveInventory(inventory: Inventory) {
        val id = inventory.id ?: inventoryRef().document().id
        inventory.id = id

        inventoryRef()
            .document(id)
            .set(inventory)
            .await()
    }

    suspend fun getListInventory(): MutableList<Inventory> {
        val snapshot = inventoryRef()
            .get()
            .await()

        return snapshot.toObjects(Inventory::class.java)
            .toMutableList()
    }

    suspend fun deleteInventory(inventory: Inventory) {
        inventory.id?.let {
            inventoryRef()
                .document(it)
                .delete()
                .await()
        }
    }

    suspend fun updateRepository(inventory: Inventory) {
        inventory.id?.let {
            inventoryRef()
                .document(it)
                .set(inventory)
                .await()
        }
    }


    suspend fun getProducts(): MutableList<Product> {
        return try {
            apiService.getProducts()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }
}
