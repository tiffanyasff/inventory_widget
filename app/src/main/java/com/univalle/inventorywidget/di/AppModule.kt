package com.univalle.inventorywidget.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
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
object AppModule {

    @Provides
    @Singleton
    fun provideInventoryDatabase(@ApplicationContext context: Context): InventoryDB {
        return InventoryDB.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideInventoryDao(db: InventoryDB): InventoryDao {
        return db.inventoryDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}