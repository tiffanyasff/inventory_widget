package com.univalle.inventorywidget.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // 🔥 BYPASS TEMPORAL: Entrar directo al Home sin huella
        guardarSesion()
        findNavController().navigate(R.id.action_loginFragment_to_homeInventoryFragment)
    }

    private fun guardarSesion() {
        val sharedPref = requireActivity()
            .getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("isLoggedIn", true).apply()
    }
}
