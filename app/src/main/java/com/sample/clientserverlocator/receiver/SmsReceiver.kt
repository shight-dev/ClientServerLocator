package com.sample.clientserverlocator.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.sample.clientserverlocator.DataWorkService

class SmsReceiver: BroadcastReceiver() {
    val ACTION = "android.provider.Telephony.SMS_RECEIVED"

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action?.compareTo(ACTION,true)==0){
            val pduArray = intent.extras?.get("pdus")
            if(pduArray is Array<*>){
                val messageList:MutableList<SmsMessage> = mutableListOf()
                for (data in pduArray){
                    if(data is ByteArray){
                        messageList.add(SmsMessage.createFromPdu(data))
                    }
                }
                val body : StringBuilder = StringBuilder()
                messageList.forEach {
                    body.append(it.messageBody)
                }
                context?.startService(DataWorkService.newIntent(context, body.toString()))
            }

        }
    }
}