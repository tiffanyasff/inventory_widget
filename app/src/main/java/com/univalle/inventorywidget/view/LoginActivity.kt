package com.univalle.inventorywidget.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.ActivityLoginBinding
import com.univalle.inventorywidget.model.UserRequest
import com.univalle.inventorywidget.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)

        setupViews()
        setupValidation()
        viewModelObservers()
    }

    /* -------------------------------------------------------------------
       BOTONES
    ------------------------------------------------------------------- */

    private fun setupViews() {
        binding.btnLogin.setOnClickListener { loginUser() }
        binding.tvRegister.setOnClickListener { registerUser() }
    }

    /* -------------------------------------------------------------------
       VALIDACIÓN
    ------------------------------------------------------------------- */

    private fun setupValidation() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateFields() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etPass.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateFields() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validateFields() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()

        val valid = email.isNotEmpty() && pass.length >= 6

        binding.btnLogin.isEnabled = valid

        if (valid) {
            binding.btnLogin.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnLogin.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.backgroundColorOrange)
        } else {
            binding.btnLogin.setTextColor(ContextCompat.getColor(this, R.color.gray))
            binding.btnLogin.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.backgroundColorGray)
        }
    }

    /* -------------------------------------------------------------------
       OBSERVADORES
    ------------------------------------------------------------------- */

    private fun viewModelObservers() {
        loginViewModel.isRegister.observe(this) { userResponse ->
            if (userResponse.isRegister) {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
                saveSession(userResponse.email)
                finishLoginFlow()
            } else {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* -------------------------------------------------------------------
       REGISTRO
    ------------------------------------------------------------------- */

    private fun registerUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Email vacío", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass.length < 6) {
            Toast.makeText(this, "Contraseña mínimo 6 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        loginViewModel.registerUser(UserRequest(email, pass))
    }

    /* -------------------------------------------------------------------
       LOGIN (MODIFICADO PARA CRITERIOS 10, 13 Y 14)
    ------------------------------------------------------------------- */

    private fun loginUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()

        loginViewModel.loginUser(email, pass) { success ->
            if (!success) {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                return@loginUser
            }

            saveSession(email)
            finishLoginFlow()
        }
    }

    /* -------------------------------------------------------------------
       SESIÓN Y FLUJO SEGÚN ORIGEN
    ------------------------------------------------------------------- */

    private fun saveSession(email: String) {
        sharedPreferences.edit()
            .putString("email", email)
            .putBoolean("isLoggedIn", true)
            .apply()
    }

    private fun finishLoginFlow() {

        val fromWidgetEye = intent.getBooleanExtra("fromWidgetEye", false)
        val fromWidget = intent.getBooleanExtra("fromWidget", false)

        // 🔹 Caso 1: Venía del OJO → volver al widget
        if (fromWidgetEye) {
            finish()
            return
        }

        // 🔹 Caso 2: Venía de Gestionar inventario → ir al HomeInventory
        if (fromWidget) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openHomeInventory", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // 🔹 Caso 3: flujo normal de la app
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}