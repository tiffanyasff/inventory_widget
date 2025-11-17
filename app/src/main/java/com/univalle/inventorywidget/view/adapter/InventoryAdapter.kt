package com.univalle.inventorywidget.view.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.databinding.ItemInventoryBinding
import com.univalle.inventorywidget.model.Inventory
import java.text.NumberFormat
import java.util.*

class InventoryAdapter(
    private val listInventory: List<Inventory>,
    private val navController: NavController
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    inner class InventoryViewHolder(val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(inventory: Inventory) {
            binding.tvName.text = inventory.name
            binding.tvId.text = "ID: ${inventory.id}"

            // 🔹 Formato dinero
            val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            binding.tvPrice.text = formatoMoneda.format(inventory.price)

            // 🔹 Navegación al detalle
            binding.cvInventory.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable("dataInventory", inventory)  // ✔ CLAVE CORREGIDA
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
