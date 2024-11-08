package com.yemen_restaurant.greenland.storage

import GetStorage
import com.yemen_restaurant.greenland.activities.getCurrentDate
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.models.OrderStatus
import com.yemen_restaurant.greenland.shared.MyJson
import java.time.LocalDateTime

class OrderStorage {
    private val getStorage = GetStorage("orders")
    private val statustKey = "status"
    private val statusDateKey = "stdateKey"
//
    private val productsKey = "products"
    private val productsDateKey = "prdateKey"

    fun isSetProducts():Boolean{
//       return getStorage.getData(homeComponentKey).isNotEmpty()
        return try {
            getProducts()
            true
        }catch (e:Exception){
            setProducts("")
            false
        }
    }
    fun setProducts(data:String){
        getStorage.setData(productsDateKey, getCurrentDate().toString())
        getStorage.setData(productsKey,data)
    }

    fun getProductsDate(): LocalDateTime? {
       return (LocalDateTime.parse(getStorage.getData(productsDateKey)))
    }
    fun getProducts():HomeComponent{
       return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(productsKey))
    }
//
fun isSetStatus():Boolean{
//       return getStorage.getData(homeComponentKey).isNotEmpty()
    return try {
        getStatus()
        true
    }catch (e:Exception){
        setStatus("")
        false
    }
}
    fun setStatus(data:String){
        getStorage.setData(statusDateKey, getCurrentDate().toString())
        getStorage.setData(statustKey,data)
    }

    fun getStatusDate(): LocalDateTime? {
        return (LocalDateTime.parse(getStorage.getData(statusDateKey)))
    }
    fun getStatus():List<OrderStatus>{
        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(statustKey))
    }
}