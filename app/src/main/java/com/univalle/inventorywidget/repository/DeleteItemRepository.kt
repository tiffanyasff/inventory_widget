package com.univalle.inventorywidget.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DeleteItemRepository {

    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    private val collectionName = "Articulos"

    /**
     * Elimina un producto de Firestore utilizando su ID.
     *
     * @param productId El ID del documento en Firestore (no el nombre del producto).
     * @return Result<Boolean> Retorna Success(true) si salió bien, o Failure(exception) si falló.
     */
    suspend fun deleteItem(productId: String): Result<Boolean> {
        return try {
            // Accedemos a la colección, buscamos el documento por ID y ejecutamos delete()
            db.collection("Articulos")
                .document(productId)
                .delete()
                .await() // .await() suspende la corrutina hasta que Firebase responda

            // Si llegamos a esta línea, Firebase confirmó la eliminación
            Result.success(true)
        } catch (e: Exception) {
            // Capturamos errores de red, permisos, etc.
            e.printStackTrace()
            Result.failure(e)
        }
    }

    companion object
}
