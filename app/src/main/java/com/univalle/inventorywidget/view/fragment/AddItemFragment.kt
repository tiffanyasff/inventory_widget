package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.databinding.FragmentAddItemBinding
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.viewmodel.InventoryViewModel

class AddItemFragment : Fragment() {

    private lateinit var binding: FragmentAddItemBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddItemBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        botones()
        validarDatos()
    }

    private fun botones() {
        // 🔙 Flecha atrás (arrow back)
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Guardar
        binding.btnSaveInventory.setOnClickListener {
            saveInventory()
        }
    }

    private fun saveInventory() {

        val productCode = binding.etProductCode.text.toString().toIntOrNull() ?: 0
        val name = binding.etName.text.toString()
        val price = binding.etPrice.text.toString().toIntOrNull() ?: 0
        val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0

        val inventory = Inventory(
            name = name,
            price = price,
            quantity = quantity
        )

        inventoryViewModel.saveInventory(inventory)

        Log.d("test", "Código: $productCode, $inventory")

        Toast.makeText(requireContext(), "Artículo guardado !!", Toast.LENGTH_SHORT).show()

        findNavController().popBackStack()
    }

    private fun validarDatos() {

        val listEditText = listOf(
            binding.etProductCode,
            binding.etName,
            binding.etPrice,
            binding.etQuantity
        )

        for (editText in listEditText) {
            editText.addTextChangedListener {

                val isListFull = listEditText.all {
                    it.text.isNotEmpty()
                }

                binding.btnSaveInventory.isEnabled = isListFull

                // si está deshabilitado, que sea más opaco
                binding.btnSaveInventory.alpha = if (isListFull) 1f else 0.5f
            }
        }
    }
}
