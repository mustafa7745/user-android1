package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class ProductModel (
    val id: String,
    val name: String,
    val prePrice: String,
    val postPrice: String,
    val categoryId: String,
    val isAvailable:String,
    val products_groupsId:String,
    val products_groupsName:String,
    val description:String?,
    val productImages: List<ProductImageModel>

)