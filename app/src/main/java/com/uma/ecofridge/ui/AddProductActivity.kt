package com.uma.ecofridge.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.uma.ecofridge.database.AppDatabase
import com.uma.ecofridge.databinding.ActivityAddProductBinding
import com.uma.ecofridge.model.Product
import com.uma.ecofridge.repository.ProductRepository
import com.uma.ecofridge.ui.viewmodel.ProductViewModel
import com.uma.ecofridge.ui.viewmodel.ProductViewModelFactory
import com.uma.ecofridge.R
import java.util.*

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private var selectedExpiryDate: Long = 0
    private var currentProductId: Int = 0

    private val viewModel: ProductViewModel by viewModels {
        val database = AppDatabase.getDatabase(this)
        val repository = ProductRepository(database.productDao())
        ProductViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. CHEQUEO: ¿Me han pasado datos para editar?
        if (intent.hasExtra("extra_id")) {
            // ¡Sí! Estamos en MODO EDICIÓN
            title = "Editar Producto" // Cambiamos el título de la barra superior
            binding.btnSave.text = "Actualizar" // Opcional: cambiar texto del botón

            // Recuperamos los datos
            currentProductId = intent.getIntExtra("extra_id", 0)
            val name = intent.getStringExtra("extra_name")
            val quantity = intent.getIntExtra("extra_quantity", 1)
            selectedExpiryDate = intent.getLongExtra("extra_expiry", 0)

            // Rellenamos los campos visuales
            binding.etProductName.setText(name)
            binding.etQuantity.setText(quantity.toString())

            // Mostramos la fecha recuperada
            if (selectedExpiryDate != 0L) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedExpiryDate
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)
                // Usa getString con R.string.selected_date_format si lo tienes traducido
                binding.tvSelectedDate.text = "$day/$month/$year"
            }
        }
        binding.btnDatePicker.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { saveProduct() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, { _, year, month, day ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, day)
            selectedExpiryDate = selectedCalendar.timeInMillis
            binding.tvSelectedDate.text = getString(R.string.caduca_texto, day, month + 1, year)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString()
        val quantityStr = binding.etQuantity.text.toString()

        // 1. Validación de datos (Ingeniería de robustez)
        if (name.isBlank() || quantityStr.isBlank() || selectedExpiryDate == 0L) {
            Snackbar.make(binding.root, R.string.error_rellenar_campos, Snackbar.LENGTH_LONG).show()
            return
        }

        val product = Product(
            id=currentProductId,
            name = name,
            category = "General", // Podrás ampliar esto con un Spinner más adelante
            expiryDate = selectedExpiryDate,
            quantity = quantityStr.toInt()
        )
        if(currentProductId==0){
            // 2. Inserción asíncrona mediante ViewModel
            viewModel.insert(product)
        }else{
            viewModel.update(product)
        }

        // 3. Feedback al usuario y cierre
        Snackbar.make(binding.root, R.string.producto_guardado, Snackbar.LENGTH_SHORT).show()

        // Finalizamos la actividad para volver a la MainActivity
        finish()
    }
}