package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.storage.ProductsStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import com.yemen_restaurant.greenland.viewModels.HomeComponentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardActivity : ComponentActivity() {
//    private val homeComponentViewModel: HomeComponentViewModel by viewModels()
//    val requestServer = RequestServer(this)

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

//        homeComponentViewModel.productsStorage= ProductsStorage(this)
//        homeComponentViewModel.read(requestServer)

        lifecycleScope.launch {

            delay(3000)
            goToMain()
        }

        setContent {
            GreenlandRestaurantTheme {

                AsyncImage(
                    model = R.drawable.onboard,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    private fun goToMain() {
        val intent =
            Intent(this@OnboardActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}