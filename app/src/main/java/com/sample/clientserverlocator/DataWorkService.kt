package com.sample.clientserverlocator

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.text.format.Formatter.formatIpAddress
import android.net.wifi.WifiInfo
import androidx.core.content.ContextCompat.getSystemService
import android.net.wifi.WifiManager
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.format.Formatter
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.net.InetAddress
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class DataWorkService : IntentService("LocatorService") {
    override fun onHandleIntent(intent: Intent?) {
        val key:String = getSharedPreferences("appData", Context.MODE_PRIVATE).getString("server_api_key","")?:""
        if(!key.contentEquals("")) {
            val body = intent?.getStringExtra("body")
            if (body?.startsWith(key, true) == true){
                //TODO check network
                val mac = body.substringAfter(key)
                findMac(mac)
                val smsManager =  SmsManager.getDefault()
            }
        }
    }

    fun findMac(mac:String):Boolean{
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val wm = this.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val connectionInfo = wm.connectionInfo
        val ipAddress = connectionInfo.ipAddress
        val ipString = Formatter.formatIpAddress(ipAddress)

        val prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1)

        val mutableList = mutableListOf<String>()
        for (i in 0..254) {
            val testIp = prefix + i.toString()

            val address = InetAddress.getByName(testIp)
            val reachable = address.isReachable(1000)
            if(reachable){
                val hostName = address.canonicalHostName
                mutableList.add(testIp)
            }
        }
        return false
    }

    companion object{
        fun newIntent(context: Context, body:String):Intent{
            val intent = Intent(context,DataWorkService::class.java)
            intent.putExtra("body", body)
            return intent
        }
    }
}