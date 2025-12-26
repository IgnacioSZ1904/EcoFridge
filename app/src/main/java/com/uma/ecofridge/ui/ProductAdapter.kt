package com.uma.ecofridge.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.uma.ecofridge.R
import com.uma.ecofridge.model.Product
import java.text.SimpleDateFormat
import java.util.*

class ProductAdapter(
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products = emptyList<Product>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // El ViewHolder es el contenedor de las vistas de cada fila.
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvDate: TextView = itemView.findViewById(R.id.tvExpiryDate)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    // Se llama cuando el RecyclerView necesita una nueva fila (molde).
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    // Se llama para "rellenar" los datos de una fila específica.
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = products[position]
        holder.tvName.text = current.name
        holder.tvQuantity.text = "Cantidad: ${current.quantity}"

        // Convertimos el Long (Timestamp) de la BD a una fecha legible.
        holder.tvDate.text = "Caduca: ${dateFormat.format(Date(current.expiryDate))}"

        // Configuración del botón de eliminar.
        holder.btnDelete.setOnClickListener {
            onDeleteClick(current)
        }
    }

    override fun getItemCount() = products.size

    // Función para actualizar la lista de forma eficiente.
    fun setProducts(newProducts: List<Product>) {
        this.products = newProducts
        notifyDataSetChanged()
    }
}