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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.LocationTypeModel
import com.yemen_restaurant.greenland.models.UserLocationModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.UserStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody


class AddLocationActivity : ComponentActivity() {
    val stateController = StateController()
    private val requestServer = RequestServer(this)
    val openDialog =  mutableStateOf(false)
    var locationTypes = mutableStateOf<List<LocationTypeModel>>(listOf())
    val selectedLocationType =   mutableStateOf<LocationTypeModel?>(null)

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    var long = mutableStateOf("0")
    var lat = mutableStateOf("0")

    val location = mutableStateOf(LatLng(lat.value.toDouble(),long.value.toDouble() ))

//    val singaporeMarkerState = mutableStateOf(MarkerState(position = LatLng(lat.value.toDouble(), long.value.toDouble())))


    val street = mutableStateOf("")
    val nearto = mutableStateOf("")
    val contact = mutableStateOf("")

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

private fun add() {
    stateController.isErrorAUD.value = false
    stateController.isLoadingAUD.value = true
    stateController.errorAUD.value = ""
    val data3 = buildJsonObject {
        put("tag", "add")
        put("inputUserLocationCity", "صنعاء")
        put("inputUserLocationStreet", street.value.trim())
        put("inputUserLocationLatLong", (lat.value +","+ long.value).trim())
        put("inputUserLocationNearTo", nearto.value.trim())
        put("inputUserLocationContactPhone", contact.value.trim())
        if (selectedLocationType.value != null) {
            put("inputLocationTypeId", selectedLocationType.value!!.id)
        }
    }
    val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("data1", requestServer.getData1().toString())
        .addFormDataPart("data2", requestServer.getData2())
        .addFormDataPart("data3", data3.toString())
        .build()

    requestServer.request2(body1, Urls.userLocationUrl, { code, it ->
        stateController.errorStateAUD(it)
    }) {
            stateController.successStateAUD()
            MyJson.IgnoreUnknownKeys.decodeFromString<UserLocationModel>(it)
            runOnUiThread {
                Toast.makeText(this@AddLocationActivity,"تمت الاضافه بنجاح",Toast.LENGTH_SHORT).show()
            }
            val data1 = Intent()
            data1.putExtra("location",it)
            setResult(RESULT_OK,data1)
            finish()
    }
}
    private fun readLocationTypes() {
        stateController.startAud()
        val data3 = buildJsonObject {
            put("tag", "read")
        }
        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.locationTypesUrl, { code, it ->

            stateController.errorStateAUD(it)
        }) {
                stateController.successStateAUD()
                locationTypes.value =
                    MyJson.IgnoreUnknownKeys.decodeFromString(
                        it
                    )
//            expanded.value = true
                openDialog.value = true

        }


    }
    val isGpsEnabled = mutableStateOf(false)
    val isGetLastLocation = mutableStateOf(false)
    val isPermissionAllow = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIsPermissionGranted()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGpsEnabled.value = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)


        setContent {
            GreenlandRestaurantTheme {
                MainCompose2(padding = 0.dp, stateController =stateController , activity = this@AddLocationActivity ) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                        if (isPermissionAllow.value) {
                            if (!isGpsEnabled.value) {
                                Button(onClick = {
                                    requestEnableGPS()
                                }) {
                                    Text(text = "تفعيل الموقع",fontFamily = FontFamily(
                                        Font(R.font.bukra_bold)))
                                }
                            } else {
                                LaunchedEffect(null){
                                    getLocation()
                                }
                                if (!isGetLastLocation.value){
                                    Button(onClick = {
                                        getLocation()
                                    }) {
                                        Text(text = "تأكيد الموقع",fontFamily = FontFamily(
                                            Font(R.font.bukra_bold)
                                        )
                                        )
                                    }
                                }else{
                                    ContentAdd()
                                }
                            }
                        } else {
                            Text(text = "لم يتم منح صلاحية الوصول الى الموقع")
                            Button(onClick = {
                                checkIsPermissionGranted()
                            }) {
                                Text(text = "طلب الوصول الى الموقع",fontFamily = FontFamily(
                                    Font(R.font.bukra_bold)))
                            }
                        }
                    }

                }
            }
        }
    }

//    val singapore = LatLng(lat.value.toDouble(),long.value.toDouble() )
    val editPositionMode = mutableStateOf( false)

    val makerState = mutableStateOf(MarkerState(position =location.value ))
