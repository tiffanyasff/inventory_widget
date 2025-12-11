package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.FragmentItemDetailsBinding
import com.univalle.inventorywidget.model.Product // IMPORTANTE: Usamos Product, no Inventory
import com.univalle.inventorywidget.viewmodel.InventoryViewModel

class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    // CAMBIO 1: Usamos Product porque es el que tiene el ID de Firestore
    private lateinit var currentProduct: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarToolbar()
        obtenerDatos()
        configurarEventos()
    }

    private fun configurarToolbar() {
        binding.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun obtenerDatos() {
        try {
            // Recuperamos el objeto que viene desde el Adapter
            currentProduct = arguments?.getSerializable("dataInventory") as Product

            // ---- PRUEBA DE FUEGO ----
            // Si aquí el Log muestra un ID vacío, el borrado NUNCA funcionará.
            Log.d("DETALLE_PRODUCTO", "ID Recibido: '${currentProduct.id}'")
            Log.d("DETALLE_PRODUCTO", "Nombre Recibido: '${currentProduct.name}'")
            // ------------------------

            // Pintamos los datos
            binding.tvName.text = "Nombre: ${currentProduct.name}"
            binding.tvPrice.text = "Precio: $ ${currentProduct.price}"
            binding.tvQuantity.text = "Cantidad: ${currentProduct.quantity}"
            binding.tvTotal.text =
                "Total: $ ${inventoryViewModel.totalProducto(currentProduct.price, currentProduct.quantity)}"

        } catch (e: Exception) {
            Log.e("DETALLE_PRODUCTO", "CRASH: El objeto recibido no es un 'Product' o es nulo. Error: ${e.message}")
        }
    }

    private fun configurarEventos() {

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar producto")
                .setMessage("¿Deseas eliminar este producto de la nube?") // Mensaje actualizado
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Sí") { _, _ ->

                    // CAMBIO 3: Llamamos a la función de borrar en FIRESTORE
                    inventoryViewModel.deleteProduct(currentProduct)

                    // No necesitas llamar a getListInventory() aquí manualmente,
                    // el ViewModel lo hará si la operación es exitosa.

                    findNavController().popBackStack()
                }
                .show()
        }

        binding.fabEdit.setOnClickListener {
            val bundle = Bundle()
            // Pasamos el producto para editar
            bundle.putSerializable("dataInventory", currentProduct)

            findNavController().navigate(
                R.id.action_itemDetailsFragment_to_itemEditFragment,
                bundle
            )
        }
    }
}
