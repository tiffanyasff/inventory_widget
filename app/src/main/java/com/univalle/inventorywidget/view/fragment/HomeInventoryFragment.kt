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

        // Validar sesión
        val sharedPref = requireActivity().getSharedPreferences("SessionPref", 0)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_loginFragment)
            return
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
        // Toolbar sin menú por ahora
    }

    private fun configurarEventos() {
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addItemFragment)
        }
    }

    private fun observarViewModel() {
        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { lista ->
            binding.rvItems.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = InventoryAdapter(lista, findNavController())
            }
        }

        inventoryViewModel.progresState.observe(viewLifecycleOwner) { status ->
            binding.progressBarHome.isVisible = status
        }
    }

    private fun configurarBotonAtras() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().moveTaskToBack(true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
