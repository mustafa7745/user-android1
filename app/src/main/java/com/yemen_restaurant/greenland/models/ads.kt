package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class AdsModel (
    val id: String,
    val description: String,
    val image: String,
    val type : String?,
    val product_cat_id : String?,
    val createdAt: String,
    val updatedAt: String
)