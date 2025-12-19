package com.uma.ecofridge.repository

import com.uma.ecofridge.database.ProductDao
import com.uma.ecofridge.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    // Room detecta cambios automáticamente y emite la nueva lista.
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    // Las operaciones de escritura deben ser suspendidas para ejecutarse en hilos de fondo.
    suspend fun insert(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun update(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun delete(product: Product) {
        productDao.deleteProduct(product)
    }
}