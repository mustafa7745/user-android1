package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.OfferModel
import com.yemen_restaurant.greenland.models.OfferProductsModel
import com.yemen_restaurant.greenland.models.ProductModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.OfferProductsStorage
import com.yemen_restaurant.greenland.storage.ProductsStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.Duration
import java.time.LocalDateTime


class OfferProductsActivity : ComponentActivity() {
    private val offerProducts = mutableStateOf<List<OfferProductsModel>>(listOf())
    lateinit var offer: OfferModel
    private val stateController = StateController()
    val requestServer = RequestServer(this)
    val offerProductsStorage : OfferProductsStorage = OfferProductsStorage()





    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val str = intent.getStringExtra("offer")
        if (str != null ) {
            offer = MyJson.MyJson.decodeFromString(str)
        }
        else{
            finish()
        }
        checkIfNeedUpdate()
//        read()


        setContent {
            GreenlandRestaurantTheme {
                val topBarHeight = 70.dp
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        cCompose.topBar(topBarHeight = topBarHeight, this)
                    },
                    content = {
                        MainCompose1(padding = topBarHeight, stateController = stateController, activity = this, read = { read() }){
                            ProductsCompose()
                        }
                    },
                )
            }
        }
    }

    private fun checkIfNeedUpdate() {
        if (offerProductsStorage.isSet()) {
            val diff = Duration.between(offerProductsStorage.getDate(), getCurrentDate()).toMinutes()
            if (diff > 1) {
                read()
            } else {
                offerProducts.value = offerProductsStorage.get()
                stateController.successState()
            }
        } else {
            read()
        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ProductsCompose() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ){
           Text(text = "اسم العرض: " +  offer.name)
            HorizontalDivider(Modifier.padding(5.dp))
           Text(text =  "سعر العرض: " + offer.price)
            HorizontalDivider(Modifier.padding(5.dp))
           Text(text =  "وصف العرض: "+offer.description)
        }
        val foundItem = cartController3.offers.value.find { it.offerModel.id == offer.id }
        if (foundItem == null)
//            Box(modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.Cyan)
//                .padding(5.dp)
//                .clickable {
//                    cartController3.addOffer((offer))
//                } ,contentAlignment = Alignment.Center,)
//
//            {
//                Text(text = "اضافة الى السلة", fontSize = 12.sp)
//            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .padding(vertical = 8.dp),
                onClick = {
                    cartController3.addOffer((offer))
                }
            ) {
                Text(
                    text = "اضافة الى السلة", fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(
                        Font(R.font.bukra_bold)
                    )
                )
            }
        else{
            HorizontalDivider(Modifier.padding(5.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    cartController3.incrementOfferQuantity(offer.id)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = ""
                    )
                }
                Text(text = foundItem.offerCount.value.toString())
                IconButton(onClick = {


                    cartController3.decrementOfferQuantity(offer.id)
                }) {
                    Icon(
                        painter = painterResource(
                            R.drawable.baseline_remove_24
                        ),
                        contentDescription = ""
                    )

                    //                                                                            Icon(imageVector = R.drawable.ic_launcher_background, contentDescription = "" )
                }
                IconButton(onClick = {
                    cartController3.removeOffer(offer.id)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = ""
                    )
                }
            }
            HorizontalDivider(Modifier.padding(5.dp))
        }

        LazyVerticalGrid(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                ,
            columns = GridCells.Fixed(2),
            content = {
                itemsIndexed(offerProducts.value) { index, s1 ->

                    Card(
                        Modifier
                            .width(200.dp)
                            .padding(5.dp)

                    ) {
                        Column {
//                            Text(text = s1.product.name)
//                            NamePriceModal(s1)
                            NamePriceProductOffer(s1)

                            val pagerState =
                                rememberPagerState(pageCount = { s1.product.productImages.size })
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                            ) {
                                if (s1.product.productImages.isEmpty())
                                    Text(modifier = Modifier.align(Alignment.Center), text = "لايوجد صور لهذا الصنف" , fontSize = 8.sp)
                                else
                                    HorizontalPager(
                                        pagerState,
                                        modifier = Modifier.fillMaxSize()
                                    ) { i ->
                                        Card(
                                            Modifier
                                                .fillMaxSize(),
                                            colors = CardColors(
                                                containerColor = Color.White,
                                                contentColor = Color.Black,
                                                disabledContainerColor = Color.Blue,
                                                disabledContentColor = Color.Cyan
                                            )
                                        ) {
                                            CustomImageView(
                                                context = this@OfferProductsActivity,
                                                imageUrl = s1.product.productImages[i].image,
                                                okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                            )
                                        }

                                    }
                            }


                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary),

                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
//                                        LazyRow(
//                                            Modifier
//                                                .fillMaxWidth()
//                                                .padding(5.dp),
//                                            horizontalArrangement = Arrangement.Center,
//                                            content = {
//                                                items(s1.productImages.size) {
//
//
//                                                    if (pagerState.currentPage == it)
//                                                        Icon(
//                                                            modifier = Modifier.size(10.dp),
//                                                            painter = painterResource(R.drawable.baseline_filled_circle_24),
//                                                            contentDescription = "",
//                                                            tint = MaterialTheme.colorScheme.background
//                                                        )
//                                                    else {
//                                                        Icon(
//                                                            modifier = Modifier.size(10.dp),
//                                                            painter = painterResource(R.drawable.outline_circle_24),
//                                                            contentDescription = "",
//                                                            tint = MaterialTheme.colorScheme.background
//                                                        )
//                                                    }
//                                                }
//                                            })
                                HorizontalDivider(
                                    Modifier.padding(5.dp)
                                )
                            }
                        }

                    }

