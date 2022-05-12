package com.sample.slidedrawerlayout.touch

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.sample.slidedrawerlayout.databinding.ActivityTouchEventBinding
import timber.log.Timber

class TouchEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTouchEventBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTouchEventBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // down = 0
        // move = 2
        // up = 1

//        binding.vg11.setOnClickListener {
//            Timber.d("->OnClick!!")
//        }

        binding.vg22.setOnClickListener{
            Timber.d("->vg22.OnClick!!")
        }

//        binding.vg0.setOnTouchListener { view, ev ->
//            Timber.d("外.onTouchEvent->${ev.actionMasked}")
//            return@setOnTouchListener false
//        }

//        binding.vg1.setOnTouchListener { view, ev ->
//            Timber.d("Vg1.onTouchEvent->${ev.actionMasked}")
//            return@setOnTouchListener false
//        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("${switchEvent(ev.actionMasked)} -> Activity.dispatchTouchEvent")
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("${switchEvent(ev.actionMasked)} -> Activity.onTouchEvent")
        return super.onTouchEvent(ev)
    }
}