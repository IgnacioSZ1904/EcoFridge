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
import java.util.*

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private var selectedExpiryDate: Long = 0

    private val viewModel: ProductViewModel by viewModels {
        val database = AppDatabase.getDatabase(this)
        val repository = ProductRepository(database.productDao())
        ProductViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDatePicker.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { saveProduct() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, { _, year, month, day ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, day)
            selectedExpiryDate = selectedCalendar.timeInMillis
            binding.tvSelectedDate.text = "Caduca el: $day/${month + 1}/$year"
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString()
        val quantityStr = binding.etQuantity.text.toString()

        // 1. Validación de datos (Ingeniería de robustez)
        if (name.isBlank() || quantityStr.isBlank() || selectedExpiryDate == 0L) {
            Snackbar.make(binding.root, "Por favor, rellena todos los campos", Snackbar.LENGTH_LONG).show()
            return
        }

        val product = Product(
            name = name,
            category = "General", // Podrás ampliar esto con un Spinner más adelante
            expiryDate = selectedExpiryDate,
            quantity = quantityStr.toInt()
        )

        // 2. Inserción asíncrona mediante ViewModel
        viewModel.insert(product)

        // 3. Feedback al usuario y cierre
        Snackbar.make(binding.root, "Producto guardado", Snackbar.LENGTH_SHORT).show()

        // Finalizamos la actividad para volver a la MainActivity
        finish()
    }
}