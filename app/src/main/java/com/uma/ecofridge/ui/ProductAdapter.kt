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

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val current = products[position]
        // Obtenemos el contexto desde la vista para poder usar getString()
        val context = holder.itemView.context

        holder.tvName.text = current.name

        // Usamos el recurso R.string.card_quantity y le pasamos el número
        holder.tvQuantity.text = context.getString(R.string.card_quantity, current.quantity)

        // Primero formateamos la fecha como String
        val dateString = dateFormat.format(Date(current.expiryDate))
        // Luego la insertamos en la plantilla de texto traducible
        holder.tvDate.text = context.getString(R.string.card_expires, dateString)

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