package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.univalle.inventorywidget.databinding.FragmentAddItemBinding
import com.univalle.inventorywidget.repository.AddItemRepository
import com.univalle.inventorywidget.viewmodel.AddItemViewModel
import com.univalle.inventorywidget.viewmodel.AddItemViewModelFactory
import com.univalle.inventorywidget.viewmodel.SaveState
import kotlinx.coroutines.launch

class AddItemFragment : Fragment() {

    private lateinit var binding: FragmentAddItemBinding

    private val viewModel: AddItemViewModel by viewModels {
        AddItemViewModelFactory(AddItemRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        botones()
        validarDatos()
        observeSaveState()
    }


    private fun botones() {
        binding.toolbarAdd.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSaveInventory.setOnClickListener {
            saveInventory()
        }
    }

    private fun saveInventory() {
        val rawCode = binding.etProductCode.text.toString()
        val rawName = binding.etName.text.toString()
        val rawPrice = binding.etPrice.text.toString()
        val rawQty = binding.etQuantity.text.toString()

        viewModel.saveInventory(rawCode, rawName, rawPrice, rawQty)
    }

    private fun observeSaveState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveState.collect { state ->
                when (state) {
                    is SaveState.Idle -> { }
                    is SaveState.Loading -> { }
                    is SaveState.Success -> {
                        Toast.makeText(context, "Artículo guardado", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                        viewModel.resetSaveState()
                    }
                    is SaveState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        viewModel.resetSaveState()
                    }
                }
            }
        }
    }

    private fun validarDatos() {
        val listEditText: List<TextInputEditText> = listOf(
            binding.etProductCode,
            binding.etName,
            binding.etPrice,
            binding.etQuantity
        )
        listEditText.forEach { editText ->
            editText.addTextChangedListener {
                val isListFull = listEditText.all { campo ->
                    !campo.text.isNullOrBlank()
                }
                binding.btnSaveInventory.isEnabled = isListFull
            }
        }
    }
}
