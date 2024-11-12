package com.yemen_restaurant.greenland.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderContentModel (
    val products: List<OrderContentProductsModel>,
    val offers:  List<OrderContentOffersModel>,
    var delivery:OrderContentDeliveryModel?,
    val discount: OrderContentDiscountModel?,
)
@Serializable
data class OrderContentWithDeliveryModel (
    val order: OrderModel,
    val orderContent:  OrderContentModel,
    var orderStatus:List<OrderStatus>,
    val deliveryMan: DeliveryMan?,
)
@Serializable
data class DeliveryMan(val name:String,val phone:String)

@Serializable
data class OrderContentProductsModel (
    val id: String,
    val productId: String,
    val orderId:String,
    val productName: String,
    val productPrice: String,
    val productQuantity:String,
    val createdAt: String,
    val updatedAt: String,
)
@Serializable
data class OrderContentOffersModel (
    val id: String,
    val offerId: String,
    val orderId:String,
    val offerName: String,
    val offerPrice: String,
    val offerQuantity:String,
    val createdAt: String,
    val updatedAt: String,
)
@Serializable
data class OrderContentDeliveryModel (
    val id: String,
    val price: String,
    val userLocationId: String,
)

@Serializable
data class OrderContentDiscountModel (
    val id: String,
    val type: String,
    val amount: String,
)
