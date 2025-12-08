package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.univalle.inventorywidget.databinding.FragmentAddItemBinding
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemFragment : Fragment() {

    private lateinit var binding: FragmentAddItemBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddItemBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observarProductosAPI()
        validarCampos()
        configurarBotonGuardar()
    }

    /**
     * CRITERIO 2: Mostrar imagen + título del producto traído de la API
     */
    private fun observarProductosAPI() {
        inventoryViewModel.getProducts()

        inventoryViewModel.listProducts.observe(viewLifecycleOwner) { lista ->
            if (lista.isNotEmpty()) {
                val product = lista[2]   // Elegimos el producto 3 (como en develop)
                Glide.with(requireContext())
                    .load(product.image)
                    .into(binding.ivImagenApi)

                binding.tvTitleProduct.text = product.title
            }
        }
    }

    /**
     * Validamos que los campos no estén vacíos para habilitar el botón
     */
    private fun validarCampos() {
        val campos = listOf(binding.etName, binding.etPrice, binding.etQuantity)

        for (edit in campos) {
            edit.addTextChangedListener {
                val completos = campos.all { it.text.isNotEmpty() }
                binding.btnAdd.isEnabled = completos
            }
        }
    }

    /**
     * Guarda el inventario (Room)
     */
    private fun configurarBotonGuardar() {
        binding.btnAdd.setOnClickListener {
            val name = binding.etName.text.toString()
            val price = binding.etPrice.text.toString().toInt()
            val quantity = binding.etQuantity.text.toString().toInt()

            val inventory = Inventory(name = name, price = price, quantity = quantity)

            inventoryViewModel.saveInventory(inventory)

            Toast.makeText(requireContext(), "Artículo guardado", Toast.LENGTH_SHORT).show()

            findNavController().popBackStack()
        }
    }
}
