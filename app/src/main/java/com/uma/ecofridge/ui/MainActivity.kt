package com.uma.ecofridge.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.uma.ecofridge.database.AppDatabase
import com.uma.ecofridge.databinding.ActivityMainBinding
import com.uma.ecofridge.repository.ProductRepository
import com.uma.ecofridge.ui.viewmodel.ProductViewModel
import com.uma.ecofridge.ui.viewmodel.ProductViewModelFactory

class MainActivity : AppCompatActivity() {

    // 1. ViewBinding para acceder a los componentes del XML sin usar findViewById
    private lateinit var binding: ActivityMainBinding

    // 2. Inicialización del ViewModel mediante la Factoría y el Repositorio
    private val viewModel: ProductViewModel by viewModels {
        val database = AppDatabase.getDatabase(this)
        val repository = ProductRepository(database.productDao())
        ProductViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout usando ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupNavigation()
    }

    private fun setupRecyclerView() {
        val adapter = ProductAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Observar los datos (Reactividad)
        // Cada vez que la base de datos cambie, este bloque se ejecutará automáticamente
        viewModel.allProducts.observe(this) { products ->
            products?.let {
                adapter.setProducts(it)
            }
        }
    }

    private fun setupNavigation() {
        // Navegar a la actividad de añadir producto al pulsar el botón flotante
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }
}