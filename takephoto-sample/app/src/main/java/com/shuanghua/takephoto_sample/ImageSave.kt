/**
 * 针对：
 * 1.应用专属外部存储 getExternalFilesDir()
 * 2.外部共享目录 Pictures 等目录进行图片保存
 *
 * 对于专属内部存储 getFilesDir()，内存有限，不应该用来存储大文件
 *
 * 笔记：Q+ 访问自己创建的文件(包括 Public, getExternalFilesDir() getFilesDir() 目录下)，无需任何权限
 * 但要访问 Public 目录下，其它应用的所存的文件，需要申请读取权限，而且还只能访问对应目录的文件类型
 * 比如 Picture 目录下，其它应用的非媒体文件是访问不了的
 */
package com.shuanghua.takephoto_sample

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 *
 *  getExternalFilesDir() 在Android Q上已经被设定为应用私有目录，
 *  App 可把主动把该目录的图片分享给别的应用，
 * 但相册或其它App都是访问不了这个目录里面的图片
 */
@Throws(IOException::class)
fun readImage2(context: Activity, uri: Uri?): Bitmap? {
    val resolver = context.contentResolver
    return resolver.openFileDescriptor(uri!!, "r").use { pfd ->
        if (pfd != null) BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor) else null
    }
}


/**
 * 保存到共享目录 Pictures 下 （已适配全版本（
 *
 *  Android Q + 使用 MediaStore
 * Android Q 之前，请申请权限后使用 File
 *
 * sdcard/Pictures/图片名.后缀
 *
 * sdcard/Pictures/应用名/图片名.后缀
 * @param context   activity
 * @param imageName 图片的名字
 * @param suffix    图片的格式：jpg 或者 png
 * @param directory 图片的目录（只需要层一目录，强烈建议写自己应用的名字）
 * @param mimeType  传 image/jpeg 或 mime/png
 * @param bitmap    bitmap 资源
 * @return 保存成功后的图片路径 Uri
 */
@Throws(IOException::class)
fun saveImageToSharedPictures(
    context: Activity,
    imageName: String,
    suffix: String?,
    directory: String = context.getString(R.string.app_name),
    mimeType: String?,
    bitmap: Bitmap
): Uri {
    // 如果要保存到其它公享目录，可以修改这里的 DIRECTORY_PICTURES
    val shareDir = Environment.DIRECTORY_PICTURES
    val imageDir = shareDir + File.separator + directory

    val resolver = context.applicationContext.contentResolver
    val values = ContentValues()

    values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, imageDir) // MediaStore 方式
    } else { // Q 之前的需要申请读和写的权限
        val dirFile = File(Environment.getExternalStoragePublicDirectory(shareDir), directory)
        if (!dirFile.exists()) {
            dirFile.mkdirs() // 必须先创建第一层目录
        }
        val imageFile = File.createTempFile(imageName, suffix, dirFile) //然后创建全目录
        values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
    }

    val uri = checkNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
    val outputStream = resolver.openOutputStream(uri) //获取写出流
    outputStream?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) } // 写入 bitmap
    return uri //这个Uri可直接用于分享(不用使用 fileProvider)
}

/**
 * 保存 专属外部存储目录下 (Android Q 之前需要申请权限)
 * /sdcard/Android/data/应用包名/files/Pictures/xxxx.jpg
 */
fun saveImageToExternalFilesDir(
    activity: Activity,
    bitmap: Bitmap,
    imageName: String = "xxxx.jpg"
): Uri {
    val imageFile = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageName)
    FileOutputStream(imageFile).use { fos ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    }
    val uri = Uri.fromFile(imageFile)
    return uri
}


/**
 * 根据图片名字获取图片
 * 同时建议申请读取权限（Q+ 也建议，因为 Q+ 在应用卸载重装的情况下会把上次的图片识别为非自己目录的文件）
 * 建议在 io 线程调用
 *
 * @param imageName 图片名字，[ 一定要包含后缀格式 ]
 * @return 图片的 Bitmap 形式
 */
