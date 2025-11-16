package com.univalle.inventorywidget.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.databinding.ItemInventoryBinding

class InventoryAdapter(
    private val listInventory: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    inner class InventoryViewHolder(val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvName.text = product.name
            binding.tvPrice.text = "$ ${product.price}"
            binding.tvId.text = "ID: ${product.id}"

            binding.cvInventory.setOnClickListener {
                onClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(listInventory[position])
    }

    override fun getItemCount(): Int = listInventory.size
}
