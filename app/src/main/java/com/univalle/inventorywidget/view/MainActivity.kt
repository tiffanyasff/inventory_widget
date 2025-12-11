package com.univalle.inventorywidget.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.univalle.inventorywidget.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        //  ⚡ Verifica si LoginActivity pidió abrir HomeInventoryFragment
        if (intent.getBooleanExtra("openHomeInventory", false)) {

            // Obtener el NavHostFragment que controla la navegación
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.navigationContainer) as NavHostFragment

            val navController = navHostFragment.navController

            // Navegar explícitamente a HomeInventoryFragment
            navController.navigate(R.id.homeInventoryFragment)

            // Eliminar el flag para evitar navegación repetida
            intent.removeExtra("openHomeInventory")
        }
    }
}
