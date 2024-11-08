package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.Duration
import java.time.LocalDateTime

val cartController3 = CartController3()


class DashboardActivity : ComponentActivity() {

    private val homeComponentViewModel: HomeComponentViewModel by viewModels()
    val requestServer = RequestServer(this)

    private val userName = mutableStateOf("")

    val isShowSubProducts = mutableStateOf(false)
    lateinit var groupId: String
    val isShowSearch = mutableStateOf(false)

    private lateinit var updateName2ActivityResult: ActivityResultLauncher<Intent>
//





    @OptIn(ExperimentalMaterial3Api::class)
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
       homeComponentViewModel.checkIfNeedUpdate(requestServer, goToAddName = {goToAddName()})
        setContent {
            GreenlandRestaurantTheme {


                                MainCompose1(padding = 0.dp, stateController = homeComponentViewModel.stateController, activity = this@DashboardActivity, read = { homeComponentViewModel.read(requestServer, goToAddName = { goToAddName() }) }){




//                                    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
//                                        rememberTopAppBarState())
//                                    Scaffold (
//                                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
//                                    ){
//                                        Column(
//                                            modifier = Modifier
//                                                .nestedScroll(scrollBehavior.nestedScrollConnection)
//                                        ) {
//                                            if (scrollBehavior.state.collapsedFraction != 1f)
//                                                Log.e("ff",scrollBehavior.state.collapsedFraction.toString())
//
//                                        }
//
//
//                                    }
                                    ImagesAndName()
//                                    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
//                                        rememberTopAppBarState())
//                                    Log.e("sdsd",scrollBehavior.state.collapsedFraction.toString())
//                                    val (selectedTabIndex, setSelectedTabIndex, listState) = lazyListTabSync(cats.indices.toList())
//                                    Scaffold(
//                                        topBar = {
////                                            // TopAppBar with scroll behavior
////
////
//                                            LargeTopAppBar(
//                                                title = {  Text(text = "mmdf") },
//                                                scrollBehavior = scrollBehavior
//                                            )
//                                        },
//                                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
//
//
//
//                                        ) {
//                                        Column(
//                                            Modifier.fillMaxSize()
////                                          .padding(top = if (scrollBehavior.state.collapsedFraction != 1f) 0.dp else 50.dp)
//
//                                        ) {
////                                            HeaderComponent(modifier = Modifier.height(
////                                                if (scrollBehavior.state.collapsedFraction == 1f) 0.dp else 300.dp),)
//
//
////
//                                        }



//                                    }
//                                    HeaderComponent()



//                                    MyTabBar(categories = cats, selectedTabIndex =selectedTabIndex) { index, _ ->
//                                        GlobalScope.launch {
////                                            stateController.startAud()
////                                            delay(500)
////                                            stateController.successStateAUD()
//                                        }
//                                        setSelectedTabIndex(
//                                            index
//                                        )
//                                    }

//                                    MyLazyList( categories = cats,listState)
//                                    TabSyncComposeScreen(dummyCategories)
//                                    SmartTabsList(
//                                        smartTabsContent = homeComponentViewModel.homeComponent.products,
//                                        isTab = { itemFromContent -> true },
//                                        smartTab = { headerItem, isSelected -> Text(text = headerItem.categoryId)
////                                            Tab(
////                                                content = { Text(text = headerItem.categoryId) },
////                                                selected =  true,
////                                                onClick = {
////
////                                                }
////                                            )
//
//
//                                                   },
//                                        smartItem = { itemFromContent ->
//
//                                            Text(text = itemFromContent.name, modifier = Modifier.height(200.dp))
//                                        }
//                                    )
//                                    val (selectedTabIndex, setSelectedTabIndex, syncedListState) = lazyListTabSync(homeComponentViewModel.homeComponent.products.indices.toList())
//
//                                    ScrollableTabRow(selectedTabIndex) {
//                                        homeComponentViewModel.homeComponent.products.forEachIndexed { index, product ->
//                                            Tab(
//                                                selected = index == selectedTabIndex,
//                                                onClick = {
////                                                    selectedCategory.value = category
//                                                    setSelectedTabIndex(index)
//                                                          },
//                                            ){
//                                                homeComponentViewModel.homeComponent.categories.find { it.id == product.categoryId }
//                                                    ?.let { Text(text = it.name) }
//                                            }
//                                        }
//                                    }


//                                    SynchronizedTabLazyColumnAndRow()
//                                    Categories2()
//                                    HorizontalDivider(thickness = 3.dp , modifier = Modifier.padding(1.dp))
//                                    ImagesAndName(cats,listState)
//                                    LazyColumn(content = {
//                                        if (homeComponentViewModel.homeComponent.offers.isNotEmpty())
//                                        OffersComponents()
//                                        if (homeComponentViewModel.homeComponent.ads.isNotEmpty())
//                                        AdsComponent()
////                                        Categories()
//                                    })

                                    if (isShowSubProducts.value) {
//                                modalList(cart)
                                        modalListV2()
                                    }
                                    if (isShowSearch.value){
                                        SearchDialog(
                                            onDismiss = { isShowSearch.value = false }
                                        )
                                    }

                                }

                        }
        }
    }

    @Composable
    private fun Categories2() {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {

            }) {
                Icon(

                    painter = painterResource(
                        R.drawable.baseline_read_more_24
                    ),
                    contentDescription = ""
                )
            }
            LazyRow(
                modifier = Modifier
                    .height(60.dp)
                    .padding(6.dp),
                content = {
                    items(homeComponentViewModel.homeComponent.categories) {
//                        Tab(selected = selectedCategory.value.id == it.id, onClick = {selectedCategory.value = it }) {
//                            Text(modifier = Modifier.padding(5.dp), text = it.name)
//                        }
//                        Card(
//                            Modifier
//                                .padding(5.dp)
//                                .clickable {
//                                    selectedCategory.value = it
//                                }
//                        ) {
//                            Text(modifier = Modifier.padding(5.dp), text = it.name)
//                        }

                    }
                })
        }
    }


    private fun LazyListScope.Categories() {
        item {
            Text(text = "الاصناف")
        }
        items(homeComponentViewModel.homeComponent.categories.chunked(2)) { rowItems ->
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


    @Composable
    private fun HeaderComponent(modifier :Modifier = Modifier) {
        var count = 3
        if (homeComponentViewModel.homeComponent.ads.isNotEmpty()) count++
        if (homeComponentViewModel.homeComponent.offers.isNotEmpty()) count++
        if (homeComponentViewModel.homeComponent.discounts.isNotEmpty()) count++

        if (homeComponentViewModel.homeComponent.user != null)
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
                        if (homeComponentViewModel.homeComponent.user!!.name2 != null) homeComponentViewModel.homeComponent.user!!.name2.toString()
                        else homeComponentViewModel.homeComponent.user!!.name


                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count), content = {
                        item {
                            CartButton(this@DashboardActivity)
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
                                        isShowSearch.value = true
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
                    })
            }
    }



    private fun goToAddName() {
        val intent = Intent(this@DashboardActivity, AddNameActivity::class.java)
        updateName2ActivityResult.launch(intent)
    }



    @Composable
    private fun OffersContent() {
        Text(text = "العروض")
        HorizontalDivider()
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
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            OfferProductsActivity::class.java
                                        )
                                        intent.putExtra("offer", MyJson.MyJson.encodeToString(item))
                                        startActivity(intent)
                                    }) {
                                CustomImageView(
                                    context = this@DashboardActivity,
                                    imageUrl = item.image,
                                    okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                )
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

    @Composable
    private fun AdsContent() {
        LazyHorizontalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            rows = GridCells.Fixed(1), content = {
                itemsIndexed(homeComponentViewModel.homeComponent.ads) { index, item ->
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


            AnimatedVisibility(visible = isVisible,
//                exit = fadeOut(tween(durationMillis = 500)) + slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500))
            ) {
                Column {
                    HeaderComponent()
                    if (homeComponentViewModel.homeComponent.ads.isNotEmpty())
                    AdsContent()
                }
            }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            IconButton(onClick = {

            }) {
                Icon(

                    painter = painterResource(
                        R.drawable.baseline_read_more_24
                    ),
                    contentDescription = ""
                )
            }
            MyTabBar(categories = homeComponentViewModel.cats, selectedTabIndex =selectedTabIndex,requestServer) { index, _ ->
                setSelectedTabIndex(
                    index
                )
            }
        }




        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                itemsIndexed(homeComponentViewModel.cats) { index, s3 ->
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
                    .height(150.dp)
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
    @OptIn(ExperimentalFoundationApi::class)
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
                val intent = Intent(
                    appComponentActivity,
                    CartActivity::class.java
                )
                appComponentActivity.startActivity(intent)

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

