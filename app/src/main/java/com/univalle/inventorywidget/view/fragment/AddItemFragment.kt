package com.univalle.inventorywidget.view.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.data.InventoryDB
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.databinding.FragmentAddItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddItemFragment : Fragment() {

    private lateinit var binding: FragmentAddItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupAgregar()
    }

    private fun setupToolbar() {
        binding.toolbarAdd.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupAgregar() {
        binding.btnAgregar.setOnClickListener {

            val name = binding.editTextName.text.toString().trim()
            val price = binding.editTextPrice.text.toString().trim()
            val quantity = binding.editTextQuantity.text.toString().trim()

            if (name.isEmpty() || price.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(requireContext(), "Complete los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val product = Product(
                id = 0,
                name = name,
                price = price.toDouble(),
                quantity = quantity.toInt()
            )

            CoroutineScope(Dispatchers.IO).launch {
                InventoryDB.getDatabase(requireContext()).inventoryDao().insert(product)

                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Producto agregado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }
}
