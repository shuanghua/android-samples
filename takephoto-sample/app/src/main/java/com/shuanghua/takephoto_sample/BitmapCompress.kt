package com.shuanghua.takephoto_sample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import java.io.FileNotFoundException

private const val SCREEN_WIDTH_120 = 120

/**
 * 采样压缩 （不需要加载原图到内存）
 * 不能指定最后输出的分辨率
 * inSampleSize = 1，采样后的图片大小为原始大小
 * inSampleSize < 1，也按照 1 来计算
 * inSampleSize > 1，即采样后的图片将缩小，缩小比例为 1 / ( inSampleSize 的二次方 )
 */
@Throws(FileNotFoundException::class)
fun Context.optionsImg(imgUri: Uri): Bitmap {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true // BitmapFactory.decodeStream 会返回 null ，不会真正加载 bitmap 到内存中
    BitmapFactory.decodeStream(contentResolver.openInputStream(imgUri), null, options)
    val widthTmp = options.outWidth
    val heightTmp = options.outHeight
//    println("采样前：" + widthTmp + " x " + heightTmp + ", size: " + options.inDensity)
    var scale = 2
    while (true) {
        if (widthTmp / scale < SCREEN_WIDTH_120) break
        scale += 2
    }
    options.inSampleSize = scale / 2
    options.inJustDecodeBounds = false
    val bm = BitmapFactory.decodeStream(
        contentResolver.openInputStream(imgUri), null, options
    )

//    println("采样后：" + options.outWidth + " x " + options.outHeight + ", size: " + bm!!.byteCount)
    return bm!!
}


/**
 * 矩阵压缩（需要先加载原图到内存）
 */
fun Context.matrixImg(imgUri: Uri): Bitmap {
    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imgUri))
//    println("Matrix前：" + bitmap.width + " x " + bitmap.height + ", size: " + bitmap.byteCount)

    val matrix = Matrix()
    matrix.setScale(1200f / bitmap.width, 1200f / bitmap.height) //数越小，压缩越狠
    val bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

//    println("Matrix后：" + bm.width + " x " + bm.height + ", size: " + bm.byteCount)
    return bm
}