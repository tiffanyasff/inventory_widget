package com.univalle.inventorywidget.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validarCampos()
        setupLoginButton()
    }

    // -----------------------------------------------------------------------------------------------------------------
    // VALIDACIÓN REALTIME
    // -----------------------------------------------------------------------------------------------------------------
    private fun validarCampos() {
        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validarPassword()
            habilitarBoton()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun validarPassword() {
        val password = binding.etPassword.text.toString()

        if (password.length in 1..5) {
            binding.tilPassword.error = "Mínimo 6 dígitos"
            binding.tilPassword.setErrorTextColor(
                resources.getColorStateList(R.color.redError, null)
            )
        } else {
            binding.tilPassword.error = null
        }
    }

    private fun habilitarBoton() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        val camposLlenos = email.isNotEmpty() && password.length >= 6

        binding.btnLogin.isEnabled = camposLlenos
        binding.btnLogin.alpha = if (camposLlenos) 1f else 0.4f
    }

    // -----------------------------------------------------------------------------------------------------------------
    // ACCIÓN DEL BOTÓN LOGIN
    // -----------------------------------------------------------------------------------------------------------------
    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            // 🔹 Simulación de autenticación
            if (validarCredenciales(email, password)) {
                guardarSesion()
                findNavController().navigate(R.id.action_loginFragment_to_homeInventoryFragment)
            } else {
                Toast.makeText(requireContext(), "Login incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔹 Lógica de validación (sustituye esto con tu API o base de datos si deseas)
    private fun validarCredenciales(email: String, password: String): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("usuarios", Context.MODE_PRIVATE)

        val emailGuardado = sharedPref.getString("email", "")
        val passGuardado = sharedPref.getString("password", "")

        return email == emailGuardado && password == passGuardado
    }

    private fun guardarSesion() {
        val sharedPref = requireActivity().getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("isLoggedIn", true).apply()
    }
}
