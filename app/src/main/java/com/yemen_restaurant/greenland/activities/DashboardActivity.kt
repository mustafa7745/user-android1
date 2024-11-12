package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
//import com.quadible.smarttabslist.SmartTabsList
//import com.ahmadhamwi.tabsync_compose.lazyListTabSync
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.application.MyApplication
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.models.OfferModel
import com.yemen_restaurant.greenland.models.ProductModel
import com.yemen_restaurant.greenland.models.User
import com.yemen_restaurant.greenland.shared.CartController3
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.SharedInAppUpdate
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.HomeComponentStorage
import com.yemen_restaurant.greenland.storage.ProductsStorage
import com.yemen_restaurant.greenland.storage.UserStorage
import com.yemen_restaurant.greenland.synclist.Category
import com.yemen_restaurant.greenland.synclist.MyTabBar
import com.yemen_restaurant.greenland.synclist.convertToCategoryStructure
import com.yemen_restaurant.greenland.synclist.lazyListTabSync
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import com.yemen_restaurant.greenland.viewModels.HomeComponentViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Duration
import java.time.LocalDateTime
import java.util.Locale
import kotlin.system.exitProcess

val cartController3 = CartController3()


class DashboardActivity : ComponentActivity() {
    private val homeComponentViewModel: HomeComponentViewModel by viewModels()
    val requestServer = RequestServer(this)
    private val userName = mutableStateOf("")
    val isShowSubProducts = mutableStateOf(false)
    lateinit var groupId: String
    val isShowSearch = mutableStateOf(false)

    private lateinit var updateName2ActivityResult: ActivityResultLauncher<Intent>
    val drawerState = mutableStateOf(DrawerState(initialValue = DrawerValue.Closed))

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       SharedInAppUpdate(this).checkUpdate()
       homeComponentViewModel.productsStorage= ProductsStorage(this)
       updateName2ActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val resultValue = data?.getStringExtra("user2")
                // استخدام النتيجة كما تشاء
                if (resultValue != null){
                    val user =  MyJson.IgnoreUnknownKeys.decodeFromString<User>(resultValue)
                    if (user.name2 != null){
                        userName.value = user.name2.toString()
                        homeComponentViewModel.userStorage.setUser(MyJson.IgnoreUnknownKeys.encodeToString(user))
                        homeComponentViewModel.homeComponent.user?.name2 = user.name2
                        homeComponentViewModel. homeComponentStorage.setHomeComponent(MyJson.IgnoreUnknownKeys.encodeToString(homeComponentViewModel.homeComponent))
                    }
                }

            }
        }
