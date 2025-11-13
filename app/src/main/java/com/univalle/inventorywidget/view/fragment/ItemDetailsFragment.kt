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
    private lateinit var receivedInventory: Inventory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarToolbar()
        dataInventory()
        controladores()
    }

    private fun configurarToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            // Volver al Home
            findNavController().popBackStack()
        }
    }

    private fun controladores() {
        binding.btnDelete.setOnClickListener {
            //deleteInventory()
            mostrarDialogoConfirmacion()
        }

        binding.fbEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("dataInventory", receivedInventory)
            findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
        }
    }



    private fun dataInventory() {
        val receivedBundle = arguments
        receivedInventory = receivedBundle?.getSerializable("clave") as Inventory
        binding.tvItem.text = "${receivedInventory.name}"
        binding.tvPrice.text = "$ ${receivedInventory.price}"
        binding.tvQuantity.text = "${receivedInventory.quantity}"
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar producto")
            .setMessage("¿Deseas eliminar este producto del inventario?")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Si") { _, _ ->
                deleteInventory()
            }
            .show()
    }

    private fun deleteInventory(){
        inventoryViewModel.deleteInventory(receivedInventory)
        inventoryViewModel.getListInventory()
        findNavController().popBackStack()
    }

}