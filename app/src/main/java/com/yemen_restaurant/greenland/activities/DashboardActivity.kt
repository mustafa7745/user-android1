package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.yemen_restaurant.greenland.shared.LoadingCompose
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.models.User
import com.yemen_restaurant.greenland.shared.CartController3
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.SharedInAppUpdate
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.HomeComponentStorage
import com.yemen_restaurant.greenland.storage.UserStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.Duration
import java.time.LocalDateTime

val cartController3 = CartController3()

class DashboardActivity : ComponentActivity() {
    private lateinit var homeComponent: HomeComponent
    val stateController = StateController()
    private val homeComponentStorage = HomeComponentStorage()
    private val userName = mutableStateOf("")
    val userStorage = UserStorage()

    val itemView = mutableStateOf(false)
    val itemType = mutableStateOf(0)


    val requestServer = RequestServer(this)


    private lateinit var updateName2ActivityResult: ActivityResultLauncher<Intent>

    private fun read() {

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
                val data = MyJson.IgnoreUnknownKeys.decodeFromString<HomeComponent>(it)
                if (data.user != null) {
                    userStorage.setUser(MyJson.IgnoreUnknownKeys.encodeToString(data.user))
                }else{
                    data.user = userStorage.getUser()
                }
                homeComponent = data
                homeComponentStorage.setHomeComponent(MyJson.IgnoreUnknownKeys.encodeToString(data))
                stateController.successState()
                if (homeComponent.user!!.name2 == null){
                    goToAddName()
                }
        }
    }

