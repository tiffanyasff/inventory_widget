package com.univalle.inventorywidget.view.viewholder

import android.os.Bundle
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.ItemInventoryBinding
import com.univalle.inventorywidget.model.Inventory
import java.text.NumberFormat
import java.util.*

class InventoryViewHolder(
    private val binding: ItemInventoryBinding,
    private val navController: NavController
) : RecyclerView.ViewHolder(binding.root) {

    fun setItemInventory(inventory: Inventory) {

        binding.tvName.text = inventory.name

        binding.tvId.text = "ID: ${inventory.id}"

        val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        val precioFormateado = formatoMoneda.format(inventory.price)
        binding.tvPrice.text = precioFormateado


        binding.cvInventory.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("clave", inventory)
            }
            navController.navigate(
                R.id.action_homeInventoryFragment_to_itemDetailsFragment,
                bundle
            )
        }
    }
}