//    val cameraPositionState = mutableStateOf(CameraPosition.fromLatLngZoom(location.value, 15f))

    val cameraPositionState = mutableStateOf(
        CameraPositionState(
            position = CameraPosition.fromLatLngZoom(location.value, 16f)
        )
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ContentAdd() {
//        val makerState = rememberMarkerState(position =location.value )
        val userStorage = UserStorage()
        contact.value = userStorage.getUser().phone
        val fontSize = 10.sp
        DialogTypes()
        LazyColumn(
            Modifier.fillMaxSize()
        ) {

           item {
               Card(Modifier.padding(8.dp).height(250.dp).fillMaxWidth()) {
                   Box (Modifier.fillMaxSize()){
                       GoogleMap(
                           modifier = Modifier.fillMaxSize(),
                           cameraPositionState = cameraPositionState.value
                       ) {
                           Marker(
                               state = makerState.value,
                           )
                       }
                       Row (
                           Modifier.align(Alignment.TopEnd).padding(5.dp).border(
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
                       ){
                           IconButton(onClick = {

                               getLocation()
                           }){
                               Icon(
                                   modifier =  Modifier.padding(5.dp),
                                   imageVector = Icons.Outlined.Place,
                                   contentDescription = "",
                                   tint = MaterialTheme.colorScheme.primary
                               )
                           }
                           IconButton(onClick = {
                               val intent = Intent(
                                   this@AddLocationActivity,
                                   ChangeMapLocationActivity::class.java
                               )
                               intent.putExtra("lat",lat.value)
                               intent.putExtra("long",long.value)


                               activityResultLauncherChangeLocationMap.launch(intent)
                           }){
                               Icon(
                                   modifier =  Modifier.padding(5.dp),
                                   imageVector = Icons.Outlined.Edit,
                                   contentDescription = "",
                                   tint = MaterialTheme.colorScheme.primary
                               )
                           }
                       }
                   }
               }
           }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "إضافة تفاصيل الموقع",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6200EE),
                            fontFamily =  FontFamily(
                                Font(R.font.bukra_bold)))
                    )


                    OutlinedTextField(
                        value = "صنعاء",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth()
                    )

                    var isValidStreet by remember { mutableStateOf(true) }
                    if (!isValidStreet) {
                        Text(
                            text = "يحب الايزيد طول الحقل اكثر من 20 حرف",
                            color = Color.Red,
                            fontSize = fontSize,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

//                    chooseType()
                    Button(
                        onClick = {
                            if (locationTypes.value.isEmpty())
                                readLocationTypes()
                            else
                                openDialog.value=true

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White, // Background color
                        ),
                        modifier = Modifier
                            .fillMaxWidth().border(
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
                    ) {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = selectedLocationType.value?.name ?: "اختر نوع العنوان",
                                color = MaterialTheme.colorScheme.primary, fontSize = 18.sp,fontFamily = FontFamily(
                                    Font(R.font.bukra_bold)),

                                )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Dropdown Arrow",
                                tint = MaterialTheme.colorScheme.onSurface
                            )

                        }

                    }

                    var isValidNearTo by remember { mutableStateOf(true) }
                    if (!isValidNearTo) {

                        Text(
                            text = "يحب الايزيد طول الحقل اكثر من 100 حرف",
                            color = Color.Red,
                            fontSize = fontSize,
                            modifier = Modifier.padding(start = 4.dp))


                    }

                    OutlinedTextField(
                        value = nearto.value,
                        onValueChange = { nearto.value = it
                            isValidNearTo = nearto.value.length < 100
                        },
                        label = { Text(if (selectedLocationType.value  != null && selectedLocationType.value!!.name.contains("سيارة")) "رقم السيارة ونوعها ولونها" else "بالقرب من / مركز معروف / محل / منزل", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        singleLine = false,
                        isError = !isValidNearTo,
                    )

                    OutlinedTextField(
                        value = street.value,
                        onValueChange = { s->street.value = s
                            isValidStreet = street.value.length < 20
                        },
                        label = { Text("الشارع", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = !isValidStreet,
                    )



                    var isValidPhone by remember { mutableStateOf(true) }
                    if (!isValidPhone) {
                        Text(
                            fontSize = fontSize,
                            text = "الرجاء إدخال رقم هاتف صحيح (يجب أن يتكون من 9 أرقام ويبدأ بـ 70, 71, 73, 77, أو 78)",
                            color = Color.Red,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = contact.value,
                        onValueChange = {
                            contact.value = it
                            isValidPhone = it.matches(Regex("^7[0|1|3|7|8][0-9]{7}$"))
                        },
                        label = { Text("رقم الهاتف للتواصل معه") },
                        isError = !isValidPhone,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )




                    Button(
                        enabled = isValidPhone && contact.value.isNotEmpty() && nearto.value.isNotEmpty() && street.value.isNotEmpty() && isValidNearTo && isValidStreet && selectedLocationType.value != null,
                        onClick = { add() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "حفظ", color = Color.White, fontSize = 18.sp,fontFamily = FontFamily(
                            Font(R.font.bukra_bold)))
                    }
                }
            }
        }
    }


    private val activityResultLauncherChangeLocationMap = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.e("result Avt", result.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val locationLat = data.getStringExtra("lat")
                val locationLong = data.getStringExtra("long")
                if (locationLat != null) {
                    setLocationInMap(locationLat,locationLong!!)
                }
            }
        } else {

        }
    }
    private fun getLocation(){
        if (ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[0]
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[1]
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[2]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
           checkIsPermissionGranted()
        }else{
            stateController.startAud()
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { myLocation: Location?->
                if (myLocation != null) {
                    stateController.successStateAUD()
                    isGetLastLocation.value = true
                    setLocationInMap(myLocation.latitude.toString(),myLocation.longitude.toString())

                }else{
                    stateController.errorStateAUD("1")
//                    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
//                        .setFastestInterval(5000)
//                        .build()
                    val locationRequest = LocationRequest.create().apply {
                        interval = 10000 // Update interval in milliseconds
                        fastestInterval = 5000 // Fastest update interval in milliseconds
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Use high accuracy
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, null)
                }
            }.addOnFailureListener {
                stateController.errorStateAUD("2")
            }
        }
    }

    private fun setLocationInMap(latitude: String,longitude:String) {
        long.value = longitude
        lat.value = latitude
        location.value = LatLng(lat.value.toDouble(), long.value.toDouble())
        makerState.value.position = LatLng(lat.value.toDouble(), long.value.toDouble())
        cameraPositionState.value.position = CameraPosition.fromLatLngZoom(location.value, 16f)
    }

    //
