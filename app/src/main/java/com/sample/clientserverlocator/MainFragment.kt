package com.sample.clientserverlocator

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(){

    val PHONE_KEY = "phone_key"
    val SERVER_API_KEY = "server_api_key"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onStart() {
        super.onStart()
        val pref = activity?.getPreferences(MODE_PRIVATE)
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
                val prefPhone = activity?.getPreferences(MODE_PRIVATE)
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
                val prefServerApi = activity?.getPreferences(MODE_PRIVATE)
                val editor = prefServerApi?.edit()
                editor?.putString(SERVER_API_KEY,s.toString())
                editor?.apply()
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment()
    }
}