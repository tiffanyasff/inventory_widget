package com.univalle.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.univalle.inventorywidget.model.UserRequest
import com.univalle.inventorywidget.model.UserResponse
import com.univalle.inventorywidget.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _isRegister = MutableLiveData<UserResponse>()
    val isRegister: LiveData<UserResponse> = _isRegister

    fun registerUser(userRequest: UserRequest) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(
                    userRequest.email,
                    userRequest.password
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _isRegister.value = UserResponse(
                            isRegister = true,
                            message = "Usuario registrado exitosamente",
                            email = userRequest.email
                        )
                    } else {
                        _isRegister.value = UserResponse(
                            isRegister = false,
                            message = task.exception?.message ?: "Error al registrar usuario",
                            email = ""
                        )
                    }
                }
            } catch (e: Exception) {
                _isRegister.value = UserResponse(
                    isRegister = false,
                    message = e.message ?: "Error desconocido",
                    email = ""
                )
            }
        }
    }

    fun loginUser(email: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        callback(task.isSuccessful)
                    }
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    fun sesion(email: String?, callback: (Boolean) -> Unit) {
        if (email != null && auth.currentUser != null) {
            callback(true)
        } else {
            callback(false)
        }
    }
}