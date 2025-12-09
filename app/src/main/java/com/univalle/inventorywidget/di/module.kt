package com.univalle.inventorywidget.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.inventorywidget.data.InventoryDB
import com.univalle.inventorywidget.data.InventoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module { // Nota: Es mejor convención usar mayúscula inicial (Module en lugar de module)

    // 1. Proveer la instancia de Room Database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InventoryDB {
        return Room.databaseBuilder(
            context,
            InventoryDB::class.java,
            "inventory_database" // Nombre de tu base de datos
        )
            .fallbackToDestructiveMigration() // Opcional: Útil en desarrollo si cambias el esquema
            .build()
    }

    // 2. Proveer el DAO (Esto soluciona el error original de MissingBinding)
    @Provides
    fun provideInventoryDao(database: InventoryDB): InventoryDao {
        return database.inventoryDao()
    }

    // 3. Proveer Firebase Firestore (Necesario para DataMigrationHelper)
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
