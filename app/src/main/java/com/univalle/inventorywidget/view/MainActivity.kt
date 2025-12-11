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

        // ✔ Garantiza navegación al HomeInventory si el widget lo solicita
        if (intent.getBooleanExtra("openHomeInventory", false)) {

            val navHost = supportFragmentManager
                .findFragmentById(R.id.navigationContainer) as NavHostFragment

            val navController = navHost.navController

            // Navega a HU 3.0
            navController.navigate(R.id.homeInventoryFragment)

            // Limpia el flag para evitar repeticiones
            intent.removeExtra("openHomeInventory")
        }
    }
}