//    private fun successState() {
//        stateController.isLoadingRead.value = false
//        stateController.isSuccessRead.value = true
//        stateController.isErrorRead.value = false
//    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedInAppUpdate(this).checkUpdate()
        updateName2ActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val resultValue = data?.getStringExtra("user2")
                // استخدام النتيجة كما تشاء
                if (resultValue != null){
                    val user =  MyJson.IgnoreUnknownKeys.decodeFromString<User>(resultValue)
                    if (user.name2 != null){
                        userName.value = user.name2.toString()
                        userStorage.setUser(MyJson.IgnoreUnknownKeys.encodeToString(user))
                        homeComponent.user?.name2 = user.name2
                        homeComponentStorage.setHomeComponent(MyJson.IgnoreUnknownKeys.encodeToString(homeComponent))
                    }
                }

            }
        }
        checkIfNeedUpdate()
        setContent {
            GreenlandRestaurantTheme {


                                MainCompose1(padding = 0.dp, stateController = stateController, activity = this@DashboardActivity, read = { read() }){
                                    HeaderComponent()
                                    LazyColumn(content = {
                                        if (homeComponent.offers.isNotEmpty())
                                        OffersComponents()
                                        if (homeComponent.ads.isNotEmpty())
                                        AdsComponent()
                                        Categories()
                                    })

                                }

                        }


        }
    }


    private fun LazyListScope.Categories() {
        item {
            Text(text = "الاصناف")
        }
        items(homeComponent.categories.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // توزيع العناصر بالتساوي
            ) {
                rowItems.forEach { s ->
                    Card(
                        Modifier
                            .weight(1f)
                            .height(200.dp)
                            .width(200.dp)
                            .padding(5.dp)

                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .border(
                                    1.dp,
                                    Color.Black,
                                    RoundedCornerShape(
                                        16.dp
                                    )
                                )
                                .clip(
                                    RoundedCornerShape(
                                        16.dp
                                    )
                                )
                                .clickable {
                                    val intent = Intent(
                                        this@DashboardActivity,
                                        ProductsActivity::class.java
                                    )
                                    intent.putExtra("category_id", s.id)
                                    startActivity(intent)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                                    .align(Alignment.TopCenter)
                            ) {
                                CustomImageView(
                                    context = this@DashboardActivity,
                                    imageUrl = s.category_image_path + s.image,
                                    okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(MaterialTheme.colorScheme.primary),
                            ) {

                                Text(
                                    modifier = Modifier
                                        .align(Alignment.Center),
                                    text = s.name,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )

                            }
                        }


                    }
                }

            }
        }
    }

    private fun checkIfNeedUpdate() {
        if (homeComponentStorage.isSetHomeComponent()) {
            val diff = Duration.between(homeComponentStorage.getDate(), getCurrentDate()).toMinutes()
            if (diff > 1) {
                read()
            } else {
                homeComponent = homeComponentStorage.getHomeComponent()
                stateController.successState()
            }
        } else {
            read()
        }
    }

    @Composable
    private fun CategoriesComponent() {



//        HeaderComponent()


        if (itemView.value == true) {
            if (itemType.value == 1) {
                if (homeComponent.offers.isNotEmpty()) {
//                    OffersComponents()
                }
            }
            if (itemType.value == 2) {
                if (homeComponent.ads.isNotEmpty()){}
//                    AdsComponent()
            }
        }
        CategoriesComponents()
    }

    @Composable
    private fun HeaderComponent() {
        var count = 3
        if (homeComponent.ads.isNotEmpty()) count++
        if (homeComponent.offers.isNotEmpty()) count++
        if (homeComponent.discounts.isNotEmpty()) count++

        if (homeComponent.user != null)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animatedBorder(
                        borderColors = listOf(Color.Red, Color.Green, Color.Blue),
                        backgroundColor = Color.White,
                        shape = RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp),
                        borderWidth = 4.dp,
                        animationDurationInMillis = 5000
                    ),
            ) {
                Row(

                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp), model = R.drawable.user, contentDescription = null
                    )
                    userName.value =
                        if (homeComponent.user!!.name2 != null) homeComponent.user!!.name2.toString()
                        else homeComponent.user!!.name


                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(3.dp),
                        text = "مرحبا بك: ${userName.value}"
                    )
                }
                if (homeComponent.user!!.name2 == null)
                    Text(
                        "قم بتعيين الاسم الان", fontSize = 8.sp, maxLines = 1,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                goToAddName()
                            },
                        color = Color.Blue,
                        overflow = TextOverflow.Ellipsis,
                    )
                HorizontalDivider()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count), content = {
                        item {
                            Column(
                                Modifier
                                    .padding(3.dp)
                                    .width(20.dp)
                                    .clickable {
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            CartActivity::class.java
                                        )
                                        startActivity(intent)

                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Row(

                                    Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    BadgedBox(
                                        modifier = Modifier.fillMaxWidth(),
                                        badge = {
                                            Text(
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .padding(3.dp),
                                                color = Color.White,
                                                text = (cartController3.products.value.size + cartController3.offers.value.size).toString()
                                            )
                                        }) {
                                        Box(
                                            Modifier.align(Alignment.Center)
                                        ) {


                                            AsyncImage(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .padding(10.dp),
                                                model = R.drawable.shopping_cart_svgrepo_com,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }


                                Text(text = "السلة", fontSize = 10.sp)
                            }
                        }
                        item {
                            Column(
                                Modifier
                                    .padding(3.dp)
                                    .width(20.dp)
                                    .clickable {
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            OrdersActivity::class.java
                                        )
                                        startActivity(intent)

                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(10.dp),
                                    model = R.drawable.orders,
                                    contentDescription = null
                                )
                                Text(text = "الطلبات", fontSize = 10.sp)
                            }
                        }
                        item {
                            Column(
                                Modifier
                                    .padding(3.dp)
                                    .width(20.dp)
                                    .clickable {
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            SearchActivity::class.java
                                        )
                                        startActivity(intent)

                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(10.dp),
                                    model = R.drawable.search,
                                    contentDescription = null
                                )
                                Text(text = "بحث", fontSize = 10.sp)
                            }
                        }
                        if (homeComponent.offers.isNotEmpty())
                            item {
                                Column(
                                    Modifier
                                        .padding(3.dp)
                                        .clickable {
                                            itemView.value = !itemView.value
                                            itemType.value = 1
                                        }
                                        .width(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(10.dp),
                                        model = R.drawable.offers,
                                        contentDescription = null
                                    )
                                    Text(text = "العروض", fontSize = 10.sp)
                                }
                            }
                        if (homeComponent.ads.isNotEmpty())
                            item {
                                Column(
                                    Modifier
                                        .padding(3.dp)
                                        .clickable {
                                            itemView.value = !itemView.value
                                            itemType.value = 2
                                        }
                                        .width(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(10.dp),
                                        model = R.drawable.ads_advertising_color_svgrepo_com,
                                        contentDescription = null
                                    )
                                    Text(text = "الاعلانات", fontSize = 10.sp)
                                }
                            }
                        if (homeComponent.discounts.isNotEmpty())
                            item {
                                Column(
                                    Modifier
                                        .padding(3.dp)
                                        .clickable {
                                            Log.e("discounts", homeComponent.discounts.toString())
                                        }
                                        .width(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(10.dp),
                                        model = R.drawable.discount_label_svgrepo_com,
                                        contentDescription = null
                                    )
                                    Text(text = "التخفيضات", fontSize = 10.sp)
                                }
                            }
                    })
            }
    }

    private fun goToAddName() {
        val intent = Intent(this@DashboardActivity, AddNameActivity::class.java)
        updateName2ActivityResult.launch(intent)
    }

    @Composable
    private fun CategoriesComponents() {

//        Column {
//            Text(text = "الاصناف")
//            LazyVerticalGrid(
//                horizontalArrangement = Arrangement.Center,
//                modifier = Modifier
//                    .fillMaxWidth(),
//                columns = GridCells.Fixed(2),
//                content = {
//                    itemsIndexed(homeComponent.categories) { index, s ->
//
//                        Card(
//                            Modifier
//
//                                .height(200.dp)
//                                .width(200.dp)
//                                .padding(5.dp)
//
//                        ) {
//                            Box(
//                                Modifier
//                                    .fillMaxSize()
//                                    .background(Color.White)
//                                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
//                                    .clip(RoundedCornerShape(16.dp))
//                                    .clickable {
//                                        val intent = Intent(
//                                            this@DashboardActivity,
//                                            ProductsActivity::class.java
//                                        )
//                                        intent.putExtra("category_id", s.id)
//                                        startActivity(intent)
//                                    }
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(170.dp)
//                                        .align(Alignment.TopCenter)
//                                ) {
//                                    CustomImageView(context = this@DashboardActivity, imageUrl =s.category_image_path + s.image , okHttpClient = requestServer.createOkHttpClientWithCustomCert())
//                                }
//
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(30.dp)
//                                        .align(Alignment.BottomCenter)
//                                        .background(MaterialTheme.colorScheme.primary),
//                                ) {
//
//                                    Text(
//                                        modifier = Modifier
//                                            .align(Alignment.Center),
//                                        text = s.name,
//                                        fontSize = 12.sp,
//                                        color = MaterialTheme.colorScheme.secondary,
//                                        overflow = TextOverflow.Ellipsis,
//                                        maxLines = 1
//                                    )
//
//                                }
//                            }
//
//
//                        }
//
//                    }
//                })
//        }
    }

