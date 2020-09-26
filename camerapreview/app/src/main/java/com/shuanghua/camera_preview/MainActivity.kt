package com.shuanghua.camera_preview

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var surfaceView: PreviewView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surfaceView = findViewById(R.id.surface_view)
        imageView = findViewById(R.id.image_view)
        findViewById<Button>(R.id.button).setOnClickListener {
            surfaceView.startPreview()
        }

        findViewById<Button>(R.id.button_next).setOnClickListener {
            surfaceView.nextFrame()
        }

        surfaceView.setImageView(imageView)
    }
}