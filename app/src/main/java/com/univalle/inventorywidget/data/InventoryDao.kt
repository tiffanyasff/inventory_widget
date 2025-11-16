package com.univalle.inventorywidget.data

import androidx.room.*

@Dao
interface InventoryDao {

    @Insert
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM inventory_table ORDER BY id DESC")
    suspend fun getAllInventory(): List<Product>

    @Query("SELECT * FROM inventory_table WHERE id = :id")
    suspend fun getProductById(id: Int): Product?
}
