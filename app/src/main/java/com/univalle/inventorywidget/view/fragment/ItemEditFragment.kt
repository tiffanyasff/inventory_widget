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
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.univalle.inventorywidget.model.Product
import com.univalle.inventorywidget.repository.AddItemRepository
import kotlinx.coroutines.launch
import com.univalle.inventorywidget.R


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
        val price = binding.etPrice.text.toString().trim().toInt()
        val quantity = binding.etQuantity.text.toString().trim().toInt()

        // 1) Actualizamos el objeto Inventory (Room / lógica interna)
        val inventarioActualizado = Inventory(
            id = receivedInventory.id,   // este id lo estamos usando como productCode en Firebase
            name = name,
            price = price,
            quantity = quantity
        )

        // Opcional: si quieres seguir actualizando también la BD local (Room)
        viewModel.updateInventory(inventarioActualizado)

        // 2) Convertimos a Product para Firebase
        val product = Product(
            productCode = inventarioActualizado.id,
            name = inventarioActualizado.name,
            price = inventarioActualizado.price,
            quantity = inventarioActualizado.quantity
        )

        // 3) Usamos el mismo repositorio que para guardar: hace .set() en el doc => crea o ACTUALIZA
        val repository = AddItemRepository()

        viewLifecycleOwner.lifecycleScope.launch {
            val success = repository.saveProduct(product)

            if (success) {
                Toast.makeText(requireContext(), "Artículo actualizado", Toast.LENGTH_SHORT).show()
                // Volvemos a la pantalla home solo cuando Firebase ya se actualizó
                findNavController().popBackStack(R.id.homeInventoryFragment, false)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al actualizar el artículo en la base de datos",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}
