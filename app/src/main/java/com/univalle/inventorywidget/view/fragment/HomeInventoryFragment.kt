package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.data.InventoryDB
import com.univalle.inventorywidget.databinding.FragmentHomeInventoryBinding
import com.univalle.inventorywidget.view.adapter.InventoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeInventoryFragment : Fragment() {

    private lateinit var binding: FragmentHomeInventoryBinding
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

        cargarInventario()

        binding.fabAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addItemFragment)
        }
    }

    private fun cargarInventario() {
        binding.progress.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {

            // 👈 CORREGIDO: antes llamaba getAllProducts() (no existe)
            val listInventory =
                InventoryDB.getDatabase(requireContext()).inventoryDao().getAllInventory()

            requireActivity().runOnUiThread {
                binding.progress.visibility = View.GONE

                adapter = InventoryAdapter(
                    listInventory = listInventory,
                    onClick = {
                        // Por ahora solo navega al detalle sin argumentos
                        findNavController().navigate(
                            R.id.action_homeInventoryFragment_to_itemDetailsFragment
                        )
                    }
                )

                binding.recyclerview.adapter = adapter
            }
        }
    }
}
