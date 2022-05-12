package com.sample.slidedrawerlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import com.sample.slidedrawerlayout.widget.SlideDrawerContainer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val drawerLayout = findViewById<SlideDrawerContainer>(R.id.slideLayout)
        findViewById<TextView>(R.id.open).setOnClickListener {
            drawerLayout.open()
        }

        findViewById<TextView>(R.id.close).setOnClickListener {
            drawerLayout.close()
        }
    }
}