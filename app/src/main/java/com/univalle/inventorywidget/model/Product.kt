package com.univalle.inventorywidget.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable // <--- 1. IMPORTANTE: Agrega este import

data class Product(
    // Este campo lo llenamos manualmente, no viene en el JSON del documento
    @get:Exclude var id: String = "",
    val productCode: Int = 0,
    val name: String = "",
    val price: Int = 0,
    val quantity: Int = 0
    // ... otros campos que tengas ...

) : Serializable //
