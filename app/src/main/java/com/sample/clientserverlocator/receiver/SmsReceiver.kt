package com.sample.clientserverlocator.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsMessage
import com.sample.clientserverlocator.service.ScanNetworkService

const val ACTION = "android.provider.Telephony.SMS_RECEIVED"
const val INTENT_FILTER = "android.provider.Telephony.SMS_RECEIVED"

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action?.compareTo(ACTION, true) == 0) {
            val pduArray = intent.extras?.get("pdus")
            if (pduArray is Array<*>) {
                val messageList: MutableList<SmsMessage> = mutableListOf()
                for (data in pduArray) {
                    if (data is ByteArray) {
                        //TODO fix
                        messageList.add(SmsMessage.createFromPdu(data))
                    }
                }
                val phone: String? = messageList.getOrNull(0)?.displayOriginatingAddress
                val body: StringBuilder = StringBuilder()
                messageList.forEach {
                    body.append(it.messageBody)
                }
                context?.let {
                    phone?.let {
                        ScanNetworkService.startActionScan(context, body.toString(), phone)
                    }
                }
            }

        }
    }

    companion object {
        fun createIntentFilter(): IntentFilter {
            return IntentFilter(INTENT_FILTER)
        }
    }
}