//       if (!homeComponentViewModel.stateController.isLoadingRead.value)
        homeComponentViewModel.stateController.startRead()
        lifecycleScope.launch {
            delay(1)
            homeComponentViewModel.checkIfNeedUpdate(requestServer, goToAddName = {goToAddName()})
        }

        setContent {
            GreenlandRestaurantTheme {
                ExitConfirmation()
                MainCompose1(
                    padding = 0.dp,
                    stateController = homeComponentViewModel.stateController,
                    activity = this@DashboardActivity,
                    read = {
                        homeComponentViewModel.read(
                            requestServer,
                            goToAddName = { goToAddName() })
                    }) {
                    ModalNavigationDrawer(
                        drawerState = drawerState.value,
                        drawerContent = {
                            NavigationDrawer()
                        }
                    ) {
                        Column(
                            Modifier.fillMaxSize()
                        ) {
                            ImagesAndName()

                            if (isShowSubProducts.value) {
                                modalListV2()
                            }
                            if (isShowSearch.value) {
                                SearchDialog(
                                    onDismiss = { isShowSearch.value = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NavigationDrawer() {
        ModalDrawerSheet(
            modifier = Modifier.fillMaxWidth(0.6F)
        ) {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier.padding(16.dp),
                    ) {

                        AsyncImage(
                            modifier = Modifier
                                .size(50.dp)
                                .padding(10.dp),
                            model = R.drawable.user,
                            contentDescription = null
                        )
                        userName.value =
                            if (homeComponentViewModel.homeComponent.user!!.name2 != null) homeComponentViewModel.homeComponent.user!!.name2.toString()
                            else homeComponentViewModel.homeComponent.user!!.name

                        Text(
    //                            maxLines = 1,
    //                            overflow = TextOverflow.Ellipsis,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(3.dp),
                            text = "مرحبا بك: ${userName.value}"
                        )

                    }
                    if (homeComponentViewModel.homeComponent.user!!.name2 == null)
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
                    NavigationDrawerItem(
                        icon = {

                            AsyncImage(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp), model = R.drawable.orders, contentDescription = null
                            )
                        },
                        label = { Text(text = "طلباتي السابقة",fontSize = 12.sp) },
                        selected = false,
                        onClick = {
                            goToOrders(this@DashboardActivity)
                        }
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        icon = {
                            AsyncImage(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp), model = R.drawable.shopping_cart_svgrepo_com, contentDescription = null
                            )
                        },
                        label = { Text(text = "سلتي",fontSize = 12.sp) },
                        selected = false,
                        onClick = {
                            goToCart(this@DashboardActivity)
                        }
                    )

                    HorizontalDivider()
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                modifier = Modifier.padding(5.dp),
                                imageVector = Icons.Outlined.Place,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        label = { Text(text = "اماكني",fontSize = 12.sp) },
                        selected = false,
                        onClick = {
                            goToLocations(this@DashboardActivity)
                        }
                    )
//                    HorizontalDivider()
//                    NavigationDrawerItem(
//                        icon = {
//                            Icon(
//                                modifier = Modifier.padding(5.dp),
//                                imageVector = Icons.Outlined.FavoriteBorder,
//                                contentDescription = "",
//                                tint = Color.Red
//                            )
//                        },
//                        label = { Text(text = "المفضلة",fontSize = 12.sp) },
//                        selected = false,
//                        onClick = { /*TODO*/ }
//                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                modifier = Modifier.padding(5.dp),
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        label = { Text(text = "سياسة الخصوصية وشروط الاستخدام" , fontSize = 12.sp) },
                        selected = false,
                        onClick = {
                            intentFunUrl("https://greenland-rest.com/policies-terms.html")
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider()
                    Text("الاتصال بنا ")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = {
intentFunUrl("tel:780222271")
                            }
                        ) {
                            Icon(
                                modifier = Modifier.padding(5.dp),
                                imageVector = Icons.Outlined.Phone,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
intentFunUrl("https://api.whatsapp.com/send?phone=967780222271")
                            }
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp),
                                model = R.drawable.whatsapp_icon,
                                contentDescription = null
                            )
                        }

                    }
                    Spacer(Modifier.height(40.dp))



                    HorizontalDivider()
                    Text("مواقع التواصل الاجتماعي")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = {

                                intentFunUrl("https://www.facebook.com/greenland.rests")
                            }
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp),
                                model = R.drawable.facebook_,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                intentFunUrl("https://www.instagram.com/greenland.rest")
                            }
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp),
                                model = R.drawable.instagram_,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {

                                intentFunUrl("https://t.me/+rbaWvWzfG7phYjM0")
                            }
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp),
                                model = R.drawable.telegram_,
                                contentDescription = null
                            )

                        }
                    }

                }
            }
        }
    }
    private fun intentFunUrl(uri:String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uri)
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {

        }
    }

    @Composable
    private fun HeaderComponent(modifier :Modifier = Modifier) {

        if (homeComponentViewModel.homeComponent.user != null)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(

                    Modifier.padding(8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row (
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Row (
                            Modifier.border(
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            val scope = rememberCoroutineScope()
                            IconButton(modifier =  Modifier.size(50.dp),onClick = {

                                scope.launch {
                                    drawerState.value.open()
//                                    if (drawerState.isClosed) {
//                                        drawerState.value.open()  // Open the drawer
//                                    } else {
//                                        drawerState.value.close() // Close the drawer
//                                    }
                                }
                            }){
                                Icon(
                                    modifier =  Modifier.padding(5.dp),
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                           AsyncImage(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(5.dp).clickable{
                                    scope.launch {
                                        drawerState.value.open()
                                    }
                                }, model = R.drawable.logo, contentDescription = null
                        )

                        }
                    }
                    Row (
                        Modifier.border(
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
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Spacer(Modifier.width(5.dp))

                        Text(
                            modifier = Modifier

                                .padding(2.dp),
                            color = Color.Black,
                            text = (cartController3.products.value.size + cartController3.offers.value.size).toString()
                        )
                        IconButton(modifier =  Modifier.size(50.dp),onClick = {
                            goToCart(this@DashboardActivity)
                        }){
                            Icon(
                                modifier =  Modifier.padding(5.dp),
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }



                        IconButton(modifier =  Modifier.size(50.dp),onClick = {
                            isShowSearch.value = true
                        }){
                            Icon(
                                modifier =  Modifier.padding(5.dp),
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                HorizontalDivider()
            }
    }



    private fun goToAddName() {
        val intent = Intent(this@DashboardActivity, AddNameActivity::class.java)
        updateName2ActivityResult.launch(intent)
    }

    @Composable
    fun ExitConfirmation() {
        var showExitDialog by remember { mutableStateOf(false) }

        // التقاط زر الرجوع
        BackHandler {
            showExitDialog = true
        }

        if (showExitDialog) {
            ExitConfirmationDialog(
                onConfirm = {
                    // تأكيد الخروج
                    exitProcess(0) // إغلاق التطبيق (يمكنك استبداله بإجراء آخر)
                },
                onDismiss = {
                    // إغلاق نافذة التأكيد والعودة للتطبيق
                    showExitDialog = false
                }
            )
        }

//        // واجهة المستخدم الرئيسية
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .wrapContentSize(Alignment.Center)
//        ) {
//            Text(text = "اضغط زر الرجوع للخروج أو اضغط فوق النافذة لإلغاء التأكيد.")
//        }
    }

    @Composable
    fun ExitConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "هل أنت متأكد؟")
            },
            text = {
                Text("هل تريد الخروج من التطبيق؟")
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm
                ) {
                    Text("نعم")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("لا")
                }
            }
        )
    }



    @Composable
    private fun OffersContent() {
        Text(text = "عروض مميزة")
        LazyHorizontalGrid(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .padding(5.dp),
            rows = GridCells.Fixed(1), content = {
                itemsIndexed(homeComponentViewModel.homeComponent.offers) { index, item ->
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
                                    (Duration.between(getCurrentDate(), date)
                                        .toDays() + 1).toString()
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
                                        goToAddOfferToCart(item)
                                    }) {
                                CustomImageView(
                                    context = this@DashboardActivity,
                                    imageUrl = item.image,
                                    okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                )
                            }
                        }
                    }
                }
            })
        HorizontalDivider(thickness = 5.dp)
    }

    @Composable
    private fun AdsContent() {

        // State for tracking the current index
        var currentIndex by remember { mutableStateOf(0) }

        // LazyRow for displaying images
        val scrollState = rememberLazyListState()

        // Automatically scroll every 3 seconds
        LaunchedEffect(key1 = currentIndex) {
            delay(3000) // Delay to move to the next image
            val nextIndex = if (currentIndex == homeComponentViewModel.homeComponent.ads.size - 1) 0 else currentIndex + 1
            scrollState.animateScrollToItem(nextIndex)
            currentIndex = nextIndex
        }

        LazyRow (
            state = scrollState,
            modifier = Modifier
                .fillMaxWidth()
                ,
//            rows = GridCells.Fixed(1),
            content = {
                itemsIndexed(homeComponentViewModel.homeComponent.ads) { index, item ->
                    Card(
                        Modifier
                            .height(175.dp)
                            .width(300.dp)
                            .padding(5.dp)

                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .clickable {
                                    if (item.type != null && item.product_cat_id != null){
                                        if (item.type == "1"){
                                            homeComponentViewModel.homeComponent.products.find { it.id == item.product_cat_id }
                                                ?.let { goToAddToCart(it) }
                                        }
                                        else if(item.type == "2"){
//                                            val category = homeComponentViewModel.homeComponent.categories.find { it.id == item.product_cat_id }
//                                            if (category != null){
////                                                onGoToCategory(homeComponentViewModel.homeComponent.categories.indexOf(category))
//                                                onTabClicked(index, category)
//
//                                            }

                                        }

                                    }

                                }) {
                            CustomImageView(
                                context = this@DashboardActivity,
                                imageUrl = item.image,
                                okHttpClient = requestServer.createOkHttpClientWithCustomCert(),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            })
    }

    @Composable
    private fun ImagesAndName() {
        val (selectedTabIndex, setSelectedTabIndex, listState) = lazyListTabSync(homeComponentViewModel.cats.indices.toList())
        val isVisible by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex<=0
            }
           }

            HeaderComponent()


            AnimatedVisibility(visible = isVisible,
//                exit = fadeOut(tween(durationMillis = 500)) + slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500))
            ) {
                if (homeComponentViewModel.homeComponent.ads.isNotEmpty())
                    AdsContent()
            }

//        Row(
//            horizontalArrangement = Arrangement.End,
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ){
//            IconButton(onClick = {
//
//            }) {
//                Icon(
//
//                    painter = painterResource(
//                        R.drawable.baseline_read_more_24
//                    ),
//                    contentDescription = ""
//                )
//            }
//
//        }
        MyTabBar(categories = homeComponentViewModel.cats, selectedTabIndex =selectedTabIndex,requestServer) { index, _ ->
            setSelectedTabIndex(
                index
            )
        }




        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                itemsIndexed(homeComponentViewModel.cats) { index, s3 ->
                    if (index == 0 || index == 5){
                        if (homeComponentViewModel.homeComponent.offers.isNotEmpty())
                            OffersContent()
//                        LazyRow (
//                            modifier = Modifier
//                                .fillMaxWidth().height(175.dp)
//                            ,
//                            content = {
//                                itemsIndexed(homeComponentViewModel.homeComponent.ads) { index, item ->
//                                    Card(
//                                        Modifier
//                                            .height(175.dp)
//                                            .width(300.dp)
//                                            .padding(5.dp)
//
//                                    ) {
//                                        Box(
//                                            Modifier
//                                                .fillMaxSize()
//                                                .clickable {
//                                                    if (item.type != null && item.product_cat_id != null){
//                                                        if (item.type == "1"){
//                                                            homeComponentViewModel.homeComponent.products.find { it.id == item.product_cat_id }
//                                                                ?.let { goToAddToCart(it) }
//                                                        }
//                                                        else if(item.type == "2"){
////                                            val category = homeComponentViewModel.homeComponent.categories.find { it.id == item.product_cat_id }
////                                            if (category != null){
//////                                                onGoToCategory(homeComponentViewModel.homeComponent.categories.indexOf(category))
////                                                onTabClicked(index, category)
////
////                                            }
//
//                                                        }
//
//                                                    }
//
//                                                }) {
//                                            CustomImageView(
//                                                context = this@DashboardActivity,
//                                                imageUrl = item.image,
//                                                okHttpClient = requestServer.createOkHttpClientWithCustomCert(),
//                                                modifier = Modifier.fillMaxSize()
//                                            )
//                                        }
//                                    }
//                                }
//                            })
                    }
                    Row (
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ){
                        Text(s3.category.name, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary,modifier = Modifier.padding(all = 8.dp))
                    }
                            s3.listOfProducts.forEach { product ->
                                ProductCard(product)
                        }
                }
            })
    }

    @Composable
    private fun ProductCard(product: ProductModel) {
        Card(
            Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .padding(5.dp)
                    .clickable {
                        if (product.products_groupsName != "الرئيسية") {
                            if (isShowSubProducts.value) {
                                goToAddToCart(product)
                            } else {
                                groupId = product.products_groupsId
                                isShowSearch.value = false
                                isShowSubProducts.value = true
                            }
                        } else {
                            goToAddToCart(product)
                        }
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
                            text = product.name,
                            fontSize = 12.sp,
                            color = Color.Black,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        if (product.description != null) {
                            Text(
                                text = product.description,
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
                                text = formatPrice(product.postPrice) + " ريال ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (cartController3.products.value.find { it.productsModel == product } != null) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = "",
                                    tint = Color.Blue
                                )
                            }
                            if (product.isAvailable == "0") {
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "غير متوفر حاليا",
                                    fontSize = 8.sp,
                                    color = Color.Red
                                )
                            }
                            if (product.products_groupsName != "الرئيسية"){
                                if (!isShowSubProducts.value){
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(text = "متعدد الخيارات" ,   fontSize = 8.sp,modifier = Modifier .border(
                                        1.dp,

                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(
                                            16.dp
                                        )
                                    ))
                                }

                            }
                        }
                    }

                    if (product.productImages.isNotEmpty()) {
                        CustomImageView(
                            modifier = Modifier
                                .size(150.dp),
                            context = this@DashboardActivity,
                            imageUrl = product.productImages.first().image,
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
    private fun ImagesAndName2(newList2: List<ProductModel>) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            content = {
                itemsIndexed(newList2) { index, product ->

                    ProductCard(product = product)
                    }
            })
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun modalListV2(

    ) {
        val modalList =
           homeComponentViewModel.homeComponent.products.filter { it.products_groupsId == groupId }
        val newList2 = arrayListOf<ProductModel>()
        newList2.addAll(modalList)
        ModalBottomSheet(
            onDismissRequest = { isShowSubProducts.value = false }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp)
            ){
                ImagesAndName2(newList2 = newList2)
            }
        }
    }
    private fun goToAddOfferToCart(
        s: OfferModel
    ) {

        val intent = Intent(
            this,
            AddOfferToCartActivity::class.java
        )
        intent.putExtra("offer", MyJson.MyJson.encodeToString(s))
        startActivity(intent)
    }
    private fun goToAddToCart(
        s: ProductModel
    ) {

        val intent = Intent(
            this,
            AddToCartActivity::class.java
        )
        intent.putExtra("product", MyJson.MyJson.encodeToString(s))
        startActivity(intent)
    }


