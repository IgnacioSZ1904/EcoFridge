package com.uma.ecofridge.database

import androidx.room.*
import com.uma.ecofridge.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // Obtener todos los productos ordenados por fecha de caducidad (el requisito de prioridad)
    @Query("SELECT * FROM products ORDER BY expiryDate ASC")
    fun getAllProducts(): Flow<List<Product>>

    // Insertar un nuevo producto. Si ya existe, lo reemplaza (útil para ediciones)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Actualizar un producto existente
    @Update
    suspend fun updateProduct(product: Product)

    // Borrar un producto específico
    @Delete
    suspend fun deleteProduct(product: Product)

    // Consulta personalizada para buscar por nombre (ejemplo de flexibilidad)
    @Query("SELECT * FROM products WHERE name LIKE :searchName")
    suspend fun findByName(searchName: String): List<Product>
}