package com.uma.ecofridge.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uma.ecofridge.model.Product

// Definimos la base de datos con sus entidades y la versión
@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        // Marcamos la instancia como Volatile para que sea atómica entre hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ecofridge_database"
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}