//                    Card(
//                        Modifier
//                            .width(200.dp)
//                            .padding(5.dp)
//
//                    ) {
//                        Column {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(30.dp)
//                                    .background(MaterialTheme.colorScheme.primary),
//                            ){
//                                Row (
//                                    Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.Center,
//                                ){
//                                    Text(text = "العدد: ")
//                                    Text(text = s.productQuantity)
//
//                                }
//                            }
//
//                            val pagerState = rememberPagerState(pageCount = { s.product.productImages.size })
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(170.dp)
//                            ) {
////                                SubcomposeAsyncImage(
////                                    loading = {
////                                        CircularProgressIndicator()
////                                    },
////                                    contentScale = ContentScale.Fit,
////                                    modifier = Modifier
////                                        .fillMaxSize(),
////                                    model = s.category_image_path + s.image,
////                                    contentDescription = "null",
////                                )
//
//
//                                HorizontalPager(
//                                    pagerState,
//                                    modifier = Modifier
//                                        .aspectRatio(1f)
//                                ) { i ->
//                                    Card(
//                                        Modifier
//                                            .fillMaxSize()
//                                            .padding(5.dp),
//                                        colors = CardColors(
//                                            containerColor = Color.White,
//                                            contentColor = Color.Black,
//                                            disabledContainerColor = Color.Blue,
//                                            disabledContentColor = Color.Cyan
//                                        )
//                                    ) {
//                                            AsyncImage(
//                                                modifier = Modifier.padding(10.dp
//                                                ),
//                                                model = s.product.productImages[i].image,
//                                                contentDescription = ""
//                                            )
//                                    }
//
//                                }
//                            }
//
//
//                                Column(
//                                    Modifier
//                                        .fillMaxWidth()
//                                        .background(MaterialTheme.colorScheme.primary),
//
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    LazyRow(
//                                        Modifier
//                                            .fillMaxWidth()
//                                            .padding(5.dp),
//                                        horizontalArrangement= Arrangement.Center,
//                                        content = {
//                                            items(s.product.productImages.size){
//                                                if (pagerState.currentPage == it)
//                                                    Icon(
//                                                        modifier = Modifier.size(10.dp),
//                                                        painter = painterResource(R.drawable.baseline_filled_circle_24) ,
//                                                        contentDescription = "",
//                                                        tint = MaterialTheme.colorScheme.background
//                                                    )
//                                                else{
//                                                    Icon(
//                                                        modifier = Modifier.size(10.dp),
//                                                        painter = painterResource(R.drawable.outline_circle_24) ,
//                                                        contentDescription = "",
//                                                        tint = MaterialTheme.colorScheme.background
//                                                    )
//                                                }
//                                            }
//                                        })
//                                    Text(
//                                        modifier = Modifier.padding(5.dp),
//                                        text = s.product.name,
//                                        fontSize = 12.sp,
//                                        color = MaterialTheme.colorScheme.secondary,
//                                        overflow = TextOverflow.Ellipsis,
//                                        maxLines = 1
//                                    )
//                                }
//                            }
//
//                    }
                }
            })
    }

    private fun read() {
        stateController.startRead()
        val data3 : JsonObject = buildJsonObject {
            put("tag", "read")
            put("inputOfferId",offer.id)
        }

        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.offersProductsUrl,{ code, it->
            stateController.errorStateRead(it)
        }){

                offerProducts.value =  MyJson.IgnoreUnknownKeys.decodeFromString(it)
            offerProductsStorage.set(it)
                stateController.successState()
        }
    }

    @Composable
    fun NamePriceProductOffer(s1: OfferProductsModel) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = s1.product.name,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                HorizontalDivider()
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = "العدد: "+s1.productQuantity ,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
        }

//        HorizontalDivider()
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(30.dp)
//                .background(MaterialTheme.colorScheme.primary),
//        ) {
//
//            Row(
//                Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//            ) {
//                Text(text = "السعر: ", color = Color.White)
//                Text(text = formatPrice(s1.product.postPrice), color = Color.White)
//            }
//        }
    }
}