package com.univalle.inventorywidget.view.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.ItemInventoryBinding
// CAMBIO 1: Importamos Product en lugar de Inventory
import com.univalle.inventorywidget.model.Product
import java.text.NumberFormat
import java.util.*

class InventoryAdapter(
    // CAMBIO 2: La lista ahora es de tipo Product
    private val listInventory: List<Product>,
    private val navController: NavController
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    inner class InventoryViewHolder(val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // CAMBIO 3: La función bind recibe un Product
        fun bind(product: Product) {
            binding.tvName.text = product.name

            // CAMBIO 4: Aquí usamos productCode (String). Ya no habrá error de tipo.
            binding.tvId.text = "Code: ${product.productCode}"

            // 🔹 Formato dinero
            val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            binding.tvPrice.text = formatoMoneda.format(product.price)

            // 🔹 Navegación al detalle
            binding.cvInventory.setOnClickListener {
                val bundle = Bundle().apply {
                    // CAMBIO 5: Ponemos el objeto PRODUCT completo (que incluye el id de Firestore)
                    // Mantenemos la clave "dataInventory" para que tu DetailFragment lo reciba sin cambios
                    putSerializable("dataInventory", product)
                }
                navController.navigate(
                    R.id.action_homeInventoryFragment_to_itemDetailsFragment,
                    bundle
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(listInventory[position])
    }

    override fun getItemCount(): Int = listInventory.size
}
