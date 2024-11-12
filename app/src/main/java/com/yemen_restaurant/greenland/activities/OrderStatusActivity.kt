package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.play.core.review.ReviewManagerFactory
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.application.MyApplication
import com.yemen_restaurant.greenland.models.OrderContentDeliveryModel
import com.yemen_restaurant.greenland.models.OrderContentModel
import com.yemen_restaurant.greenland.models.OrderContentWithDeliveryModel
import com.yemen_restaurant.greenland.models.OrderModel
import com.yemen_restaurant.greenland.models.OrderStatus
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.SharedOrderINRest
import com.yemen_restaurant.greenland.shared.SharedOrderPaid
import com.yemen_restaurant.greenland.shared.SharedOrderStatus
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.OrderStatusStorage
import com.yemen_restaurant.greenland.storage.OrderStorage
import com.yemen_restaurant.greenland.storage.ReviewStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class OrderStatusActivity : ComponentActivity() {
    val stateController = StateController()
//    private val orderStatus = mutableStateOf<List<OrderStatus>>(listOf())
    private var orderContentWithDeliveryModel = mutableStateOf<OrderContentWithDeliveryModel?>(null)
//    private lateinit var orderContents: OrderContentModel
    val orderStatusStorage = OrderStatusStorage()
    val isRefresh = mutableStateOf(false)

    val requestServer = RequestServer(this)
    private lateinit var orderId: String
    val reviewStorage = ReviewStorage()
//    private fun read() {
//        stateController.startRead()
//        val data3 = buildJsonObject {
//            put("tag", "readOrderStatus")
//            put("inputOrderId", orderId)
//        }
//        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart("data1", requestServer.getData1().toString())
//            .addFormDataPart("data2", requestServer.getData2())
//            .addFormDataPart("data3", data3.toString())
//            .build()
//
//        requestServer.request2(body1, Urls.ordersUrl, { _, it ->
//            stateController.errorStateRead(it)
//        }) {
//
//            orderStatus.value =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    it
//                )
//            orderStorage.setStatus(it)
//            stateController.successState()
//        }
//    }

    private fun read() {
        stateController.startRead()
        val data3 = buildJsonObject {
            put("tag", "readOrderContent")
            put("inputOrderId", orderId)
        }
        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString())
            .addFormDataPart("data2", requestServer.getData2())
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.ordersUrl, { _, it ->
            stateController.errorStateRead(it)
        }) {

            orderContentWithDeliveryModel.value =  MyJson.IgnoreUnknownKeys.decodeFromString(
                it
            )
            orderStatusStorage.setOrders(it)
            isRefresh.value = false
//            if (orderContentWithDeliveryModel != null){
//                orderStatus.value = orderContentWithDeliveryModel!!.orderStatus
//            }
//            orderStorage.setStatus(it)
            stateController.successState()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        check()
        val pageList = listOf(
            OrderPageModel("المنتجات", 0),
            OrderPageModel("حالة الطلب", 1),
            OrderPageModel("موصل الطلب", 2),
            OrderPageModel("رمز التسليم", 3),
        )

        setContent {
            GreenlandRestaurantTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
//                    topBar = {
//                        cCompose.topBar(topBarHeight = topBarHeight, this)
//                    },
                    content = {
                        MainCompose1(
                            padding = 0.dp,
                            stateController = stateController,
                            activity = this,
                            read = { read() })
                        {


                            OrderDetails(orderContentWithDeliveryModel.value!!.order)

                            val pagerState =
                                rememberPagerState(pageCount = { pageList.size })
                            TabRow(pagerState.currentPage) {
                                val coroutineScope = rememberCoroutineScope()
                                pageList.map { item ->
                                    Tab(
                                        modifier = Modifier.padding(3.dp),
                                        selected = item.pageId == pagerState.currentPage,
                                        onClick = {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(item.pageId)
                                            }
                                        },
                                        text = {
                                            Text(
                                                modifier = Modifier.padding(3.dp),
                                                textAlign = TextAlign.Start,
                                                text = item.pageName,
                                                fontFamily = FontFamily(
                                                    Font(R.font.bukra_bold)
                                                ),
                                                fontSize = 10.sp,
                                                color = if (item.pageId == pagerState.currentPage) MaterialTheme.colorScheme.primary else Color.Black
                                            )
                                        }
                                    )
                                }
                            }
//                            Row (
//                                Modifier.fillMaxWidth()
//                            ){
////                                pageList.map { item ->
////                                    Tab(
////                                        modifier= Modifier.padding(3.dp).height(80.dp),
////                                        selected = item.pageId == pagerState.currentPage,
////                                        onClick = { },
////                                        text = {
////                                            Text(
////                                                modifier = Modifier.padding(3.dp),
////                                                textAlign = TextAlign.Start,
////                                                text = item.pageName,
////                                                fontFamily = FontFamily(
////                                                    Font(R.font.bukra_bold)
////                                                ),
////                                                fontSize = 12.sp,
////                                                color = if (item.pageId == pagerState.currentPage) MaterialTheme.colorScheme.primary else Color.Black
////                                            )
////
////                                        }
////                                    )
////                                }
//                            }
                            HorizontalPager(
                                pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { item ->
                                SwipeRefresh(
                                    modifier = Modifier.fillMaxSize(),
                                    state = rememberSwipeRefreshState(isRefresh.value),
                                    onRefresh = {
                                        refresh()

                                    }){
                                    Column(
                                        Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ){
                                        if (pageList.find { 0 == item } != null) {
                                            OrderProductContentPage()
                                        } else if (pageList.find { 1 == item } != null) {
                                            OrderStatusPage()
                                        } else if (pageList.find { 2 == item } != null) {
                                            if (orderContentWithDeliveryModel.value!!.deliveryMan != null){
                                                if (orderContentWithDeliveryModel.value!!.order.situationId != SharedOrderStatus.ORDER_COMPLETED && orderContentWithDeliveryModel.value!!.order.situationId != SharedOrderStatus.ORDER_CENCELED){
                                                    Text("معلومات مندوب التوصيل")
                                                    Spacer(Modifier.height(10.dp))
                                                    Text(orderContentWithDeliveryModel.value!!.deliveryMan!!.name)
                                                    Spacer(Modifier.height(10.dp))
                                                    Text(orderContentWithDeliveryModel.value!!.deliveryMan!!.phone)
                                                }

                                            }
                                        } else {

                                            if(orderContentWithDeliveryModel.value!!.order.code != null){
                                                Text( orderContentWithDeliveryModel.value!!.order.code!!)
                                            }
                                            else{
                                                Text( "لم يتم توليد رمز التسليم بعد")
                                                OutLinedButton(text = "تحديث") {
                                                   refresh()
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
//                            OrderStatusPage()

//                            // Define weights for columns
//                            val column0Weight = 0.07f // 30%
//                            val column1Weight = 0.36f // 30%
//                            val column2Weight = 0.27f // 70%
//                            val column3Weight = 0.3f // 30%
//
//                            LazyVerticalGrid(
//                                horizontalArrangement = Arrangement.Center,
//                                modifier = Modifier.fillMaxWidth(),
//                                columns = GridCells.Fixed(1),
//                                content = {
//                                    item {
//                                        Row(Modifier.background(Color.Gray)) {
//                                            TableCell(text = "#", weight = column0Weight)
//                                            TableCell(text = "الحاله", weight = column1Weight)
//                                            TableCell(text = "الوقت", weight = column2Weight)
//                                            TableCell(text = "التاريخ", weight = column3Weight)
//                                        }
//                                    }
//                                    itemsIndexed(orderStatus.value) { index, s ->
//                                        Row(Modifier.fillMaxWidth()) {
//                                            TableCell(
//                                                text = (index + 1).toString(),
//                                                weight = column0Weight
//                                            )
//                                            TableCell(
//                                                text = s.situation,
//                                                weight = column1Weight
//                                            )
//                                            val formattedDateTime =
//                                                s.createdAt.replace("\\s".toRegex(), "T")
//                                            val date = (LocalDateTime.parse(formattedDateTime))
//                                            TableCell(
//                                                text = date.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//                                                    .toString(),
//                                                weight = column2Weight
//                                            )
//
//                                            TableCell(
//                                                text = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//                                                    .toString(),
//                                                weight = column3Weight
//                                            )
//                                        }
//                                    }
//                                })
//                        }
                    },
                )
            }
        }

    }

    private fun refresh() {
        lifecycleScope.launch {
            isRefresh.value = true
            delay(2000)
            checkIfNeedUpdate()
        }
    }

    @Composable
    fun RowScope.TableCell(
        text: String,
        weight: Float
    ) {
        Text(
            modifier = Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            text = text,
            fontSize = 10.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }

    @Composable
    fun OrderDetails(order: OrderModel) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp) // تم تعديل الارتفاع ليتناسب مع المحتوى
                .padding(16.dp)
                .background(Color(0xFFF2F2F2)) // خلفية رمادية فاتحة
                .clip(RoundedCornerShape(12.dp)) // حواف مستديرة
        ) {
            // عمود النصوص الجانبية
            Column(
            ) {
                Text("رقم الطلب", fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp)) // مسافة بين العناصر
                Text("طريقة الدفع: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp)) // مسافة بين العناصر
                Text("حالة الدفع: ", fontWeight = FontWeight.Bold,fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp)) // مسافة بين العناصر
                Text("عنوان التسليم", fontWeight = FontWeight.Bold,fontSize = 12.sp, color = Color.Gray)
            }

            // عمود النصوص المتغيرة حسب قيمة الطلب
            Column(
                Modifier.padding(start = 8.dp)
            ) {
                // عرض رقم الطلب
                Text(order.id, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp)) // مسافة بين العناصر

                // تحديد طريقة الدفع بناءً على الحالة
                val paymentMethod = when (order.paid) {
                    SharedOrderPaid.ELECTEONIC_PAID -> "دفع الكتروني"
                    SharedOrderPaid.PAID_FROM_WALLET -> "دفع من المحفظة"
                    SharedOrderPaid.PAID_IN_STORE -> "دفع نقدي في المطعم"
                    else -> "عند تسليم الطلب"
                }
                Text(paymentMethod,fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp)) // مسافة بين العناصر

                // حالة الدفع
                val paymentStatus = when (order.paid) {
                    SharedOrderPaid.PAID_ON_DELIVERY,
                    SharedOrderPaid.ELECTEONIC_PAID,
                    SharedOrderPaid.PAID_FROM_WALLET,
                    SharedOrderPaid.PAID_IN_STORE -> "تم الدفع"
                    else -> "لم يتم الدفع بعد"
                }
                Text(paymentStatus,fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp)) // مسافة بين العناصر

                // تحديد طريقة التسليم بناءً على الخيار
                val deliveryMethod = when (order.inrest) {
                    SharedOrderINRest.CAR -> "الى السيارة"
                    SharedOrderINRest.SAFARY -> "سفري"
                    SharedOrderINRest.MAHALY -> "محلي"
                    SharedOrderINRest.FAMILY -> "محلي عوائل"
                    else -> "الى العنوان المحدد"
                }
                Text(deliveryMethod,fontSize = 12.sp)
            }
        }
    }



    @Composable
    private fun OrderStatusPage() {



                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    LazyColumn(
                        Modifier.padding(16.dp),

                        ) {
                        //                                itemsIndexed(orderStatus.value){index,it->
                        //
                        //                                }
                        item {
                            Row {
                                val enabled =
                                    Icon(
                                        modifier = Modifier.padding(5.dp),
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = "",
                                        tint = if (orderContentWithDeliveryModel.value!!.orderStatus.find { it.situationId == SharedOrderStatus.ORDER_VIEWED } != null) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                Text("الاطلاع على الطلب")
                            }
                            VerticalDivider(Modifier.height(50.dp))
                            Row {
                                Icon(
                                    modifier = Modifier.padding(5.dp),
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = "",
                                    tint = if (orderContentWithDeliveryModel.value!!.orderStatus.find { it.situationId == SharedOrderStatus.ORDER_PREPARED } != null) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                                Text("تجهيز الطلب")
                            }

                            var item =
                                orderContentWithDeliveryModel.value!!.orderStatus.find { it.situationId == SharedOrderStatus.ASSIGN_DELIVERY_MAN }
                            if (item != null) {
                                VerticalDivider(Modifier.height(50.dp))
                                Row {
                                    Icon(
                                        modifier = Modifier.padding(5.dp),
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(item!!.situation)
                                }
                            }

                            VerticalDivider(Modifier.height(50.dp))
                            Row {
                                Icon(
                                    modifier = Modifier.padding(5.dp),
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = "",
                                    tint = if (orderContentWithDeliveryModel.value!!.orderStatus.find { it.situationId == SharedOrderStatus.ORDER_INWAY } != null) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                                Text("الطلب في الطرق اليك")
                            }
                            VerticalDivider(Modifier.height(50.dp))
                            Row {
                                Icon(
                                    modifier = Modifier.padding(5.dp),
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = "",
                                    tint = if (orderContentWithDeliveryModel.value!!.orderStatus.find { it.situationId == SharedOrderStatus.ORDER_COMPLETED } != null) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                                Text("تسليم الطلب")
                            }
                            item =
                                orderContentWithDeliveryModel.value!!.orderStatus.find { it.situationId == SharedOrderStatus.ORDER_CENCELED }
                            if (item != null) {
                                VerticalDivider(Modifier.height(50.dp))
                                Row {
                                    Icon(
                                        modifier = Modifier.padding(5.dp),
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = "",
                                        tint = Color.Red
                                    )
                                    Text(item.situation)
                                }
                            }
                        }
                    }
                }
    }
    @Composable
    private fun OrderProductContentPage() {
        val column0Weight = 0.07f // 30%
        val column1Weight = 0.41f // 30%
        val column2Weight = 0.15f // 70%
        val column3Weight = 0.15f // 30%
        val column4Weight = 0.22f // 70%

        LazyColumn(content =
        {
            // Header row
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "#", weight = column0Weight)
                    TableCell(text = "الصنف", weight = column1Weight)
                    TableCell(text = "عدد", weight = column2Weight)
                    TableCell(text = "السعر", weight = column3Weight)
                    TableCell(text = "الاجمالي", weight = column4Weight)
                }
            }

            itemsIndexed(orderContentWithDeliveryModel.value!!.orderContent.products) { index, s ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(
                        text = (index + 1).toString(),
                        weight = column0Weight
                    )
                    TableCell(
                        text = s.productName,
                        weight = column1Weight
                    )
                    TableCell(
                        text = s.productQuantity,
                        weight = column2Weight
                    )
                    TableCell(
                        text = formatPrice(s.productPrice),
                        weight = column3Weight
                    )
                    TableCell(
                        text = formatPrice((s.productPrice.toDouble() * s.productQuantity.toInt()).toString()),
                        weight = column4Weight
                    )
                }
            }
            itemsIndexed(orderContentWithDeliveryModel.value!!.orderContent.offers) { index, s ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(
                        text = (index + 1).toString(),
                        weight = column0Weight
                    )
                    TableCell(
                        text = s.offerName,
                        weight = column1Weight
                    )
                    TableCell(
                        text = s.offerQuantity,
                        weight = column2Weight
                    )
                    TableCell(
                        text = formatPrice(s.offerPrice),
                        weight = column3Weight
                    )
                    TableCell(
                        text = formatPrice((s.offerPrice.toDouble() * s.offerQuantity.toInt()).toString()),
                        weight = column4Weight
                    )
                }
            }
            item {

                if (orderContentWithDeliveryModel.value!!.orderContent.delivery != null)

                    Row(Modifier.fillMaxWidth()) {
                        TableCell(
                            text = (orderContentWithDeliveryModel.value!!.orderContent.products.size + orderContentWithDeliveryModel.value!!.orderContent.offers.size + 1).toString(),
                            weight = column0Weight
                        )
                        TableCell(
                            text = "توصيل الطلب",
                            weight = column1Weight
                        )
                        TableCell(
                            text = "1",
                            weight = column2Weight
                        )
                        TableCell(
                            text = orderContentWithDeliveryModel.value!!.orderContent.delivery!!.price,
                            weight = column3Weight
                        )
                        TableCell(
                            text = orderContentWithDeliveryModel.value!!.orderContent.delivery!!.price,
                            weight = column4Weight
                        )

                    }
                if (orderContentWithDeliveryModel.value!!.orderContent.discount != null) {
                    Row(Modifier.fillMaxWidth()) {
                        var size =
                            (orderContentWithDeliveryModel.value!!.orderContent.products.size + orderContentWithDeliveryModel.value!!.orderContent.offers.size + 1)
                        val discountTypeDescription =
                            if (orderContentWithDeliveryModel.value!!.orderContent.discount!!.type == "0") {
                                "خصم مئوي"  // Percentage Discount
                            } else {
                                "خصم رئيسي"  // Fixed Discount
                            }
                        if (orderContentWithDeliveryModel.value!!.orderContent.discount != null)
                            size++
                        TableCell(
                            text = size.toString(),
                            weight = column0Weight
                        )
                        TableCell(
                            text = discountTypeDescription,
                            weight = column1Weight
                        )
                        TableCell(
                            text = "1",
                            weight = column2Weight
                        )
                        TableCell(
                            text = orderContentWithDeliveryModel.value!!.orderContent.discount!!.amount,
                            weight = column3Weight
                        )
                        TableCell(
                            text = orderContentWithDeliveryModel.value!!.orderContent.discount!!.amount,
                            weight = column4Weight
                        )
                    }

                }
                Row(
                    Modifier
                        .background(Color.LightGray)
                        .clickable {
                            requestInAppReview()
                        }) {
                    TableCell(
                        text = "اجمالي الفاتورة",
                        weight = (column0Weight + column1Weight + column2Weight + column3Weight)
                    )

                    TableCell(
                        text = roundToNearestFifty(getAllFinalPrice().roundToInt()).toString(),
                        weight = column4Weight
                    )

                }
            }
        }
        )
    }
    private fun check() {
        val intent = intent
        val str1 = intent.getStringExtra("orderContent")
        val str2 = intent.getStringExtra("order_id")
        if (str1 != null) {
            orderContentWithDeliveryModel = MyJson.IgnoreUnknownKeys.decodeFromString(str1)
            stateController.successState()
            orderId = orderContentWithDeliveryModel.value!!.order.id
            Toast.makeText(this, "تم ارسال الطلب بنجاح", Toast.LENGTH_SHORT).show()
            if (!reviewStorage.isReview()) {
                reviewStorage.incrementCountOrder()
                val orderCount = reviewStorage.getCountOrder()
                if (orderCount > 3) {
                    requestInAppReview()
                }
            }
        } else if (str2 != null) {
            orderId = str2
            checkIfNeedUpdate()
        } else {
            finish()
        }
    }
    private fun requestInAppReview() {
        val manager = ReviewManagerFactory.create(this)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                if (reviewInfo != null) {
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // Mark that the review was requested
                        reviewStorage.setReview()
                    }
                } else {
                    // Handle the case where reviewInfo is null
                    Log.e("InAppReview", "ReviewInfo is null")
                }
            } else {
                // Handle the error case
                Log.e("InAppReview", "Failed to get review flow: ${task.exception?.message}")
            }
        }
    }
    private fun getAllFinalPrice(): Double {
        var sum = getProductsFinalPrice() + getOffersFinalPrice()

        orderContentWithDeliveryModel.value!!.orderContent.delivery?.let {
            sum += it.price.toDouble()
        }

        orderContentWithDeliveryModel.value!!.orderContent.discount?.let {
            val amount = it.amount
            when (it.type) {
                "0" -> { // Percentage discount
                    val discount = (sum * amount.toInt()) / 100
                    sum -= discount
                    sum = 50 * Math.round((sum / 50).toDouble()).toDouble()
                    println("Discount: $discount")
                }

                else -> { // Fixed amount discount
                    sum -= amount.toInt()
                }
            }
        }

        return sum
    }
    private fun getProductsFinalPrice(): Double {
        return orderContentWithDeliveryModel.value!!.orderContent.products.sumOf {
            it.productPrice.toDouble() * it.productQuantity.toInt()
        }
    }

    private fun getOffersFinalPrice(): Double {
        return orderContentWithDeliveryModel.value!!.orderContent.offers.sumOf {
            it.offerPrice.toDouble() * it.offerQuantity.toInt()
        }
    }

    fun checkIfNeedUpdate() {
        if (processStoredProducts()) return
        read()
    }
    private fun processStoredProducts(): Boolean {

        if (orderStatusStorage.isSetOrders()) {
            val diff =
                Duration.between(orderStatusStorage.getOrdersDate(), getCurrentDate()).toMinutes()
            if (diff <= 1) {
                orderContentWithDeliveryModel.value = orderStatusStorage.getOrders()
                orderId = orderContentWithDeliveryModel.value!!.order.id
                isRefresh.value = false
                stateController.successState()
                return true
            }
        }
        return false
    }
}

data class OrderPageModel(val pageName:String,val pageId:Int)