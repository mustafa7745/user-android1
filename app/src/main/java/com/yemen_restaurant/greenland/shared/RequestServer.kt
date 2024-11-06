package com.yemen_restaurant.greenland.shared

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.yemen_restaurant.greenland.activities.MainActivity
import com.yemen_restaurant.greenland.models.ErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
class RequestServer(private val activity: ComponentActivity) {

    val login = Login()
    private  val deviceInfoMethod = DeviceInfoMethod()
    private val AppInfoMethod = AppInfoMethod()

    fun request2(body: RequestBody,url:String,onFail:(code:Int,fail:String)->Unit,onSuccess:(data:String)->Unit,) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val okHttpClient = createOkHttpClientWithCustomCert()
                try {
                    if (!isInternetAvailable()) {
                        onFail(0, "لايوجد اتصال بالانترنت")
                    }
                    else{

                        val request = Request.Builder()
                            .url(url)
                            .post(body)
                            .build()
                        val response = okHttpClient.newCall(request).execute()
                        val data = response.body!!.string()
                        println(data)
//                        Log.e("dataa",data)
                        Log.e("dataaUrl",url)

                        when(response.code){
                            200->{
                                if (MyJson.isJson(data)){

                                    onSuccess(data)
                                }
                                else{
                                    onFail(response.code,"not json")
                                }
                            }
                            400->{
                                if (MyJson.isJson(data)){

                                    val ero = MyJson.IgnoreUnknownKeys.decodeFromString<ErrorMessage>(data)
                                    when (ero.code) {
                                        1111 -> {
                                            login.setServerKey("")
                                            val intent = Intent(activity, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            activity.startActivity(intent)
                                            activity.finish()
                                        }
                                        5001 -> {
                                            val intent = Intent(activity, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            intent.putExtra("refreshToken", "1")
                                            activity.startActivity(intent)
                                            activity.finish()
                                        }
                                        5002 -> {
                                            val intent = Intent(activity, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            login.setLoginToken("")
                                            activity.startActivity(intent)
                                            activity.finish()
                                        }
                                    }
                                    onFail(ero.code,ero.message.ar)
                                }
                                else{
                                    onFail(response.code,"not json E")
                                }
                            }
                            else->{
                                onFail(response.code,response.code.toString())
                            }
                        }
                    }



                } catch (e:Exception){
//                onFail(0,e.message.toString())
                    val errorMessage = when (e) {
                        is java.net.SocketTimeoutException -> "Request timed out"
                        is java.net.UnknownHostException -> "Unable to resolve host"
                        is java.net.ConnectException -> "Failed to connect to server"
                        else -> e.message ?: "Unknown error occurred"
                    }
                    onFail(0, "Request failed: $errorMessage")
                    Log.e("request2", "Exception: ", e)
                }
                finally {
                    okHttpClient.connectionPool.evictAll()
                }
            }
        }
    }

    fun createOkHttpClientWithCustomCert(): OkHttpClient {
//        // Load the certificate from raw resources
//        val certInputStream: InputStream = activity.resources.openRawResource(R.raw.isrgrootx1)
//
//        // Create a CertificateFactory
//        val certificateFactory = CertificateFactory.getInstance("X.509")
//
//        // Generate the certificate
//        val certificate = certificateFactory.generateCertificate(certInputStream) as X509Certificate
//
//        // Create a KeyStore and add the certificate
//        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
//            load(null, null)
//            setCertificateEntry("ca", certificate)
//        }
//
//        // Initialize TrustManagerFactory with the KeyStore
//        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//        trustManagerFactory.init(keyStore)
//
//        // Create SSLContext with the custom TrustManager
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(null, trustManagerFactory.trustManagers, null)

        // Build OkHttpClient with the custom SSLContext




        return OkHttpClient.Builder()
//            .sslSocketFactory(sslContext.socketFactory, trustAllCertificates)
//            .sslSocketFactory(sslContext.socketFactory, trustManagerFactory.trustManagers.first() as X509TrustManager)
            .build()
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun getData1(): JsonObject {

        return buildJsonObject {
            put("packageName",AppInfoMethod.getAppPackageName())
//            put("appSha",AppInfoMethod.getAppSha())
                put("appSha", "41:C7:4D:A4:15:03:35:83:84:62:54:9A:22:E6:39:DA:07:F9:60:05:44:CC:4C:5E:A2:02:74:34:BD:3A:E2:73")

            put("appVersion",activity.packageManager.getPackageInfo(activity.packageName, 0).versionCode)
            put("device_type_name","android")
//            put("devicePublicKey",public_key)
            put("deviceId",deviceInfoMethod.getDeviceId())
            put("deviceInfo", deviceInfoMethod.getDeviceInfo().toString())
            put("appDeviceToken",AppInfoMethod.getAppToken())
        }
    }

    fun getData2(): String {
        val text = buildJsonObject {
            put("inputLoginToken", login.getLoginTokenWithDate().token)
        }
        return text.toString()
    }
}