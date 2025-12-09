package com.univalle.inventorywidget.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Inventory(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var price: Int = 0,
    var quantity: Int = 0
) : Serializable {
    // Constructor vacío requerido por Firebase Firestore
    constructor() : this("", "", 0, 0)
}