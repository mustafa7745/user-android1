package com.yemen_restaurant.greenland.storage

import GetStorage
import com.yemen_restaurant.greenland.activities.getCurrentDate
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.models.OrderModel
import com.yemen_restaurant.greenland.models.OrderStatus
import com.yemen_restaurant.greenland.shared.MyJson
import java.time.LocalDateTime

class OrderStorage {
    private val getStorage = GetStorage("orders")
    private val statustKey = "orders1"
    private val statusDateKey = "orders1Key"


fun isSetOrders():Boolean{
//       return getStorage.getData(homeComponentKey).isNotEmpty()
    return try {
        getOrders()
        true
    }catch (e:Exception){
        setOrders("")
        false
    }
}
    fun setOrders(data:String){
        getStorage.setData(statusDateKey, getCurrentDate().toString())
        getStorage.setData(statustKey,data)
    }

    fun getOrdersDate(): LocalDateTime? {
        return (LocalDateTime.parse(getStorage.getData(statusDateKey)))
    }
    fun getOrders():List<OrderModel>{
        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(statustKey))
    }
}