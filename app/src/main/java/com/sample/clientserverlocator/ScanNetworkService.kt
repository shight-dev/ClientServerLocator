package com.sample.clientserverlocator

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.net.wifi.WifiManager
import android.telephony.SmsManager
import android.text.format.Formatter
import java.io.BufferedReader
import java.io.FileReader
import java.lang.Exception
import java.net.InetAddress

private const val ACTION_SCAN = "com.sample.clientserverlocator.action.scan"
private const val BODY = "com.sample.clientserverlocator.extra.body"

class ScanNetworkService : IntentService("ScanNetworkService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_SCAN -> {
                val body = intent.getStringExtra(BODY)
                handleActionScan(body)
            }
        }
    }

    private fun handleActionScan(body: String) {
        val key:String = getSharedPreferences(getString(R.string.app_data), Context.MODE_PRIVATE).getString(getString(
                    R.string.server_api_key),"")?:""
        if(!key.contentEquals("")) {
            if (body.startsWith(key, true)){
                val mac = body.substringAfter(key)
                val macList = scanNetwork()
                val smsManager = SmsManager.getDefault()
                if(macList.contains(mac)){
                    smsManager.sendTextMessage(body, null,getString(R.string.mac_was_found), null, null)
                }
                else{
                    smsManager.sendTextMessage(body, null, getString(R.string.mac_was_not_found), null, null)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun startActionScan(context: Context, body: String) {
            val intent = Intent(context, ScanNetworkService::class.java).apply {
                action = ACTION_SCAN
                putExtra(BODY, body)
            }
            context.startService(intent)
        }
    }

    private fun scanNetwork():MutableList<String> {
        val wm = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val mutableIpList = mutableListOf<String>()
        val mutableMacList = mutableListOf<String>()
        if(wm.isWifiEnabled) {
            val connectionInfo = wm.connectionInfo
            val ipAddress = connectionInfo.ipAddress
            val ipString = Formatter.formatIpAddress(ipAddress)

            val prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1)
            for (i in 0..254) {
                val testIp = prefix + i.toString()

                val address = InetAddress.getByName(testIp)
                val reachable = address.isReachable(100)
                if (reachable) {
                    val s = getMacAddressFromIP(testIp)
                    mutableMacList.add(s)
                    mutableIpList.add(testIp)
                }
            }
        }
        return mutableMacList
    }

    private fun getMacAddressFromIP(ipFinding: String): String {
        val bufferedReader: BufferedReader?
        try {
            bufferedReader = BufferedReader (FileReader ("/proc/net/arp"))
            var line:String? = bufferedReader.readLine()
            while ((line) != null) {

                val splitted = line.split(Regex("\\s+"))
                if (splitted.size >= 4) {
                    val ip:String = splitted [0]
                    val mac:String = splitted [3]
                    if (mac.matches(Regex("..:..:..:..:..:.."))) {

                        if (ip.equals(ipFinding,true)) {
                            return mac
                        }
                    }
                }
                line = bufferedReader.readLine()
            }
        } catch (e: Exception) {
            return getString(R.string.no_data)
        }
        return getString(R.string.no_data)
    }
}