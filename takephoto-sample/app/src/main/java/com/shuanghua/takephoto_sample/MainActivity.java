package com.shuanghua.takephoto_sample;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions  //第一步
public class MainActivity extends AppCompatActivity {
    private static final int SCREEN_WIDTH = 120;
    public static final int REQUEST_TAKE_PHOTO_CAMERA = 1;
    public static final int REQUEST_TAKE_PHOTO_GALLERY = 2;
    private ImageView mImageView;
    private Button mButton;
    File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.imageView);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(view -> {
            MainActivityPermissionsDispatcher.takePhotoWithPermissionCheck(this); // 第四步
            //pickPhotoFromGallery();
        });
    }

    /**
     * 从相册获取
     */
    private void pickPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO_GALLERY);
    }

    /**
     * Path -> File -> Uri
     */
    @NeedsPermission(Manifest.permission.CAMERA) //第二步
    public void takePhoto() {
        Uri imgUri;
        imgFile = new File(getExternalCacheDir(), "output_image.jpg");// getExternalCacheDir 返回当前应用包名下的 cache 目录路径
        if (imgFile.exists()) {
            imgFile.delete();
        } else {
            try {
                imgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT < 24) {
            imgUri = Uri.fromFile(imgFile);
        } else { // FileProvider
            imgUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", imgFile);//.fileProvider 也可以改成别的,只是一个标识而已
        }
        // uri=content://com.shuanghua.takephoto_sample.fileProvider/external_files/Android/data/com.shuanghua.takephoto_sample/cache/output_image.jpg
        // file=/storage/emulated/0/Android/data/com.shuanghua.takephoto_sample/cache/output_image.jpg
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri); // 如果需要拍照的原图 (不需要则将该行注释掉,然后在 ActivityResult 通过 data 获取)
        startActivityForResult(intent, REQUEST_TAKE_PHOTO_CAMERA);// 打开系统相机，同时设置一个 ActivityResult 标记（说明是是从相机页面返回的结果）
    }

    /**
     * 采样压缩 （不需要加载原图到内存）
     * 不能指定最后输出的分辨率
     * inSampleSize = 1，采样后的图片大小为原始大小
     * inSampleSize < 1，也按照 1 来计算
     * inSampleSize > 1，即采样后的图片将缩小，缩小比例为 1 / ( inSampleSize 的二次方 )
     */
    private Bitmap optionsImg(Uri imgUri) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // BitmapFactory.decodeStream 会返回 null ，不会真正加载 bitmap 到内存中
        BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri), null, options);
        int width_tmp = options.outWidth;
        int height_tmp = options.outHeight;
        System.out.println("采样前：" + width_tmp + " x " + height_tmp + ", size: " + options.inDensity);
        int scale = 2;
        while (true) {
            if (width_tmp / scale < SCREEN_WIDTH) break;
            scale += 2;
        }
        options.inSampleSize = scale / 2;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeStream(
                getContentResolver().openInputStream(imgUri), null, options);

        System.out.println("采样后：" + options.outWidth + " x " + options.outHeight + ", size: " + bm.getByteCount());
        return bm;
    }

    /**
     * 矩阵压缩（需要先加载原图到内存）
     */
    private Bitmap matrixImg(Uri imgUri) throws FileNotFoundException {

        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
        System.out.println("Matrix前：" + bitmap.getWidth() + " x " + bitmap.getHeight() + ", size: " + bitmap.getByteCount());

        Matrix matrix = new Matrix();
        matrix.setScale(1200f / bitmap.getWidth(), 1200f / bitmap.getHeight());//数越小，压缩越狠
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        System.out.println("Matrix后：" + bm.getWidth() + " x " + bm.getHeight() + ", size: " + bm.getByteCount());
        return bm;
    }


    /**
     * 保存bitmap到SD卡
     * Path -> File -> FileOutputStream
     */
    public String saveBitmapToSDCard(Bitmap bitmap) {
        File imgFile = new File(getExternalCacheDir(), "output.jpg");// getExternalCacheDir 返回当前应用包名下的 cache 目录路径
        FileOutputStream fos;
        try {
            if (imgFile.exists()) imgFile.delete();
            fos = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);//保存
            fos.close();
//            Uri uri = Uri.fromFile(imgFile);
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)); // 通知相册更新
            return imgFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO_CAMERA:
                    try {
                        Uri imgUri = Uri.fromFile(imgFile); // File to Uri
                        Bitmap bitmap = matrixImg(imgUri);
                        //Bitmap bitmap = optionsImg(imgUri);
                        saveBitmapToSDCard(bitmap);
                        //Bitmap photo = data.getParcelableExtra("data");// 获取系统压缩过后的图片（压缩后的图片特别小, 大概 2cm X 3cm）
                        mImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_TAKE_PHOTO_GALLERY:
                    try {
                        Uri imgUri = data.getData();// 相册页面返回对应图片的 uri
                        Bitmap bitmap = matrixImg(imgUri);
                        mImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults); //第三步
    }
}