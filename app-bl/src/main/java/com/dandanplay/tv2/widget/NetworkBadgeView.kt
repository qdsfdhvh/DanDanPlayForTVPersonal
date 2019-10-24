package com.dandanplay.tv2.widget

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.dandanplay.tv2.R

private const val IS_NONE = 0
private const val IS_WIFI = 2
private const val IS_ETH = 3
private const val IS_MOBILE = 4

class NetworkBadgeView : ImageView {

    private var connectType = 0

    private val networkReceiver = object : NetworkBroadcastReceiver() {
        override fun setRssiLevel(rssi: Int) {
            this@NetworkBadgeView.setRssiLevel(rssi)
        }
    }

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init()

    }

    private fun init() {
        val manager = (context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager) ?: return

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getTypeFor29(manager)
        } else {
            getTypeFor18(manager)
        }

        when(type) {
            IS_WIFI -> {
                visibility = View.VISIBLE
                setImageResource(R.drawable.level_main_network)
                refreshRssi()
            }
            IS_ETH -> {
                visibility = View.VISIBLE
                setImageResource(R.drawable.ic_network_eth)
            }
            IS_MOBILE -> {

            }
            IS_NONE -> {

            }
        }
        connectType = type
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun getTypeFor29(manager: ConnectivityManager): Int {
        val nw = manager.activeNetwork ?: return IS_NONE
        val actNw = manager.getNetworkCapabilities(nw) ?: return IS_NONE
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> IS_WIFI
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> IS_ETH
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> IS_MOBILE
            else -> IS_NONE
        }
    }

    private fun getTypeFor18(manager: ConnectivityManager): Int {
        val info = manager.activeNetworkInfo
        if (info == null || !info.isConnected) return IS_NONE
        return when(info.type) {
            ConnectivityManager.TYPE_WIFI -> IS_WIFI
            ConnectivityManager.TYPE_ETHERNET -> IS_ETH
            ConnectivityManager.TYPE_MOBILE -> IS_MOBILE
            else -> IS_NONE
        }
    }

    private fun setRssiLevel(rssi: Int) {
        if (connectType == IS_WIFI) {
            val level = WifiManager.calculateSignalLevel(rssi, 4)
            setImageLevel(level)
        }
    }

    private fun refreshRssi() {
        val wifiInfo = (context.applicationContext.getSystemService(Context.WIFI_SERVICE)
                as WifiManager).connectionInfo
        val rssi = wifiInfo?.rssi ?: -100
        setRssiLevel(rssi)
    }

    override fun onAttachedToWindow() {
        networkReceiver.registerReceiver(context)
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        networkReceiver.unregisterReceiver(context)
        super.onDetachedFromWindow()
    }

}

abstract class NetworkBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras ?: return
        if (bundle.containsKey(WifiManager.EXTRA_NEW_RSSI)) {
            setRssiLevel(bundle.getInt(WifiManager.EXTRA_NEW_RSSI))
        }
    }

    abstract fun setRssiLevel(rssi: Int)
}

private fun NetworkBroadcastReceiver.registerReceiver(context: Context) {
    context.registerReceiver(this, IntentFilter(WifiManager.RSSI_CHANGED_ACTION))
}

private fun NetworkBroadcastReceiver.unregisterReceiver(context: Context) {
    context.unregisterReceiver(this)
}