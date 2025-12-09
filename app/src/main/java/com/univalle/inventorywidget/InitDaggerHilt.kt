package com.univalle.inventorywidget

import android.app.Application // Import this
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InitDaggerHilt : Application() { // Add ": Application()" here
    override fun onCreate() {
        super.onCreate()
        // Initialization logic here if needed
    }
}
