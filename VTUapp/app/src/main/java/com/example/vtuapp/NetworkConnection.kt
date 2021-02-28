package com.example.vtuapp

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData

@Suppress("DEPRECATION")
class NetworkConnection(private val context: Context): LiveData<Boolean>() {
    private var connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    private lateinit var networkCallBack: ConnectivityManager.NetworkCallback


    override fun onActive() {
        super.onActive()
        updateConnection()

        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->{
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())

            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->{
                lollipopNetworkRequest()
            }else ->{
            context.registerReceiver(
                    networkReciever,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        }
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopNetworkRequest(){
        val reqBuilder = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)



        connectivityManager.registerNetworkCallback(
                reqBuilder.build(),
                connectivityManagerCallback()

        )
    }

    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            networkCallBack = object: ConnectivityManager.NetworkCallback() {

                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(false)
                }


                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    postValue(true)
                }
            }

            return networkCallBack;
        }else{
            throw IllegalAccessError("Error !")
        }
    }

    private val networkReciever = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }

    }
    private fun updateConnection(){
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }
}