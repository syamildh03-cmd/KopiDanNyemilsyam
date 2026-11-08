package com.example.kopidannyemilsyam

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val rating: Double,
    val imageRes: Int,
    val emoji: String = "☕"
)
