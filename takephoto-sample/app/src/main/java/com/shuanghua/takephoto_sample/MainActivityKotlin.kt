package com.shuanghua.takephoto_sample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException


/**
 * 调用相机拍照获取图片
 * 调用相册选择图片获取图片
 * kotlin 版本的
 */
class MainActivityKotlin : AppCompatActivity() {
    companion object {
        private const val SCREEN_WIDTH: Int = 120
    }

    private lateinit var imageView: ImageView

    private lateinit var imgFile: File
    private lateinit var imgUri: Uri


    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { result: Boolean -> // 接口回调，result 为拍照成功和失败
        if (result) { // imageUri 指向的文件已经有图片了
//            imageUri.let { imageView.setImageURI(it) }
//            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
            val bitmap = optionsImg(imgUri) // 先压缩图片，再显示
            imageView.setImageBitmap(bitmap)
        }
    }

    private val pickPictureLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { imgUri: Uri? -> // 接口回调，uri 为选取图片成功和失败
        imgUri?.let { imageView.setImageURI(it) }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        systemUiController()

        imageView = findViewById(R.id.imageView)
        val cameraButton = findViewById<Button>(R.id.button1)
        val galleryButton = findViewById<Button>(R.id.button2)
        val saveImageButton = findViewById<Button>(R.id.button3)

        imgFile = createFile(externalCacheDir!!, "image_name.jpg")
        imgUri = toAndroidUri(imgFile, "fileProvider")

        // 相机
        cameraButton.setOnClickListener {
//            takePictureFromCamera(imgUri, takePictureLauncher)
            takePictureFromCameraSample(takePictureLauncher)
        }

        // 相册
        galleryButton.setOnClickListener {
            pickPictureFromGallery(pickPictureLauncher)
        }

        // 下载图片保存到共享目录： Pictures 下， 并通知相册识别
        saveImageButton.setOnClickListener {
            lifecycleScope.launch {
                val uri = saveImageToPictures(this@MainActivityKotlin)
                println("uri: $uri")
                // Android Q 之前需要手动通知相册更新
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    notifyGallery(this@MainActivityKotlin, uri)
                }
                val msg = String.format("图片已保存到 %s", "/Pictures/TakePhotoSampleApp")
                Toast.makeText(this@MainActivityKotlin, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun AppCompatActivity.systemUiController() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.imageView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @Throws(FileNotFoundException::class)
    private fun optionsImg(imgUri: Uri): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // BitmapFactory.decodeStream 会返回 null ，不会真正加载 bitmap 到内存中
        BitmapFactory.decodeStream(contentResolver.openInputStream(imgUri), null, options)
        val width_tmp = options.outWidth
        val height_tmp = options.outHeight
        println("采样前：" + width_tmp + " x " + height_tmp + ", size: " + options.inDensity)
        var scale = 2
        while (true) {
            if (width_tmp / scale < SCREEN_WIDTH) break
            scale += 2
        }
        options.inSampleSize = scale / 2
        options.inJustDecodeBounds = false
        val bm = BitmapFactory.decodeStream(
            contentResolver.openInputStream(imgUri), null, options
        )

        println("采样后：" + options.outWidth + " x " + options.outHeight + ", size: " + bm!!.byteCount)
        return bm
    }
}
