package com.sample.clientserverlocator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class SingleFragmentActivity : AppCompatActivity() {

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)
        val fm = supportFragmentManager
        val fragment: Fragment? = fm.findFragmentById(R.id.main_frame)
        fragment ?: kotlin.run {
            fm.beginTransaction()
                .add(R.id.main_frame, createFragment())
                .commit()
        }
    }
}