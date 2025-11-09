package com.univalle.inventorywidget.webservice
import com.univalle.inventorywidget.model.Product
import com.univalle.inventorywidget.utils.Constants.END_POINT
import retrofit2.http.GET

interface ApiService {
    @GET(END_POINT)
    suspend fun getProducts(): MutableList<Product>
}