package com.univalle.inventorywidget.repository

import com.google.firebase.auth.FirebaseAuth
import com.univalle.inventorywidget.model.UserResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    // Registrar usuario
    suspend fun registerUser(email: String, password: String): UserResponse {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            UserResponse(
                isRegister = true,
                message = "Usuario registrado exitosamente",
                email = email
            )
        } catch (e: Exception) {
            UserResponse(
                isRegister = false,
                message = e.message ?: "Error al registrar usuario",
                email = ""
            )
        }
    }

    // Iniciar sesión
    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Verificar si hay sesión activa
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Obtener usuario actual
    fun getCurrentUser() = auth.currentUser

    // Cerrar sesión
    fun signOut() = auth.signOut()
}
