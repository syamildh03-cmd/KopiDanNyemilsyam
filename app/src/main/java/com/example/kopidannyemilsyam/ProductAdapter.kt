package com.example.kopidannyemilsyam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var products: List<Product>,
    private val onAddClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvRating: TextView = itemView.findViewById(R.id.tvProductRating)
        val tvDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val btnAdd: Button = itemView.findViewById(R.id.btnAddProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.ivImage.setImageResource(product.imageRes)
        holder.tvName.text = product.name
        holder.tvRating.text = product.rating.toString()
        holder.tvDescription.text = product.description

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvPrice.text = formatter.format(product.price)

        holder.btnAdd.setOnClickListener {
            onAddClick(product)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }
}
