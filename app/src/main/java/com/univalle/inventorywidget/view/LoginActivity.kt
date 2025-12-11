package com.univalle.inventorywidget.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.ActivityLoginBinding
import com.univalle.inventorywidget.model.UserRequest
import com.univalle.inventorywidget.viewmodel.LoginViewModel
import com.univalle.inventorywidget.viewmodel.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)

        checkSession()
        setupViews()
        setupValidation()
        viewModelObservers()
    }

    private fun checkSession() {
        val email = sharedPreferences.getString("email", null)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (email != null && isLoggedIn) {
            goToHome()
        }
    }

    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun setupValidation() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateFields()
            }
        })

        binding.etPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()

                when {
                    password.isEmpty() -> {
                        binding.tilPassword.error = null
                        binding.tilPassword.boxStrokeColor = ContextCompat.getColor(
                            this@LoginActivity,
                            R.color.unfocusedBorder
                        )
                    }
                    password.length < 6 -> {
                        binding.tilPassword.error = "Mínimo 6 dígitos"
                        // Color rojo para el error
                        binding.tilPassword.setBoxStrokeColorStateList(
                            ContextCompat.getColorStateList(
                                this@LoginActivity,
                                android.R.color.holo_red_dark
                            )!!
                        )
                    }
                    else -> {
                        binding.tilPassword.error = null
                        binding.tilPassword.boxStrokeColor = ContextCompat.getColor(
                            this@LoginActivity,
                            R.color.focusedBorder
                        )
                    }
                }

                validateFields()
            }
        })
    }

    private fun validateFields() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPass.text.toString()

        // El botón se habilita solo si:
        // 1. Email no está vacío
        // 2. Password tiene mínimo 6 dígitos
        val isValid = email.isNotEmpty() && password.length >= 6

        binding.btnLogin.isEnabled = isValid

        // Cambiar el color del botón según el estado
        if (isValid) {
            binding.btnLogin.setTextColor(
                ContextCompat.getColor(this, R.color.textColorWhite)
            )
            binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(
                this,
                R.color.backgroundColorOrange
            )
        } else {
            binding.btnLogin.setTextColor(
                ContextCompat.getColor(this, R.color.backgroundColorGray)
            )
            binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(
                this,
                R.color.backgroundColorGray
            )
        }
    }

    private fun viewModelObservers() {
        observerIsRegister()
    }

    private fun observerIsRegister() {
        loginViewModel.isRegister.observe(this) { userResponse ->
            if (userResponse.isRegister) {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
                sharedPreferences.edit()
                    .putString("email", userResponse.email)
                    .putBoolean("isLoggedIn", true)
                    .apply()
                goToHome()
            } else {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        val userRequest = UserRequest(email, pass)

        if (email.isNotEmpty() && pass.length >= 6) {
            loginViewModel.registerUser(userRequest)
        } else {
            if (email.isEmpty()) {
                Toast.makeText(this, "Email vacío", Toast.LENGTH_SHORT).show()
            } else if (pass.length < 6) {
                Toast.makeText(this, "La contraseña debe tener mínimo 6 dígitos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Email vacío", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass.length < 6) {
            Toast.makeText(this, "La contraseña debe tener mínimo 6 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        loginViewModel.loginUser(email, pass) { isLogin ->
            if (isLogin) {
                sharedPreferences.edit()
                    .putString("email", email)
                    .putBoolean("isLoggedIn", true)
                    .apply()
                goToHome()
            } else {
                Toast.makeText(this, "Login incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}