//

    @Composable
    fun SearchDialog(
        onDismiss: () -> Unit
    ) {

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    var search by remember { mutableStateOf("") }
                    val newList2 = if (search.isNotEmpty()) {
                        homeComponentViewModel.homeComponent.products.filter { it.name.contains(search, ignoreCase = true) }
                    } else {
                        emptyList() // Return an empty list when the search query is empty
                    }
                    // Search TextField
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick =onDismiss) {
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
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = ""
                            )
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(3.dp))
                    TextField(
                        value = search,
                        onValueChange = {
                                      search = it
                        },
                        trailingIcon = {
                                Icon(

                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = ""
                                )
                        },
                        label = { Text("ابحث هنا") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("ابحث عن الوجبات والعصائر") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ImagesAndName2(newList2 = newList2 )

                    // Cancel and search buttons

                }
            }
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
@Composable
fun CartButton(appComponentActivity: ComponentActivity) {
    Column(
        Modifier
            .padding(3.dp)
            .clickable {
                goToCart(appComponentActivity)

            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    modifier = Modifier
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
                        )
                        .padding(3.dp),
                    color = Color.Black,
                    text = (cartController3.products.value.size + cartController3.offers.value.size).toString()
                )
            }
            Column {
                AsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(10.dp),
                    model = R.drawable.shopping_cart_svgrepo_com,
                    contentDescription = null
                )
                Text(text = "السلة", fontSize = 10.sp)
            }
        }
    }
}

private fun goToCart(appComponentActivity: ComponentActivity) {
    val intent = Intent(
        appComponentActivity,
        CartActivity::class.java
    )
    appComponentActivity.startActivity(intent)
}
private fun goToLocations(appComponentActivity: ComponentActivity) {
    val intent = Intent(
        appComponentActivity,
        UserLocationsActivity::class.java
    )
    intent.putExtra("1", "1")
    Log.e("dede","sdds")
    appComponentActivity.startActivity(intent)
}
private fun goToOrders(appComponentActivity: ComponentActivity) {
    val intent = Intent(
        appComponentActivity,
        OrdersActivity::class.java
    )
    appComponentActivity.startActivity(intent)
}

//fun roundToNearestFifty(value: Int): Int {
//    return ((value + 25) / 50) * 50
//}
fun formatPrice(price: String): String {
    val doublePrice = price.toDouble()
    val symbols = DecimalFormatSymbols(Locale.ENGLISH)
    val decimalFormat = DecimalFormat("#.##", symbols) // Format to two decimal places
    return decimalFormat.format(doublePrice)
}

