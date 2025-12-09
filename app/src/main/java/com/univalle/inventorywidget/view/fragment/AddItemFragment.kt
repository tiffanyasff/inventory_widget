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
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.databinding.FragmentAddItemBinding
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.viewmodel.InventoryViewModel
// import com.univalle.inventorywidget.model.Product // Esta importación no parece usarse, puedes borrarla si quieres.

class AddItemFragment : Fragment() {

    private lateinit var binding: FragmentAddItemBinding
    // Nota: Si usas Hilt, deberías agregar @AndroidEntryPoint a la clase
    private val inventoryViewModel: InventoryViewModel by viewModels()

    private val db = FirebaseFirestore.getInstance()

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
        binding.ivBack.setOnClickListener {
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


        if (rawCode.isNotEmpty() && rawName.isNotEmpty() && rawPrice.isNotEmpty() && rawQty.isNotEmpty()) {


            val productCode = rawCode.toInt()
            val price = rawPrice.toInt()
            val quantity = rawQty.toInt()


            val datos = hashMapOf(
                "productCode" to productCode,
                "name" to rawName,
                "price" to price,
                "quantity" to quantity
            )

            db.collection("Articulos")
                .document(productCode.toString())
                .set(datos)
                .addOnSuccessListener {
                    Toast.makeText(context, "Artículo guardado", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }

        } else {
            Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
        }
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
                val isListFull = listEditText.all { view ->
                    view.text.isNotEmpty()
                }

                binding.btnSaveInventory.isEnabled = isListFull
                binding.btnSaveInventory.alpha = if (isListFull) 1f else 0.5f
            }
        }
    }
}
