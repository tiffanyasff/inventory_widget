package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentItemEditBinding
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.repository.InventoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemEditFragment : Fragment() {

    private lateinit var binding: FragmentItemEditBinding
    private lateinit var repository: InventoryRepository
    private var currentItem: Inventory? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemEditBinding.inflate(inflater, container, false)
        repository = InventoryRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recibir producto desde argumentos
        currentItem = arguments?.getSerializable("item") as? Inventory

        currentItem?.let { item ->
            binding.tvId.text = "Id: ${item.id}"
            binding.etName.setText(item.name)
            binding.etPrice.setText(item.price.toString())
            binding.etQuantity.setText(item.quantity.toString())
        }

        setupToolbar()
        setupValidations()

        binding.btnEdit.setOnClickListener {
            saveChanges()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    /** CONFIGURA TODAS LAS VALIDACIONES */
    private fun setupValidations() {
        binding.btnEdit.isEnabled = false

        binding.etName.addTextChangedListener(fieldWatcher)
        binding.etPrice.addTextChangedListener(fieldWatcher)
        binding.etQuantity.addTextChangedListener(fieldWatcher)
    }

    /** TextWatcher general para todos los campos */
    private val fieldWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validateFields()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /** VERIFICA TODAS LAS VALIDACIONES */
    private fun validateFields() {
        val name = binding.etName.text.toString()
        val price = binding.etPrice.text.toString()
        val qty = binding.etQuantity.text.toString()

        var isValid = true

        // VALIDACIÓN 1 → Nombre (máx 40 chars)
        if (name.length > 40) {
            binding.etName.error = "Máximo 40 caracteres"
            isValid = false
        }

        // VALIDACIÓN 2 → Precio (solo números, máx 20 dígitos)
        if (price.isNotEmpty() && (!price.matches(Regex("\\d+")) || price.length > 20)) {
            binding.etPrice.error = "Solo números (máx 20 dígitos)"
            isValid = false
        }

        // VALIDACIÓN 3 → Cantidad (solo números, máx 4 dígitos)
        if (qty.isNotEmpty() && (!qty.matches(Regex("\\d+")) || qty.length > 4)) {
            binding.etQuantity.error = "Solo números (máx 4 dígitos)"
            isValid = false
        }

        // VALIDACIÓN 4 → Ningún campo vacío
        if (name.isEmpty() || price.isEmpty() || qty.isEmpty()) {
            isValid = false
        }

        binding.btnEdit.isEnabled = isValid
    }

    /** GUARDAR CAMBIOS EN BD Y VOLVER */
    private fun saveChanges() {
        val newName = binding.etName.text.toString()
        val newPrice = binding.etPrice.text.toString().toInt()
        val newQty = binding.etQuantity.text.toString().toInt()

        val updatedItem = currentItem?.copy(
            name = newName,
            price = newPrice,
            quantity = newQty
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (updatedItem != null) {
                repository.updateRepositoy(updatedItem)
            }
        }

        findNavController().popBackStack()
    }
}