//    private val locationRequest = LocationRequest.create().apply {
//        interval = 5000 // Update interval in milliseconds (e.g., every 5 seconds)
//    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // Handle location updates here
            val locations = locationResult.locations

            for (location in locations) {
                // Process each location
                println("Received location: Lat=${location.latitude}, Long=${location.longitude}")
            }
        }
    }
    private fun checkIsPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[0]
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[1]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isPermissionAllow.value = false
            fineLocationActivityResultLauncher.launch(permissions[0])
            fineLocationActivityResultLauncher.launch(permissions[1])
        } else {
            isPermissionAllow.value = true
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    private fun requestEnableGPS() {
        val locationRequest = LocationRequest.create()
        //
        //
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())//Checksettings with building a request
        task.addOnSuccessListener { locationSettingsResponse ->
            getLocation()
            isGpsEnabled.value = true
            Log.d(
                "Location",
                "enableLocationRequest: LocationService Already Enabled"
            )
        }
        task.addOnFailureListener { exception ->
            isGpsEnabled.value = false
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution)
                            .build()//Create the request prompt
                    gpsActivityResultLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private val gpsActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        Log.e("result ", result.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            isGpsEnabled.value = true
            getLocation()
        } else {

        }
    }
    private val fineLocationActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isPermissionAllow.value = true

        } else {
            // Permission denied, handle accordingly (e.g., show an explanation or disable the feature)
        }
    }

    @Composable
    fun DialogTypes() {
        if (openDialog.value) {
            Dialog(
                onDismissRequest = { openDialog.value = false },
               content = {
                   Card(Modifier.fillMaxWidth()) {
                       Text(text = "نوع العنوان" ,Modifier.padding(10.dp))
                       HorizontalDivider()
                       LazyColumn(content = {
                           itemsIndexed(locationTypes.value){ _, s ->
                               Button(
                                   onClick = {
                                             selectedLocationType.value = s
                                       openDialog.value = false
                                             },
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .height(50.dp)
                                       .padding(5.dp),
                                   shape = RoundedCornerShape(8.dp)
                               ) {
                                   Text(text = s.name, color = Color.White, fontSize = 18.sp,fontFamily = FontFamily(
                                       Font(R.font.bukra_bold)))
                               }
                           }
                       })
                   }
               }
            )
        }
    }
}

