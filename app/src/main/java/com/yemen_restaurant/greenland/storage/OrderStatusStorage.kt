package com.yemen_restaurant.greenland.storage

import GetStorage
import com.yemen_restaurant.greenland.activities.getCurrentDate
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.models.OrderContentWithDeliveryModel
import com.yemen_restaurant.greenland.models.OrderModel
import com.yemen_restaurant.greenland.models.OrderStatus
import com.yemen_restaurant.greenland.shared.MyJson
import java.time.LocalDateTime

class OrderStatusStorage {
    private val getStorage = GetStorage("orderStatus")
    private val statustKey = "status"
    private val statusDateKey = "statusKey"


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
    fun getOrders():OrderContentWithDeliveryModel{
        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(statustKey))
    }
}