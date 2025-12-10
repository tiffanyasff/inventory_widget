package com.univalle.inventorywidget.model

import com.google.gson.annotations.SerializedName

data class Product(
   val productCode:Int,
   val name:String,
   val price:Int,
   val quantity:Int
)
