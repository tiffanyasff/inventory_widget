package com.univalle.inventorywidget.migration

import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.data.InventoryDao
import com.univalle.inventorywidget.model.Inventory
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Buena práctica: solo necesitas una instancia de esto en toda la app
class DataMigrationHelper @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val inventoryDao: InventoryDao // <--- CAMBIO CLAVE: Inyectar DAO directamente
) {

    // Ya no necesitas pasar 'context' como parámetro
    suspend fun migrateFromRoomToFirebase(): MigrationResult {
        return try {
            // 1. Leer todos los inventarios de Room (usando el DAO inyectado)
            val roomInventories = inventoryDao.getListInventory()

            if (roomInventories.isEmpty()) {
                return MigrationResult.NoDataToMigrate
            }

            // 2. Migrar cada inventario a Firebase
            val inventoryCollection = firestore.collection("inventories")
            var successCount = 0
            var failCount = 0

            roomInventories.forEach { roomInventory ->
                try {
                    // Crear documento en Firebase con ID autogenerado
                    val docRef = inventoryCollection.document()

                    // Convertir el inventario de Room a Firebase
                    // Asegúrate de que los nombres de los campos coincidan con tu modelo de datos
                    val firebaseInventory = Inventory(
                        id = docRef.id,
                        name = roomInventory.name,
                        price = roomInventory.price,
                        quantity = roomInventory.quantity
                        // Agrega otros campos si tu modelo Inventory los tiene (ej. urlImagen)
                    )

                    // Guardar en Firebase
                    docRef.set(firebaseInventory).await()
                    successCount++
                } catch (e: Exception) {
                    e.printStackTrace()
                    failCount++
                }
            }

            // 3. Si la migración fue exitosa, limpiar Room
            if (failCount == 0 && successCount > 0) {
                // Descomenta esto cuando estés seguro de que funciona
                // inventoryDao.deleteAllInventories()
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

    // Ya no necesitas 'context' aquí tampoco
    suspend fun checkIfMigrationNeeded(): Boolean {
        return try {
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
