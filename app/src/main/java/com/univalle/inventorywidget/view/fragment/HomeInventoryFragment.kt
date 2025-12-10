package com.univalle.inventorywidget.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentHomeInventoryBinding
import com.univalle.inventorywidget.view.LoginActivity
import com.univalle.inventorywidget.view.adapter.InventoryAdapter
import com.univalle.inventorywidget.viewmodel.InventoryViewModel

class HomeInventoryFragment : Fragment() {

    private lateinit var binding: FragmentHomeInventoryBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración del botón Cerrar Sesión
        // Verifica si en tu XML el ID es btonSigOut o btoSigOut
        binding.btonSigOut.setOnClickListener {
            inventoryViewModel.cerrarSesion()
        }

        configurarToolbar()
        configurarEventos()
        observarViewModel()
        configurarBotonAtras()
    }

    override fun onResume() {
        super.onResume()
        inventoryViewModel.getListInventory()
    }

    private fun configurarToolbar() {
        // Tu configuración de toolbar si la tienes
    }

    private fun configurarEventos() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeInventoryFragment_to_addItemFragment
            )
        }
    }

    private fun observarViewModel() {
        // Observador de la lista de inventario
        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { lista ->
            binding.rvItems.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = InventoryAdapter(lista, findNavController())
            }
        }

        // Observador de la barra de progreso
        inventoryViewModel.progresState.observe(viewLifecycleOwner) { status ->
            binding.progressBarHome.isVisible = status
        }

        // Observador para la navegación de Logout hacia LoginActivity
        inventoryViewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                // 1. IMPORTANTE: Limpiar SharedPreferences para evitar auto-login
                val sharedPreferences = requireActivity().getSharedPreferences("shared", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear() // Borra email y isLoggedIn
                editor.apply()

                // 2. Crear Intent hacia LoginActivity
                val intent = Intent(requireContext(), LoginActivity::class.java)

                // 3. Flags: Borrar historial para que no puedan volver atrás
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                // 4. Iniciar Activity y cerrar Fragment actual
                startActivity(intent)
                requireActivity().finish()

                // 5. Resetear estado en ViewModel
                inventoryViewModel.onLoginNavigationComplete()
            }
        }
    }

    private fun configurarBotonAtras() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().moveTaskToBack(true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callback
        )
    }
}
