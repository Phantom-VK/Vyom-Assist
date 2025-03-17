package com.swag.vyom.dataclasses

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val eligibilityCriteria: String,
    val interestRate: Double? = null,
    val benefits: List<String> = emptyList()
)
