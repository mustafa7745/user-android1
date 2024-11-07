package com.yemen_restaurant.greenland.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PinConfig
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody

class ChangeMapLocationActivity : ComponentActivity() {
    var long = mutableStateOf("0")
    var lat = mutableStateOf("0")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val latString = intent.getStringExtra("lat")
        val longString = intent.getStringExtra("long")
        if (latString != null) {
            lat.value = latString
            long.value = longString!!
        }
        val pinConfig = PinConfig.builder()
            .build()
        setContent{
            GreenlandRestaurantTheme{

                var location = LatLng(lat.value.toDouble(),long.value.toDouble() )
                val markerState = rememberMarkerState(position = location)
                var cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 15f)
                }


                if(cameraPositionState.isMoving){
                    location = LatLng(cameraPositionState.position.target.latitude, cameraPositionState.position.target.longitude)
                    markerState.position = LatLng(cameraPositionState.position.target.latitude, cameraPositionState.position.target.longitude)
                }


                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    AdvancedMarker(
                        state = markerState,
                        pinConfig = pinConfig
                    )
                }

                    Box(
                        Modifier.fillMaxSize(),
                    ){
                        Button(
                            onClick = {
                                long.value = cameraPositionState.position.target.longitude.toString()
                                lat.value = cameraPositionState.position.target.latitude.toString()

                                val data1 = Intent()
                                data1.putExtra("lat",lat.value)
                                data1.putExtra("long",long.value)
                                setResult(RESULT_OK,data1)
                                finish()
                            },
                            modifier = Modifier
                                .fillMaxWidth().align(Alignment.BottomCenter)
                                .padding(8.dp)
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
}