//    @Composable
    private fun LazyListScope.OffersComponents() {
    item {
        Text(text = "العروض")
        HorizontalDivider()
        LazyHorizontalGrid(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .padding(5.dp),
            rows = GridCells.Fixed(1), content = {
                itemsIndexed(homeComponent.offers) { index, item ->
                    Column(
                        Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .animatedBorder(
                                    borderColors = listOf(Color.Green, Color.Blue),
                                    backgroundColor = Color.White,
                                    shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp),
                                    borderWidth = 2.dp,
                                    animationDurationInMillis = 5000
                                )
                        )
                        {
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = item.price)
                                Text(text = " ريال ")
                            }
                            Row {
                                val formattedDateTime = item.expireAt.replace("\\s".toRegex(), "T")
                                val date = (LocalDateTime.parse(formattedDateTime))
                                val diff =
                                    (Duration.between(getCurrentDate(), date).toDays() + 1).toString()
                                if (diff == "1") {
                                    Text(text = "ينتهي اليوم ", fontSize = 12.sp)
                                } else {
                                    Text(text = "ينتهي خلال: ", fontSize = 12.sp)
                                    Text(text = diff, fontSize = 12.sp)
                                    Text(text = " ايام ")
                                }

                            }
                        }


                        Card(
                            Modifier
                                .aspectRatio(16f / 9f)
//                            .padding(5.dp)

                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            OfferProductsActivity::class.java
                                        )
                                        intent.putExtra("offer", MyJson.MyJson.encodeToString(item))
                                        startActivity(intent)
                                    }) {
                                CustomImageView(context = this@DashboardActivity, imageUrl =item.image , okHttpClient = requestServer.createOkHttpClientWithCustomCert())
//                                SubcomposeAsyncImage(
//                                    loading = {
//                                        LoadingCompose()
//                                    },
//                                    contentScale = ContentScale.Fit,
//                                    modifier = Modifier
//                                        .fillMaxSize(),
//                                    model = item.image,
//                                    contentDescription = "null",
//                                )
//                                Box (
//                                    Modifier
//                                        .align(
//                                            Alignment.BottomStart
//                                        )
//                                        .padding(5.dp)
//                                        .background(MaterialTheme.colorScheme.primary),
//                                ){
//
//
//
//                                }

                            }
                        }
                    }


                }
            })
        HorizontalDivider(thickness = 5.dp)
    }

    }

//    @Composable
    private fun LazyListScope.AdsComponent() {
        item {
            Text(text = "الاعلانات")
        }

        item{
            LazyHorizontalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                rows = GridCells.Fixed(1), content = {
                    itemsIndexed(homeComponent.ads) { index, item ->
                        Card(
                            Modifier
                                .height(150.dp)
                                .width(300.dp)
                                .padding(5.dp)

                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .clickable {

                                    }) {
                                CustomImageView(context = this@DashboardActivity, imageUrl = item.image, okHttpClient = requestServer.createOkHttpClientWithCustomCert(), modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                })
        }

    }
}

@Composable
fun Modifier.animatedBorder(
    borderColors: List<Color>,
    backgroundColor: Color,
    shape: Shape = RectangleShape,
    borderWidth: Dp = 1.dp,
    animationDurationInMillis: Int = 1000,
    easing: Easing = LinearEasing
): Modifier {
    val brush = Brush.sweepGradient(borderColors)
    val infiniteTransition = rememberInfiniteTransition(label = "animatedBorder")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationInMillis, easing = easing),
            repeatMode = RepeatMode.Restart
        ), label = "angleAnimation"
    )

    return this
        .clip(shape)
        .padding(borderWidth)
        .drawWithContent {
            rotate(angle) {
                drawCircle(
                    brush = brush,
                    radius = size.width,
                    blendMode = BlendMode.SrcIn,
                )
            }
            drawContent()
        }
        .background(color = backgroundColor, shape = shape)
}