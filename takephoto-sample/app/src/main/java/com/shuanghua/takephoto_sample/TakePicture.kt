package com.shuanghua.takephoto_sample

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * 外部传入 uri ，减少重复创建 uri 的开销
 * 如果你有 uri , 则可以直接使用
 */
fun AppCompatActivity.takePictureFromCamera(
    imgUri: Uri,
    takePictureLauncher: ActivityResultLauncher<Uri>
) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri) // 获取拍照的原图 (如不需要原图则将该行注释掉)
    takePictureLauncher.launch(imgUri) // 启动相机拍照，图片会保存到 uri 对应的目录，之后请通过该 uri 显示图片
}

/**
 * 传入 file
 * 如果你只有 file ，则转成 uri
 */
fun AppCompatActivity.takePictureFromCamera(
    file: File,
    takePictureLauncher: ActivityResultLauncher<Uri>
) {
    val imgUri = toAndroidUri(file)
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri) // 获取拍照的原图 (如不需要原图则将该行注释掉)
    takePictureLauncher.launch(imgUri) // 启动相机拍照，图片会保存到 uri 对应的目录，之后请通过该 uri 显示图片
}


/**
 * 传入 目录，文件名，和 fileProvider
 * 如果你只有 目录，文件名 ， 则转成file ，在转成 uri
 */
fun AppCompatActivity.takePictureFromCamera(
    dirFile: File,
    fileName: String,
    fileProvider: String = "fileProvider",
    takePictureLauncher: ActivityResultLauncher<Uri>
) {
    val file = createFile(dirFile, fileName) // 每次都会判断是否已存在，并删除重新创建
    val imgUri = toAndroidUri(file, fileProvider)
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri) // 获取拍照的原图 (如不需要原图则将该行注释掉)
    takePictureLauncher.launch(imgUri) // 启动相机拍照，图片会保存到 uri 对应的目录，之后请通过该 uri 显示图片
}


/**
 *
 * 如果你只想测试拍照
 *
 * 该方法只是用来演示 拍照并保存到指定 uri 的指定目录 的流程
 * 我们需要要给目录和文件名，以及和清单文件中对应的文件提供者的标识
 *
 * 在这个方法中可以：
 * 自行修改 ".fileProvider" 和清单文件中的 authorities 一致
 * 自行修改保存图片的名字 和 目录
 *
 * 通过 registerForActivityResult 打开系统相机 (替代 startActivityForResult 过时的处理方式)
 *
 * 正常使用请：
 * 改成外部传入 uri ，方便： 当含有图片时，方便共用一个 uri, 减少重复创建 uri 的开销
 *
 * 笔记：
 * Uri 的生成方式通常有3种：
 * 1. 通过 FileProvider.getUriForFile(context, authority, file) 获取 (Android 7.0 及以上)
 * 2. 通过 Uri.fromFile(file) 获取 (Android 7.0 以下)
 * 3. 通过 content:// 字符串拼接获取
 *
 * 前 2 种都需要一个 file 对象
 * 所以要注意， 在 Android 10 及以上，只能使用 App 私有目录下的目录路径来创建 File 对象，
 *
 * 对于外部公共存储目录，需要使用系统提供的 MediaStore.Images.Media.EXTERNAL_CONTENT_URI 等 uri 来获取图片
 */
fun AppCompatActivity.takePictureFromCameraSample(takePictureLauncher: ActivityResultLauncher<Uri>) {
    val imgFile = File(externalCacheDir, "image_name.jpg")
    if (imgFile.exists()) {
        imgFile.delete()
    } else {
        try {
            imgFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    val imgUri = if (Build.VERSION.SDK_INT < 24) {
        Uri.fromFile(imgFile) // file to uri
    } else { // FileProvider
        FileProvider.getUriForFile(
            this,
            "$packageName.fileProvider",
            imgFile
        ) //.fileProvider 也可以改成别的,只是一个标识而已
    }
//    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri) // 这会获取拍照的原图 (如不需要原图则将该行注释掉)
//    startActivityForResult(intent, 100) // 启动相机拍照，(已经过时)
    takePictureLauncher.launch(imgUri) // 启动相机拍照，并将图片保存到 uri
}


/**
 * 从相册选择图片并监听选择的结果
 */
fun pickPictureFromGallery(pickPictureLauncher: ActivityResultLauncher<PickVisualMediaRequest>) {
    //https://developer.android.com/training/data-storage/shared/photopicker?hl=zh-cn
    val mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
//    val mediaType = ActivityResultContracts.PickVisualMedia.VideoOnly
//    val mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
//    val mediaType = ActivityResultContracts.PickVisualMedia.SingleMimeType("image/jpeg") // image/jpeg, image/png, image/gif, image/webp ,image/*
    pickPictureLauncher.launch(PickVisualMediaRequest(mediaType)) // 启动相册选择，图片请在 pickPictureLauncher 的回调中获取
}


suspend fun saveImageToPictures(activity: AppCompatActivity): Uri = withContext(Dispatchers.IO) {
    val bitmap = Glide.with(activity)
        .asBitmap()
        .load("https://avatars.githubusercontent.com/shuanghua")
        .submit()
        .get()
    // 返回图片的 uri 到主线程
//    saveImageToPictures(
//        context = activity,
//        directory = "TakePhotoSampleApp",
//        imageName = "github_avatar",
//        suffix = ".jpg",
//        mimeType = "image/jpeg",
//        bitmap = bitmap
//    )
    saveImageToExternalFilesDir(activity, bitmap)
}


fun Context.toAndroidUri(file: File, fileProvider: String = "fileProvider"): Uri {
    return if (Build.VERSION.SDK_INT < 24) {
        Uri.fromFile(file) // file to uri
    } else {
        FileProvider.getUriForFile(this, "$packageName.$fileProvider", file)
    }
}


fun File.toAndroidUri(context: Context, fileProvider: String = "fileProvider"): Uri {
    return if (Build.VERSION.SDK_INT < 24) {
        Uri.fromFile(this) // file to uri
    } else {
        FileProvider.getUriForFile(context, "${context.packageName}.$fileProvider", this)
    }
}


/**
 * 创建文件，已经存在的文件会被删除，并返回文件对象
 */
fun Context.createFile(
    dirFile: File = externalCacheDir!!,
    fileName: String = "image_name.jpg"
): File {
    val file = File(dirFile, fileName)
    if (dirFile.exists()) {
        file.delete()
    } else {
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return file
}