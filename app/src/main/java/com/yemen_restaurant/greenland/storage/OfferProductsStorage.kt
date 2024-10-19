package com.yemen_restaurant.greenland.storage

import GetStorage
import com.yemen_restaurant.greenland.activities.getCurrentDate
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.models.OfferProductsModel
import com.yemen_restaurant.greenland.shared.MyJson
import java.time.LocalDateTime

class OfferProductsStorage {
    private val getStorage = GetStorage("offerProducts")
    private val homeComponentKey = "offerProductsKey"
    private val dateKey = "dateKey"

    fun isSet():Boolean{
        return try {
            get()
            true
        }catch (e:Exception){
            set("")
            false
        }
    }
    fun set(data:String){
        getStorage.setData(dateKey, getCurrentDate().toString())
        getStorage.setData(homeComponentKey,data)
    }

    fun getDate(): LocalDateTime? {
        return (LocalDateTime.parse(getStorage.getData(dateKey)))
    }
    fun get():List<OfferProductsModel>{
        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(homeComponentKey))
    }
}