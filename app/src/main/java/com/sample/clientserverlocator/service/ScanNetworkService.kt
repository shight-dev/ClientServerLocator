package com.sample.clientserverlocator.service

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.net.wifi.WifiManager
import android.telephony.SmsManager
import com.sample.clientserverlocator.APP_DATA
import com.sample.clientserverlocator.R
import com.sample.clientserverlocator.SERVER_API_KEY
import java.io.BufferedReader
import java.io.FileReader
import java.lang.Exception
import java.net.InetAddress
import java.math.BigInteger


private const val ACTION_SCAN = "com.sample.clientserverlocator.action.scan"
private const val BODY = "com.sample.clientserverlocator.extra.body"
private const val PHONE = "com.sample.clientserverlocator.extra.phone"

private const val WAITING_TIME = 100

class ScanNetworkService : IntentService("ScanNetworkService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_SCAN -> {
                val body = intent.getStringExtra(BODY)
                val phone = intent.getStringExtra(PHONE)
                handleActionScan(body, phone)
            }
        }
    }

    private fun handleActionScan(body: String, phone: String) {
        val key: String =
            getSharedPreferences(APP_DATA, Context.MODE_PRIVATE).getString(
                SERVER_API_KEY, ""
            ) ?: ""
        if (!key.contentEquals("")) {
            if (body.startsWith(key, true)) {
                val mac = body.substringAfter(key)
                val macList = scanNetwork()
                val smsManager = SmsManager.getDefault()
                if (macList.any { macItem ->
                        macItem.equals(mac, true)
                    }) {
                    smsManager.sendTextMessage(
                        phone,
                        null,
                        getString(R.string.mac_was_found),
                        null,
                        null
                    )
                } else {
                    smsManager.sendTextMessage(
                        phone,
                        null,
                        getString(R.string.mac_was_not_found),
                        null,
                        null
                    )
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun startActionScan(context: Context, body: String, phone: String) {
            val intent = Intent(context, ScanNetworkService::class.java).apply {
                action = ACTION_SCAN
                putExtra(BODY, body)
                putExtra(PHONE, phone)
            }
            context.startService(intent)
        }
    }

    private fun scanNetwork(): MutableList<String> {
        val wm = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val mutableMacList = mutableListOf<String>()
        if (wm.isWifiEnabled) {
            val connectionInfo = wm.connectionInfo

            val ipAddress = BigInteger.valueOf(connectionInfo.ipAddress.toLong()).toByteArray()
            ipAddress.reverse()
            val ipString = InetAddress.getByAddress(ipAddress).hostAddress

            val prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1)
            for (i in 0..254) {
                val testIp = prefix + i.toString()

                val address = InetAddress.getByName(testIp)
                val reachable = address.isReachable(WAITING_TIME)
                if (reachable) {
                    val mac = getMacAddressFromIP(testIp)
                    mac?.let {
                        mutableMacList.add(mac)
                    }
                }
            }
        }
        return mutableMacList
    }

    private fun getMacAddressFromIP(ipFinding: String): String? {
        val bufferedReader: BufferedReader?
        try {
            bufferedReader = BufferedReader(FileReader("/proc/net/arp"))
            var line: String? = bufferedReader.readLine()
            while ((line) != null) {

                val splitted = line.split(Regex("\\s+"))
                if (splitted.size >= 4) {
                    val ip: String = splitted[0]
                    val mac: String = splitted[3]
                    if (mac.matches(Regex("..:..:..:..:..:.."))) {

                        if (ip.equals(ipFinding, true)) {
                            return mac
                        }
                    }
                }
                line = bufferedReader.readLine()
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }
}
