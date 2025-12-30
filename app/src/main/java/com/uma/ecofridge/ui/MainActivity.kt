package com.uma.ecofridge.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.uma.ecofridge.R
import com.uma.ecofridge.database.AppDatabase
import com.uma.ecofridge.databinding.ActivityMainBinding
import com.uma.ecofridge.model.Product
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
        val adapter = ProductAdapter(
            onDeleteClick = { product ->
            mostrarDialogoBorrar(product)
        },
            onEditClick = { product ->
                val intent = Intent(this, AddProductActivity::class.java)
                // Pasamos los datos actuales para que los campos se rellenen solos
                intent.putExtra("extra_id", product.id)
                intent.putExtra("extra_name", product.name)
                intent.putExtra("extra_quantity", product.quantity)
                intent.putExtra("extra_expiry", product.expiryDate)
                startActivity(intent)
            }
        )
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
        // 1. Botón flotante para AÑADIR
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

        // 2. Botón de AJUSTES
        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarDialogoBorrar(product: Product) {
        android.app.AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(getString(R.string.dialog_delete_msg, product.name))

            .setPositiveButton(R.string.text_yes) { dialog, _ ->
                viewModel.delete(product)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.text_no) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}