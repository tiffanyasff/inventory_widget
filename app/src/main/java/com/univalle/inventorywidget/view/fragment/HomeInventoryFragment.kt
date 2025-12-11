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
import com.univalle.inventorywidget.model.Inventory
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

        // 🔹 A) SIGUE tu código de antes (Room + logout)
        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { lista ->
            binding.rvItems.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = InventoryAdapter(lista, findNavController())
            }
        }

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

        // 🔹 B) NUEVO: lista que viene desde Firebase
        listItemViewModel.products.observe(viewLifecycleOwner) { products ->

            // Convertimos Product -> Inventory para reutilizar InventoryAdapter
            val inventoryList = products.map { product ->
                Inventory(
                    id = product.productCode,   // usamos el código como id
                    name = product.name,
                    price = product.price,
                    quantity = product.quantity
                )
            }.toMutableList()

            binding.rvItems.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = InventoryAdapter(inventoryList, findNavController())
            }
        }

        // 🔹 C) NUEVO: loading de Firebase
        listItemViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Si prefieres, puedes combinar esto con progresState
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
