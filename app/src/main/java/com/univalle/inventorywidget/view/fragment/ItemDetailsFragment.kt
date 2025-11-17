package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentItemDetailsBinding
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.viewmodel.InventoryViewModel
import androidx.appcompat.app.AlertDialog

class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private var receivedInventory: Inventory? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarToolbar()
        recibirDatos()
        configurarListeners()
    }

    private fun configurarToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun recibirDatos() {
        receivedInventory = arguments?.getSerializable("clave") as? Inventory

        if (receivedInventory == null) {
            // Evita que la app se cierre
            binding.tvItem.text = "Error al cargar"
            return
        }

        binding.tvItem.text = receivedInventory!!.name
        binding.tvPrice.text = "$ ${receivedInventory!!.price}"
        binding.tvQuantity.text = "${receivedInventory!!.quantity}"

        val total = inventoryViewModel.totalProducto(
            receivedInventory!!.price,
            receivedInventory!!.quantity
        )
        binding.txtTotal.text = "$ $total"
    }

    private fun configurarListeners() {
        binding.btnDelete.setOnClickListener {
            mostrarDialogoConfirmacion()
        }

        binding.fbEdit.setOnClickListener {
            receivedInventory?.let {
                val bundle = Bundle().apply {
                    putSerializable("dataInventory", it)
                }
                findNavController().navigate(
                    R.id.action_itemDetailsFragment_to_itemEditFragment,
                    bundle
                )
            }
        }
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar producto")
            .setMessage("¿Deseas eliminar este producto del inventario?")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sí") { _, _ -> eliminarProducto() }
            .show()
    }

    private fun eliminarProducto() {
        receivedInventory?.let {
            inventoryViewModel.deleteInventory(it)
            inventoryViewModel.getListInventory()
        }
        findNavController().popBackStack()
    }
}
