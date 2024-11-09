package com.yemen_restaurant.greenland.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.yemen_restaurant.greenland.activities.getCurrentDate
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.models.ProductModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.HomeComponentStorage
import com.yemen_restaurant.greenland.storage.ProductsStorage
import com.yemen_restaurant.greenland.storage.UserStorage
import com.yemen_restaurant.greenland.synclist.Category
import com.yemen_restaurant.greenland.synclist.convertToCategoryStructure
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.Duration

class HomeComponentViewModel: ViewModel() {
    val stateController = StateController()
    lateinit var homeComponent: HomeComponent
    lateinit var productsStorage : ProductsStorage
    lateinit var cats : List<Category>
    //
    val userStorage = UserStorage()
    val homeComponentStorage = HomeComponentStorage()

    fun read(requestServer: RequestServer,goToAddName : () -> Unit = {}) {
        stateController.startRead()
        var data3: JsonObject
        var body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (userStorage.isSetUser()) {
            data3 = buildJsonObject {
                put("tag", "read")
            }

        } else {
            body1.addFormDataPart("data1", requestServer.getData1().toString())
                .addFormDataPart("data2", requestServer.getData2())
            data3 = buildJsonObject {
                put("tag", "readWithUser2")
            }

        }


        body1.addFormDataPart("data3", data3.toString()).build()

        val requestBody = body1.build()
        requestServer.request2(requestBody, Urls.homeUrl, { code, it ->
           stateController.errorStateRead(it)

        }) {
            val myHomeComponent = MyJson.IgnoreUnknownKeys.decodeFromString<HomeComponent>(it)
            if (myHomeComponent.user != null) {
                userStorage.setUser(MyJson.IgnoreUnknownKeys.encodeToString(myHomeComponent.user))
            }else{
                myHomeComponent.user = userStorage.getUser()
            }
            homeComponent = myHomeComponent

            productsStorage.deleteProducts()
           homeComponent.products.forEach {
                productsStorage.addProduct(it)
            }


            val homeComponentInLiteStorage = HomeComponent(homeComponent.user, homeComponent.ads,homeComponent.discounts,homeComponent.offers,homeComponent.categories,
                listOf()
            )

            homeComponentStorage.setHomeComponent(MyJson.IgnoreUnknownKeys.encodeToString(homeComponentInLiteStorage))
            initCategories()
            if (homeComponent.user!!.name2 == null){
                goToAddName()
            }
        }
    }
    fun initCategories() {
        cats = convertToCategoryStructure(homeComponent.categories, getInMainProducts())
        stateController.successState()
    }
    private fun getInMainProducts(): ArrayList<ProductModel> {
        val newList = homeComponent.products.groupBy { it.products_groupsName }

        val newList2 = arrayListOf<ProductModel>()
        newList.forEach {
            if (it.key == "الرئيسية") {
                newList2.addAll(it.value)
            } else {
                newList2.add(it.value.first())
            }
        }
        return newList2
    }
    fun checkIfNeedUpdate( requestServer: RequestServer,goToAddName: () -> Unit = {}) {
        if (processStoredProducts()) return
        read(requestServer, goToAddName = {goToAddName()})
    }
    private fun processStoredProducts(): Boolean {
        if (homeComponentStorage.isSetHomeComponent()) {
            val diff =
                Duration.between(homeComponentStorage.getDate(), getCurrentDate()).toMinutes()
            if (diff <= 5) {
                Log.e("procssed", diff.toString())
                homeComponent = homeComponentStorage.getHomeComponent()
                homeComponent.products = productsStorage.getProducts();
                initCategories()
                return true
            }
        }
        return false
    }
}