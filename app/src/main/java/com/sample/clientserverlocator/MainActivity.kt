package com.sample.clientserverlocator

import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return MainFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)
    }
}
