package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
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
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var receivedInventory: Inventory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarToolbar()
        dataInventory()
        controladores()
        validarCamposParaBoton()
    }

    private fun configurarToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun controladores() {
        binding.btnEdit.setOnClickListener {
            updateInventory()
        }
    }

    private fun dataInventory() {
        val receivedBundle = arguments
        receivedInventory =
            receivedBundle?.getSerializable("dataInventory") as Inventory

        binding.tvId.text = "Id: ${receivedInventory.id}"

        binding.etName.setText(receivedInventory.name)
        binding.etPrice.setText(receivedInventory.price.toString())
        binding.etQuantity.setText(receivedInventory.quantity.toString())

        // 🔥 CRITERIO 6: máximo 40 caracteres en el nombre
        binding.etName.filters = arrayOf(InputFilter.LengthFilter(40))
    }

    // Función ultra robusta para limpiar caracteres invisibles
    private fun limpiarTexto(input: String): String {
        return input.replace(
            "[\\u0020\\u00A0\\u1680\\u2000-\\u200A\\u202F\\u205F\\u3000\\u200B\\u200C\\u200D\\u2060\\uFEFF]"
                .toRegex(), ""
        )
    }

    private fun validarCamposParaBoton() {

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val nombre = limpiarTexto(binding.etName.text.toString())
                val precio = limpiarTexto(binding.etPrice.text.toString())
                val cantidad = limpiarTexto(binding.etQuantity.text.toString())

                binding.btnEdit.isEnabled =
                    nombre.isNotBlank() && precio.isNotBlank() && cantidad.isNotBlank()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etName.addTextChangedListener(watcher)
        binding.etPrice.addTextChangedListener(watcher)
        binding.etQuantity.addTextChangedListener(watcher)

        val nombreInit = limpiarTexto(binding.etName.text.toString())
        val precioInit = limpiarTexto(binding.etPrice.text.toString())
        val cantidadInit = limpiarTexto(binding.etQuantity.text.toString())

        binding.btnEdit.isEnabled =
            nombreInit.isNotBlank() && precioInit.isNotBlank() && cantidadInit.isNotBlank()
    }

    private fun updateInventory() {
        val name = binding.etName.text.toString()
        val price = binding.etPrice.text.toString().toInt()
        val quantity = binding.etQuantity.text.toString().toInt()

        val inventoryUpdated = Inventory(
            receivedInventory.id,
            name,
            price,
            quantity
        )

        inventoryViewModel.updateInventory(inventoryUpdated)
        findNavController().popBackStack()
    }
}
