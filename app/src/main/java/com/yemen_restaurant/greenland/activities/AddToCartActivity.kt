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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
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
import com.yemen_restaurant.greenland.models.ProductModel
import com.yemen_restaurant.greenland.models.UserLocationModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.ProductInCart
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme


class AddToCartActivity : ComponentActivity() {
    val stateController = StateController()
    val requestServer = RequestServer(this)
    lateinit var product:ProductModel

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val str = intent.getStringExtra("product")
        if (str != null) {
            try {
                product = MyJson.IgnoreUnknownKeys.decodeFromString(str)
            }catch (e:Exception){
                finish()
            }

        } else {
            finish()
        }

        setContent {
            GreenlandRestaurantTheme {
                MainCompose2(padding = 0.dp, stateController =stateController , activity = this@AddToCartActivity ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        LazyColumn(
Modifier.padding(bottom = 50.dp),
                            content = {
                            item {
                                val pagerState =
                                    rememberPagerState(pageCount = { product.productImages.size })
                                if (product.productImages.isEmpty())
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = "لايوجد صور لهذا الصنف",
                                        fontSize = 8.sp
                                    )
                                else
                                    HorizontalPager(
                                        pagerState,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1F)
                                    ) { i ->
                                        Card(
                                            Modifier
                                                .fillMaxSize()
                                                .padding(5.dp),
                                            colors = CardColors(
                                                containerColor = Color.White,
                                                contentColor = Color.Black,
                                                disabledContainerColor = Color.Blue,
                                                disabledContentColor = Color.Cyan
                                            )
                                        ) {
                                            CustomImageView(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                context = this@AddToCartActivity,
                                                imageUrl = product.productImages[i].image,
                                                okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                            )

                                        }

                                    }
                            }
                                item {
                                    Text(text =  (product.name), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                                }
                                item {
                                    Row (
                                        Modifier.fillMaxWidth().padding(5.dp)
                                    ){
                                        Text(text =  formatPrice(product.postPrice) + " ريال ", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                                    }
                                }
                        })

                        Box(
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
                                )
                                .padding(5.dp)
                        ) {
                            AddToCartUi()
                        }
                    }

                }
            }
        }
    }
    @Composable
    fun AddToCartUi(
    ) {
        val foundItem =
            cartController3.products.value.find { it.productsModel == product }
        if (foundItem == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .clickable {
                        cartController3.addProduct((product))
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
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    cartController3.incrementProductQuantity(product.id)
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
                Text(text = foundItem.productCount.value.toString())
                IconButton(onClick = {
                    cartController3.decrementProductQuantity(product.id)
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
                    cartController3.removeProduct(product.id)
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

