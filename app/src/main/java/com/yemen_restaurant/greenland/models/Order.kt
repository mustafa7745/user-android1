package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderModel (
    val id: String,
    val userId: String,
    var code:String?,
    val situationId: String,
    val paid:String?,
    val inrest:String?,
    val createdAt: String,
    val updatedAt: String,
)