package com.univalle.inventorywidget.model

import com.google.gson.annotations.SerializedName

data class Product(
   var productCode: Int = 0,
   var name: String = "",
   var price: Int = 0,
   var quantity: Int = 0
)
