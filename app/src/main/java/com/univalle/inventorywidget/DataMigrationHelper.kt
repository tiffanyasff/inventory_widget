package com.univalle.inventorywidget.migration

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.data.InventoryDB
import com.univalle.inventorywidget.model.Inventory
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DataMigrationHelper @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun migrateFromRoomToFirebase(context: Context): MigrationResult {
        return try {
            // 1. Obtener la base de datos de Room
            val roomDb = InventoryDB.getDatabase(context)
            val inventoryDao = roomDb.inventoryDao()

            // 2. Leer todos los inventarios de Room
            val roomInventories = inventoryDao.getListInventory()

            if (roomInventories.isEmpty()) {
                return MigrationResult.NoDataToMigrate
            }

            // 3. Migrar cada inventario a Firebase
            val inventoryCollection = firestore.collection("inventories")
            var successCount = 0
            var failCount = 0

            roomInventories.forEach { roomInventory ->
                try {
                    // Crear documento en Firebase con ID autogenerado
                    val docRef = inventoryCollection.document()

                    // Convertir el inventario de Room a Firebase
                    val firebaseInventory = Inventory(
                        id = docRef.id,
                        name = roomInventory.name,
                        price = roomInventory.price,
                        quantity = roomInventory.quantity
                    )

                    // Guardar en Firebase
                    docRef.set(firebaseInventory).await()
                    successCount++
                } catch (e: Exception) {
                    e.printStackTrace()
                    failCount++
                }
            }

            // 4. Si la migración fue exitosa, limpiar Room (opcional)
            if (failCount == 0) {
                //inventoryDao.deleteAllInventories()
            }

            MigrationResult.Success(
                total = roomInventories.size,
                success = successCount,
                failed = failCount
            )

        } catch (e: Exception) {
            e.printStackTrace()
            MigrationResult.Error(e.message ?: "Error desconocido")
        }
    }

    suspend fun checkIfMigrationNeeded(context: Context): Boolean {
        return try {
            val roomDb = InventoryDB.getDatabase(context)
            val inventoryDao = roomDb.inventoryDao()
            val roomInventories = inventoryDao.getListInventory()
            roomInventories.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}

sealed class MigrationResult {
    data class Success(val total: Int, val success: Int, val failed: Int) : MigrationResult()
    data class Error(val message: String) : MigrationResult()
    object NoDataToMigrate : MigrationResult()
}