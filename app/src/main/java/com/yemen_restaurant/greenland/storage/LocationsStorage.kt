package com.yemen_restaurant.greenland.storage

import GetStorage
import com.yemen_restaurant.greenland.activities.getCurrentDate
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.models.OrderModel
import com.yemen_restaurant.greenland.models.OrderStatus
import com.yemen_restaurant.greenland.models.UserLocationModel
import com.yemen_restaurant.greenland.shared.MyJson
import java.time.LocalDateTime

class LocationsStorage {
    private val getStorage = GetStorage("locations")
    private val statustKey = "location"
    private val statusDateKey = "locationKey"


fun isSetLocations():Boolean{
//       return getStorage.getData(homeComponentKey).isNotEmpty()
    return try {
        getLocations()
        true
    }catch (e:Exception){
        setLocations("")
        false
    }
}
    fun setLocations(data:String){
        getStorage.setData(statusDateKey, getCurrentDate().toString())
        getStorage.setData(statustKey,data)
    }

    fun getLocationsDate(): LocalDateTime? {
        return (LocalDateTime.parse(getStorage.getData(statusDateKey)))
    }
    fun getLocations():List<UserLocationModel>{
        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(statustKey))
    }
}