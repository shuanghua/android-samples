package com.shuanghua.camera_preview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ImageView

/**
 * 将预览画面的的一帧转换成 Bitmap
 */
class PreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr),
    SurfaceHolder.Callback,
    Camera.PreviewCallback {

    private lateinit var camera: Camera

    private val ratio_4_3: ArrayList<Camera.Size> = ArrayList()
    private val ratio_16_9: ArrayList<Camera.Size> = ArrayList()
    private val ratio_1_1: ArrayList<Camera.Size> = ArrayList()

    private var bufferSize = 0
    private var imageView: ImageView? = null
    private var bitmap: Bitmap? = null
    private var processHandler: Handler? = null
    private var nv21ByteArray: ByteArray? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private val processThread: HandlerThread = HandlerThread("cameraProcessThread")
    private val nv21ToBitmap: NV21ToBitmap = NV21ToBitmap(context)

    private var w = 1080
    private var h = 1080

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val optimalPreviewSize = getPreviewSize()
        w = optimalPreviewSize!!.width
        h = optimalPreviewSize.height
        bufferSize = w * h * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8
        setMeasuredDimension(h, w)
    }

    init {
        //初始化 camera
        try {
            camera = Camera.open()
        } catch (e: Exception) {
            Log.e("PreviewView", "相机打开异常$e")
        }
        //初始化holder
        holder.addCallback(this)
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        //获取可用的分辨率
        val s = camera.parameters?.supportedPreviewSizes!!
        handleSize(s)
        for (size in s) Log.e("PreviewView", "${size.width} - ${size.height}")

        camera.setPreviewCallbackWithBuffer(this)
        processThread.start()
        processHandler = object : Handler(processThread.looper) {
            override fun handleMessage(msg: Message) {            // WorkThread
                super.handleMessage(msg)
                nv21ByteArray?.let {

//                    val yuvImage = YuvImage(nv21ByteArray, ImageFormat.NV21, w, h, null)
//                    val os = ByteArrayOutputStream()
//                    yuvImage.compressToJpeg(Rect(0, 0, w, h), 100, os)
//                    val bitmap2 = BitmapFactory.decodeByteArray(os.toByteArray(), 0, os.size())

                    val matrix = Matrix()
                    matrix.postRotate(90f)
                    bitmap = Bitmap.createBitmap(
                        nv21ToBitmap.nv21ToBitmap(nv21ByteArray, w, h),
                        0, 0,
                        w, h,
                        matrix, true
                    )
                    Log.d("PreviewView", "bitmapSize=${bitmap?.byteCount}")
                    mainHandler.post { imageView?.setImageBitmap(bitmap) }
                }
            }
        }
    }

    private fun initCameraConfig() {
        val parameters = camera.parameters // 一定要创建一个新的引用,否则分辨率不会生效
        parameters.setPreviewSize(w, h)
        camera.parameters = parameters

        camera.setDisplayOrientation(90)// 竖屏，此时宽小于高 ,这里不会影响 onPreviewFrame 的宽高
        camera.setPreviewDisplay(holder)
        requestLayout()
        camera.startPreview()
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        Log.d("PreviewView->", "data= ${data?.size}")
        processHandler?.sendEmptyMessage(1)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        nv21ByteArray = ByteArray(bufferSize)
        camera.addCallbackBuffer(nv21ByteArray)

        initCameraConfig()
        Log.d("PreviewView-> ", "$w - $h - $bufferSize")
    }

    /**
     * 这里的宽高是布局文件的宽高
     */
    override fun surfaceChanged(holder1: SurfaceHolder, format: Int, width: Int, height: Int) {
        try {
            camera.stopPreview()
        } catch (e: Exception) {
        }
        initCameraConfig()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera.stopPreview()
        camera.release()
    }

    fun startPreview() {
        camera.startPreview()
    }

    fun stopPreview() {
        camera.stopPreview()
        camera.release()
    }

    fun nextFrame() {
        camera.addCallbackBuffer(nv21ByteArray)
    }

    fun setImageView(imageView: ImageView) {
        this.imageView = imageView
    }

    private fun getPreviewSize(): Camera.Size? {
        for (s in ratio_4_3) {
            if (s.height == getScreenWidth()) {
                return s
            }
        }
        return null
    }

    /**
     * 最接近控件宽高比率的大小
     */
    private fun getOptimalPreviewSize(ratioType: String): Camera.Size? {
        if (ratioType == "4:3" && ratio_4_3.size != 0) {
            return ratio_4_3[0]
        } else if (ratioType == "16:9" && ratio_16_9.size != 0) {
            return ratio_16_9[0]
        } else if (ratioType == "1:1" && ratio_1_1.size != 0) {
            return ratio_1_1[0]
        }
        return null
    }

    private fun handleSize(sSizes: List<Camera.Size>) {
        for (size in sSizes) {
            when {//默认0度方向，此时宽大于高
                size.height * 4 / 3 == size.width -> ratio_4_3.add(size)
                size.height * 16 / 9 == size.width -> ratio_16_9.add(size)
                size.height == size.width -> ratio_1_1.add(size)
            }
        }
    }


    fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

    fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels
}