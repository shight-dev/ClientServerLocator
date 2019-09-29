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
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(){

    val PHONE_KEY = "phone_key"
    val SERVER_API_KEY = "server_api_key"
    val MAC_ADDRESS = "mac_address"
    val SEND_SMS_PERMISSION = 0
    val RECEIVE_SMS_PERMISSION = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.RECEIVE_SMS)
                ,RECEIVE_SMS_PERMISSION)
        }
        val pref = activity?.getSharedPreferences("appData", MODE_PRIVATE)
        editPhone.setText(pref?.getString(PHONE_KEY, ""))
        editApiKey.setText(pref?.getString(SERVER_API_KEY, "Api key"))

        editPhone.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val prefPhone = activity?.getSharedPreferences("appData", MODE_PRIVATE)
                val editor = prefPhone?.edit()
                editor?.putString(PHONE_KEY,s.toString())
                editor?.apply()
            }

        })

        editApiKey.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val prefServerApi = activity?.getSharedPreferences("appData", MODE_PRIVATE)
                val editor = prefServerApi?.edit()
                editor?.putString(SERVER_API_KEY,s.toString())
                editor?.apply()
            }
        })

        macAddressEdit.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val prefServerApi = activity?.getSharedPreferences("appData", MODE_PRIVATE)
                val editor = prefServerApi?.edit()
                editor?.putString(MAC_ADDRESS,s.toString())
                editor?.apply()
            }
        })

        sendBtn.setOnClickListener {

            //TODO fix activity !!
            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.SEND_SMS)
                ,SEND_SMS_PERMISSION)
            }
            else{
                sendTextMessage()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == SEND_SMS_PERMISSION){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                sendTextMessage()
            }
        }
    }

    private fun sendTextMessage(){
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(editPhone.text.toString(),null,
            editApiKey.text.toString()+macAddressEdit.text.toString(), null, null)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment()
    }
}