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
// import com.univalle.inventorywidget.model.Inventory // Ya no necesitamos Inventory aquí para el adapter
import com.univalle.inventorywidget.viewmodel.ListItemViewModel

class HomeInventoryFragment : Fragment() {

    private lateinit var binding: FragmentHomeInventoryBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    // ViewModel que trae los productos desde Firebase
    private val listItemViewModel: ListItemViewModel by viewModels()

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
        // Recargamos los productos cada vez que volvemos a esta pantalla
        listItemViewModel.loadProducts()
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

        // --- BLOQUE ELIMINADO ---
        // Se eliminó inventoryViewModel.listInventory.observe(...)
        // porque enviaba datos de tipo 'Inventory' y el Adapter ahora solo acepta 'Product'.
        // ------------------------

        inventoryViewModel.progresState.observe(viewLifecycleOwner) { status ->
            binding.progressBarHome.isVisible = status
        }

        inventoryViewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val sharedPreferences = requireActivity()
                    .getSharedPreferences("shared", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                requireActivity().finish()

                inventoryViewModel.onLoginNavigationComplete()
            }
        }

        // --- BLOQUE CORREGIDO ---
        listItemViewModel.products.observe(viewLifecycleOwner) { products ->
            binding.rvItems.apply {
                layoutManager = LinearLayoutManager(context)

                // CORRECCIÓN: Pasamos la lista 'products' directamente.
                // Esta lista es de tipo List<Product>, que coincide con lo que pide tu Adapter actualizado.
                adapter = InventoryAdapter(products, findNavController())
            }
        }

        listItemViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarHome.isVisible = isLoading
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
