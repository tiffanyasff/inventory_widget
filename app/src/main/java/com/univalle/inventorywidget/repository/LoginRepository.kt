package com.univalle.inventorywidget.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

class LoginRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser() = auth.currentUser

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun signOut() {
        auth.signOut()
    }
}