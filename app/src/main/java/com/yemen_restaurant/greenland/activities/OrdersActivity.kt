package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.OrderModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.SharedOrderStatus
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.OrderStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrdersActivity : ComponentActivity() {
    private val stateController = StateController()
    private val orders = mutableStateOf<List<OrderModel>>(listOf())
    val requestServer = RequestServer(this)
    val ordersStorage = OrderStorage()
    private fun readOrders() {
        stateController.isLoadingRead.value = true
        val data3 = buildJsonObject {
            put("tag", "read")
            put("orderBy", "name")
            put("orderType", "ASC")
            put("from", orders.value.size)

        }
        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString())
            .addFormDataPart("data2", requestServer.getData2())
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.ordersUrl, { _, it ->
            stateController.errorStateRead(it)
        }) {
             orders.value =
                    MyJson.IgnoreUnknownKeys.decodeFromString(
                        it
                    )
            ordersStorage.setOrders(it)
             stateController.successState()
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfNeedUpdate()
        setContent {
            GreenlandRestaurantTheme {
                val topBarHeight = 70.dp

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        cCompose.topBar(topBarHeight = topBarHeight, this,"طلباتي السابقة")
                    },
                    content = {
                        MainCompose1(
                            padding = topBarHeight,
                            stateController = stateController,
                            activity = this,
                            read = { readOrders() }) {
                            if (orders.value.isEmpty()) {
                                Column(
                                    Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,

                                    ) {
                                    Text(text = "لا يوجد طلبات سابقة")
                                }

                            } else
                                LazyVerticalGrid(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    columns = GridCells.Fixed(1),
                                    content = {
                                        itemsIndexed(orders.value) { _, s ->
                                            
                                            Column(
                                                Modifier.fillMaxWidth()
                                            ) {
                                                Card(Modifier.padding(10.dp)) {
                                                    Row {
                                                        Text(text = "رقم الطلب: ")
                                                        Text(text = s.id)
                                                    }
                                                    Row {
                                                        val formattedDateTime =
                                                s.createdAt.replace("\\s".toRegex(), "T")
                                            val date = (LocalDateTime.parse(formattedDateTime))

                                                        Text(text = "تاريخ الطلب")
                                                        Text(text = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                    .toString()+ " " + date.format(DateTimeFormatter.ofPattern("h:m:s"))
                                                            .toString())
                                                    }

                                                    Spacer(Modifier.height(20.dp))
                                                    OutLinedButton(
                                                        text = "عرض الطلب"
                                                    ) {    val intent = Intent(
                                                        this@OrdersActivity,
                                                        OrderStatusActivity::class.java
                                                    )
                                                        intent.putExtra(
                                                            "order_id",
                                                            s.id
                                                        )
                                                        startActivity(intent)}
//
                                                    Spacer(Modifier.height(20.dp))

                                                    HorizontalDivider()

                                                    Row(
                                                        Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(text = "حالة الطلب")
                                                        when (s.situationId) {
                                                            SharedOrderStatus.ORDER_COMPLETED -> Text(
                                                                text = "تم انجاز الطلب",
                                                                color = MaterialTheme.colorScheme.primary
                                                            )

                                                            SharedOrderStatus.ORDER_CENCELED -> Text(
                                                                text = "تم الغاء الطلب",
                                                                color = Color.Red
                                                            )

                                                            else -> Text(text = "قيد المعالجة")
                                                        }
//                                                        IconButton(onClick = {
//                                                            val intent = Intent(
//                                                                this@OrdersActivity,
//                                                                OrderStatusActivity::class.java
//                                                            )
//                                                            intent.putExtra(
//                                                                "order_id",
//                                                                s.id
//                                                            )
//                                                            startActivity(intent)
//                                                        }) {
//                                                            Icon(
//                                                                imageVector = Icons.Default.Info,
//                                                                contentDescription = ""
//                                                            )
//                                                        }
                                                    }

                                                    Spacer(Modifier.height(20.dp))
                                                }
                                            }
                                        }
                                    })

                        }
                    },

                    )

            }
        }
    }
    fun checkIfNeedUpdate() {
        if (processStoredProducts()) return
        readOrders()
    }
    private fun processStoredProducts(): Boolean {

        if (ordersStorage.isSetOrders()) {
            val diff =
                Duration.between(ordersStorage.getOrdersDate(), getCurrentDate()).toMinutes()
            if (diff <= 3) {
                orders.value = ordersStorage.getOrders()
                stateController.successState()
                return true
            }
        }
        return false
    }
}