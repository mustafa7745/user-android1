package com.yemen_restaurant.greenland.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.LocationTypeModel
import com.yemen_restaurant.greenland.models.OfferModel
import com.yemen_restaurant.greenland.models.OfferProductsModel
import com.yemen_restaurant.greenland.models.ProductModel
import com.yemen_restaurant.greenland.models.UserLocationModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.ProductInCart
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.OfferProductsStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.Duration


class AddOfferToCartActivity : ComponentActivity() {
    val stateController = StateController()
    val requestServer = RequestServer(this)
    lateinit var offerModel: OfferModel
    val offerProductsStorage : OfferProductsStorage = OfferProductsStorage()
    private val offerProducts = mutableStateOf<List<OfferProductsModel>>(listOf())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val str = intent.getStringExtra("offer")
        if (str != null) {
            try {
                offerModel = MyJson.IgnoreUnknownKeys.decodeFromString(str)
            }catch (e:Exception){
                finish()
            }

        } else {
            finish()
        }

        checkIfNeedUpdate()
        setContent {
            GreenlandRestaurantTheme {
                MainCompose1(padding = 0.dp, stateController = stateController, activity = this, read = { read() }){
                    Column(
                    ) {
                        MainContent()
                    }
                }
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
    private fun read() {
        stateController.startRead()
        val data3 : JsonObject = buildJsonObject {
            put("tag", "read")
            put("inputOfferId",offerModel.id)
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
    @OptIn(ExperimentalFoundationApi::class)
    private fun MainContent() {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            LazyColumn {
                itemsIndexed(offerProducts.value){ index,item ->
                    ProductOfferCard(item)
                }
            }

            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(
                            16.dp
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AddToCartUi()
            }
        }
    }

    @Composable
    private fun ProductOfferCard(product: OfferProductsModel) {
        Card(
            Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(5.dp)
                    .clickable {
//                        if (product.products_groupsName != "الرئيسية") {
//                            if (isShowSubProducts.value) {
//                                goToAddToCart(product)
//                            } else {
//                                groupId = product.products_groupsId
//                                isShowSearch.value = false
//                                isShowSubProducts.value = true
//                            }
//                        } else {
//                            goToAddToCart(product)
//                        }
                    },
                colors = CardColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.Blue,
                    disabledContentColor = Color.Cyan
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                ) {

                    Column(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.6F),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(3.dp),
                            //                                                            .align(
                            //                                                                Alignment.TopStart
                            //                                                            ),
                            textAlign = TextAlign.Start,
                            text = product.product.name,
                            fontSize = 12.sp,
                            color = Color.Black,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        if (product.product.description != null) {
                            Text(
                                text = product.product.description,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                color = Color.Gray,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis

                            )
                        }



                        Row(
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Text(
                                text = formatPrice(product.product.postPrice) + " ريال ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (cartController3.products.value.find { it.productsModel == product.product} != null) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = "",
                                    tint = Color.Blue
                                )
                            }
                            if (product.product.isAvailable == "0") {
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "غير متوفر حاليا",
                                    fontSize = 8.sp,
                                    color = Color.Red
                                )
                            }
                        }
                    }

                    if (product.product.productImages.isNotEmpty()) {
                        CustomImageView(
                            modifier = Modifier
                                .size(150.dp),
                            context = this@AddOfferToCartActivity,
                            imageUrl = product.product.productImages.first().image,
                            okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                        )
                    } else {
                        AsyncImage(
                            model = R.drawable.logo,
                            contentDescription = "",
                            modifier = Modifier
                                .size(120.dp)
                                .padding(5.dp),
                            contentScale = ContentScale.Inside
                        )
                    }

                }
            }
        }
    }

    @Composable
    fun AddToCartUi(
    ) {
            val foundItem =
                cartController3.offers.value.find { it.offerModel == offerModel }
            if (foundItem == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .clickable {
                            cartController3.addOffer((offerModel))
                        },
                    contentAlignment = Alignment.Center,
                )

                {
                    Row(
                        Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "اضافة الى السلة", fontSize = 12.sp)
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                }
            } else {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = {
                        cartController3.incrementProductQuantity(offerModel.id)
                    }) {
                        Icon(
                            modifier =
                            Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(
                                        16.dp
                                    )
                                )
                                .clip(
                                    RoundedCornerShape(
                                        16.dp
                                    )
                                ),
                            imageVector = Icons.Outlined.Add,
                            contentDescription = ""
                        )
                    }
                    Text(text = foundItem.offerCount.value.toString())
                    IconButton(onClick = {
                        cartController3.decrementProductQuantity(offerModel.id)
                    }) {
                        Icon(
                            modifier =
                            Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(
                                        16.dp
                                    )
                                )
                                .clip(
                                    RoundedCornerShape(
                                        16.dp
                                    )
                                ),
                            painter = painterResource(
                                R.drawable.baseline_remove_24
                            ),
                            contentDescription = ""
                        )

                        //                                                                            Icon(imageVector = R.drawable.ic_launcher_background, contentDescription = "" )
                    }
                    IconButton(
                        onClick = {
                            cartController3.removeProduct(offerModel.id)
                        }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "",
                            tint = Color.Red
                        )
                    }
                }
        }

    }
}

