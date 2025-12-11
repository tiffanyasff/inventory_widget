package com.univalle.inventorywidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.model.UserRequest
import com.univalle.inventorywidget.model.UserResponse
import com.univalle.inventorywidget.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    private val _isRegister = MutableLiveData<UserResponse>()
    val isRegister: LiveData<UserResponse> = _isRegister

    fun registerUser(userRequest: UserRequest) {
        viewModelScope.launch {
            val response = repository.registerUser(
                userRequest.email,
                userRequest.password
            )
            _isRegister.value = response
        }
    }

    fun loginUser(email: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            callback(result)
        }
    }

    fun sesion(callback: (Boolean) -> Unit) {
        callback(repository.isUserLoggedIn())
    }
}
