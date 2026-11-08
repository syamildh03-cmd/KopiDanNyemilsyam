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

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivCartItemImage)
        val tvName: TextView = itemView.findViewById(R.id.tvCartItemName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCartItemPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvCartQuantity)
        val btnMinus: Button = itemView.findViewById(R.id.btnCartMinus)
        val btnPlus: Button = itemView.findViewById(R.id.btnCartPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.ivImage.setImageResource(cartItem.product.imageRes)
        holder.tvName.text = cartItem.product.name

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val itemTotal = cartItem.product.price * cartItem.quantity
        holder.tvPrice.text = formatter.format(itemTotal)
        holder.tvQuantity.text = cartItem.quantity.toString()

        holder.btnMinus.setOnClickListener {
            onQuantityChanged(cartItem, -1)
        }

        holder.btnPlus.setOnClickListener {
            onQuantityChanged(cartItem, 1)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
