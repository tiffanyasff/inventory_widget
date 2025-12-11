package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentItemEditBinding
import com.univalle.inventorywidget.model.Product // Usamos Product (Nube)
import com.univalle.inventorywidget.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemEditFragment : Fragment() {

    private lateinit var binding: FragmentItemEditBinding
    private val viewModel: InventoryViewModel by viewModels()

    private lateinit var currentProduct: Product

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
        observarViewModel() // Nuevo: Observamos si se guardó bien

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
        try {
            currentProduct = arguments?.getSerializable("dataInventory") as Product

            // Mostramos los datos (usamos productCode si lo tienes, o el ID oculto para debug)
            binding.tvId.text = "Code: ${currentProduct.productCode}"
            binding.etName.setText(currentProduct.name)
            binding.etPrice.setText(currentProduct.price.toString())
            binding.etQuantity.setText(currentProduct.quantity.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al cargar datos del producto", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
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

    private fun observarViewModel() {
        // Observamos el estado de la edición para saber cuándo salir
        viewModel.editProductStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Artículo actualizado correctamente", Toast.LENGTH_SHORT).show()
                // Volvemos al Home
                findNavController().popBackStack(R.id.homeInventoryFragment, false)
                // Reseteamos el estado para futuras ediciones
                viewModel.resetEditStatus()
            } else {
                Toast.makeText(requireContext(), "Error al actualizar en la nube", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.progresState.observe(viewLifecycleOwner) { isLoading ->
            binding.btnEdit.isEnabled = !isLoading
            if (isLoading) binding.btnEdit.text = "Guardando..." else binding.btnEdit.text = "Editar"
        }
    }

    private fun actualizarProducto() {
        val name = binding.etName.text.toString().trim()

        val priceString = binding.etPrice.text.toString().trim()
        val quantityString = binding.etQuantity.text.toString().trim()

        val price = if (priceString.isNotEmpty()) priceString.toInt() else 0
        val quantity = if (quantityString.isNotEmpty()) quantityString.toInt() else 0


        val updatedProduct = Product(
            id = currentProduct.id,            // IMPORTANTE: Mantener el mismo ID de Firebase
            productCode = currentProduct.productCode, // Mantenemos el código visible
            name = name,
            price = price,
            quantity = quantity
        )

        // Llamamos al ViewModel para que haga el update
        viewModel.updateProduct(updatedProduct)
    }
}