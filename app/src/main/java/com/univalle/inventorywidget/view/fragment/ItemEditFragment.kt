package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.databinding.FragmentItemEditBinding
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.viewmodel.InventoryViewModel

class ItemEditFragment : Fragment() {

    private lateinit var binding: FragmentItemEditBinding
    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var receivedInventory: Inventory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarToolbar()
        recibirDatos()
        configurarValidaciones()

        binding.btnEdit.setOnClickListener {
            actualizarProducto()
        }
    }

    private fun configurarToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun recibirDatos() {
        val bundle = arguments
        receivedInventory = bundle?.getSerializable("dataInventory") as Inventory

        binding.tvId.text = "Id: ${receivedInventory.id}"
        binding.etName.setText(receivedInventory.name)
        binding.etPrice.setText(receivedInventory.price.toString())
        binding.etQuantity.setText(receivedInventory.quantity.toString())
    }

    private fun configurarValidaciones() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validarCampos()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etName.addTextChangedListener(watcher)
        binding.etPrice.addTextChangedListener(watcher)
        binding.etQuantity.addTextChangedListener(watcher)
    }

    private fun validarCampos() {
        val name = binding.etName.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()
        val quantity = binding.etQuantity.text.toString().trim()

        val camposValidos = name.isNotEmpty() && price.isNotEmpty() && quantity.isNotEmpty()

        binding.btnEdit.isEnabled = camposValidos
        binding.btnEdit.alpha = if (camposValidos) 1f else 0.5f
    }

    private fun actualizarProducto() {
        val name = binding.etName.text.toString().trim()
        val price = binding.etPrice.text.toString().trim().toInt()   // ✔ Int
        val quantity = binding.etQuantity.text.toString().trim().toInt() // ✔ Int

        val inventarioActualizado = Inventory(
            id = receivedInventory.id,
            name = name,
            price = price,
            quantity = quantity
        )

        viewModel.updateInventory(inventarioActualizado)

        findNavController().popBackStack()
    }
}
