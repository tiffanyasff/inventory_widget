package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentHomeInventoryBinding
import com.univalle.inventorywidget.view.adapter.InventoryAdapter
import com.univalle.inventorywidget.viewmodel.InventoryViewModel
import androidx.activity.OnBackPressedCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeInventoryFragment : Fragment() {
    private lateinit var binding: FragmentHomeInventoryBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verificar sesión
        val sharedPref = requireActivity().getSharedPreferences("SessionPref", 0)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_loginFragment)
            return
        }

        configurarToolbar()
        configurarRecyclerView()
        observadoresViewModel()
        configurarBotonAtras()
    }

    private fun configurarToolbar() {
        binding.ivLogout.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("SessionPref", 0)
            sharedPref.edit().clear().apply()
            findNavController().navigate(R.id.action_homeInventoryFragment_to_loginFragment)
        }
    }

    private fun configurarRecyclerView() {
        binding.fabAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addItemFragment)
        }
    }

    private fun observadoresViewModel() {
        inventoryViewModel.getListInventory()
        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { list ->
            binding.recyclerview.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = InventoryAdapter(list, findNavController())
            }
        }
        inventoryViewModel.progresState.observe(viewLifecycleOwner) { status ->
            binding.progress.isVisible = status
        }
    }

    private fun configurarBotonAtras() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Enviar la app al escritorio en lugar de regresar al Login
                requireActivity().moveTaskToBack(true)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback) }
}