@Throws(IOException::class)
fun getImage(activity: Activity, imageName: String): Bitmap? {
    var uri: Uri? = null

    val projection = arrayOf( // 需要的信息，例如图片的所在数据库表对应的 ID，图片在数据库中的名字
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME
    )
    val selection = MediaStore.Images.Media.DISPLAY_NAME + " == ?"
    val selectionArgs = arrayOf(imageName)

    val context = activity.applicationContext
    val resolver = context.contentResolver

    resolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    ).use { cursor ->
        checkNotNull(cursor)
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
            )
        }
    }
    if (uri != null) {
        val inputStream = resolver.openInputStream(uri!!)
        return BitmapFactory.decodeStream(inputStream)
    }
    return null
}


/**
 * 根据图片名字获取图片
 *
 * @param imageName 图片名字，包含后缀格式
 * @return 返回对应图片的 Uri 形式
 */
fun getImageUri(activity: Activity, imageName: String): Uri? {
    var uri: Uri? = null

    val projection = arrayOf( // 需要的信息，例如图片的所在数据库表对应的 ID，图片在数据库中的名字
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME
    )
    val selection = MediaStore.Images.Media.DISPLAY_NAME + " == ?"
    val selectionArgs = arrayOf(imageName)

    val context = activity.applicationContext
    val resolver = context.contentResolver
    val values = ContentValues()
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)

    resolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    ).use { cursor ->
        checkNotNull(cursor)
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
            )
        }
    }
    return uri
}


/**
 * 获取共享目录下所有图片的id和名字
 * 之后可以根据图片名字从 map 获取你需要图片的 id
 * 最后根据 id 转换成 uri 获取图片
 *
 * @return 返回共享目录下所有图片的 id 和 name 的 map 集合
 */
fun getAllImageInfoMap(activity: Activity): HashMap<Long, String> {
    val map = HashMap<Long, String>()

    val projection = arrayOf( // 需要的信息，例如图片的所在数据库表对应的 ID，图片在数据库中的名字
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME
    )

    val context = activity.applicationContext
    val resolver = context.contentResolver
    val values = ContentValues()
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, "")

    resolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    ).use { cursor ->
        checkNotNull(cursor)
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            map[id] = name
        }
    }
    return map
}


/**
 * 获取本应用共享目录下所有的图片对应的 uri 集合
 *
 * @return 本应用共享目录中所有图片 uri 集合
 */
fun getImageUris(activity: Activity): ArrayList<Uri> {
    val uris = ArrayList<Uri>()

    val projection = arrayOf( // 需要的信息，例如图片的所在数据库表对应的 ID，图片在数据库中的名字
        MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME
    )

    val context = activity.applicationContext
    val resolver = context.contentResolver
    val values = ContentValues()
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, "")

    resolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    ).use { cursor ->
        checkNotNull(cursor)
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        var uri: Uri
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
            )
            uris.add(uri)
        }
    }
    return uris
}


/**
 * 通知相册更新
 * @param path 完整路径（包含图片的格式）
 */
fun notifyGallery(context: Context, path: String?) {
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val f = File(path)
    val contentUri = Uri.fromFile(f)
    mediaScanIntent.setData(contentUri)
    context.sendBroadcast(mediaScanIntent)
}


/**
 * 通知相册更新
 */
fun notifyGallery(context: Context, file: File) {
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val f = File(file.absolutePath)
    val contentUri = Uri.fromFile(f)
    mediaScanIntent.setData(contentUri)
    context.sendBroadcast(mediaScanIntent)
}


/**
 * 通知相册更新
 */
fun notifyGallery(context: Context, uri: Uri?) {
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    mediaScanIntent.setData(uri)
    context.sendBroadcast(mediaScanIntent)
}


/**
 * 生成分享 Uri
 * 不要使用此 Uri 来通知相册
 */
fun createFileProviderUri(context: Activity, file: File?): Uri {
    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0
        FileProvider.getUriForFile(
            context,  // 注意： 此 Uri 不能用于通知相册更新，仅用于分享
            context.applicationContext.packageName + ".fileprovider",
            file!!
        ) //.fileprovider 必须和清单文件保持一致
    } else {
        Uri.fromFile(file) //此 Uri 可以用于通知相册更新
    }
    return uri
}
