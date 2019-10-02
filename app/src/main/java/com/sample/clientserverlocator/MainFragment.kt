package com.sample.clientserverlocator

import android.Manifest
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sample.clientserverlocator.receiver.SmsReceiver
import kotlinx.android.synthetic.main.fragment_main.*

const val CREATED = "CREATED"
const val PHONE_KEY = "phone_key"
const val MAC_ADDRESS = "mac_address"
const val SERVER_API_KEY = "server_api_key"
const val DEFAULT_MAC_ADDRESS = "00:00:00:00:00:00"
const val DEFAULT_PHONE = ""
const val DEFAULT_API_KEY = "DEFAULT"
const val SEND_SMS_PERMISSION = 0
const val RECEIVE_SMS_PERMISSION = 10

class MainFragment : Fragment() {

    private val smsReceiver = SmsReceiver()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onStart() {
        super.onStart()
        val context = activity
        context?.let {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context, arrayOf(Manifest.permission.RECEIVE_SMS)
                    , RECEIVE_SMS_PERMISSION
                )
            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context, arrayOf(Manifest.permission.SEND_SMS)
                    , SEND_SMS_PERMISSION
                )
            }


            val pref = context.getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE)
            pref?.let {

                //запись при первом старте приложения
                val created = pref.getString(CREATED, "")
                if (created?.contentEquals("") == true) {
                    val editor = pref.edit()
                    editor.putString(PHONE_KEY, DEFAULT_PHONE)
                    editor.putString(SERVER_API_KEY, DEFAULT_API_KEY)
                    editor.putString(MAC_ADDRESS, DEFAULT_MAC_ADDRESS)
                    editor.putString(CREATED, CREATED)
                    editor.apply()
                }

                editPhone.setText(pref.getString(PHONE_KEY, DEFAULT_PHONE))
                editApiKey.setText(pref.getString(SERVER_API_KEY, DEFAULT_API_KEY))
                macAddressEdit.setText(pref.getString(MAC_ADDRESS, DEFAULT_MAC_ADDRESS))

                editPhone.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        //do nothing
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        //do nothing
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val prefPhone =
                            context.getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE)
                        val editor = prefPhone?.edit()
                        editor?.putString(PHONE_KEY, s.toString())
                        editor?.apply()
                    }

                })

                editApiKey.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        //do nothing
                    }

                    override fun afterTextChanged(s: Editable?) {
                        //do nothing
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val prefServerApi =
                            context.getSharedPreferences(getString(R.string.app_data), MODE_PRIVATE)
                        val editor = prefServerApi?.edit()
                        editor?.putString(SERVER_API_KEY, s.toString())
                        editor?.apply()
                    }
                })

                macAddressEdit.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        //do nothing
                    }

                    override fun afterTextChanged(s: Editable?) {
                        //do nothing
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val prefServerApi =
                            activity?.getSharedPreferences(
                                getString(R.string.app_data),
                                MODE_PRIVATE
                            )
                        val editor = prefServerApi?.edit()
                        editor?.putString(MAC_ADDRESS, s.toString())
                        editor?.apply()
                    }
                })

                sendBtn.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context, arrayOf(Manifest.permission.SEND_SMS)
                            , SEND_SMS_PERMISSION
                        )
                    } else {
                        sendTextMessage()
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SEND_SMS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    activity?.finish()
                }
            }
            RECEIVE_SMS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    activity?.registerReceiver(smsReceiver, SmsReceiver.createIntentFilter())
                } else {

                    activity?.finish()
                }
            }
        }
    }

    private fun sendTextMessage() {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(
            editPhone.text.toString(), null,
            editApiKey.text.toString() + macAddressEdit.text.toString(), null, null
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment()
    }
}