package com.yemen_restaurant.greenland.storage

import GetStorage
import com.yemen_restaurant.greenland.activities.getCurrentDate
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.shared.MyJson
import java.time.LocalDateTime

class HomeComponentStorage {
    private val getStorage = GetStorage("homeComponent")
    private val homeComponentKey = "homeComponentKey"
    private val dateKey = "dateKey"

    fun isSetHomeComponent():Boolean{
//       return getStorage.getData(homeComponentKey).isNotEmpty()
        return try {
            getHomeComponent()
            true
        }catch (e:Exception){
            setHomeComponent("")
            false
        }
    }
    fun setHomeComponent(data:String){
        getStorage.setData(dateKey, getCurrentDate().toString())
        getStorage.setData(homeComponentKey,data)
    }

    fun getDate(): LocalDateTime? {
       return (LocalDateTime.parse(getStorage.getData(dateKey)))
    }
    fun getHomeComponent():HomeComponent{
       return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(homeComponentKey))
    }
}