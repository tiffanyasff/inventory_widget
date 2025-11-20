package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentItemDetailsBinding
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.viewmodel.InventoryViewModel

class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var receivedInventory: Inventory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarToolbar()
        obtenerDatos()
        configurarEventos()
    }

    private fun configurarToolbar() {
        binding.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun obtenerDatos() {
        receivedInventory = arguments?.getSerializable("dataInventory") as Inventory

        inventoryViewModel.getListInventory()

        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { lista ->
            val actualizado = lista.firstOrNull { it.id == receivedInventory.id }

            if (actualizado != null) {
                receivedInventory = actualizado

                binding.tvName.text = "Nombre: ${actualizado.name}"
                binding.tvPrice.text = "Precio: $ ${actualizado.price}"
                binding.tvQuantity.text = "Cantidad: ${actualizado.quantity}"
                binding.tvTotal.text =
                    "Total: $ ${inventoryViewModel.totalProducto(actualizado.price, actualizado.quantity)}"
            }
        }
    }

    private fun configurarEventos() {

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar producto")
                .setMessage("¿Deseas eliminar este producto del inventario?")
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Sí") { _, _ ->
                    inventoryViewModel.deleteInventory(receivedInventory)
                    inventoryViewModel.getListInventory()
                    findNavController().popBackStack()
                }
                .show()
        }

        binding.fabEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("dataInventory", receivedInventory)

            findNavController().navigate(
                R.id.action_itemDetailsFragment_to_itemEditFragment,
                bundle
            )
        }
    }
}
