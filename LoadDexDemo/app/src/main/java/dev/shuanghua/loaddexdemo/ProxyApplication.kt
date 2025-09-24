package dev.shuanghua.loaddexdemo

import android.app.AppComponentFactory
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.ArrayMap
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference

class ProxyApplication : Application() {

    private var currentActivityThread: Any? = null
    private var loadedApk: Any? = null

    override fun onCreate() {
        super.onCreate()
        replaceApplication()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        //读取assets目录中的dex，写到指定目录中
        val dexName = "classes3.dex"
        val dexFilePath = filesDir.absolutePath + File.separator + dexName
        try {
            val ins = base.assets.open(dexName)
            val outs: OutputStream = FileOutputStream(File(dexFilePath))
            val bytes = ByteArray(1024)
            var index: Int
            while ((ins.read(bytes).also { index = it }) != -1) outs.write(bytes, 0, index)
            ins.close()
            outs.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //动态加载dex
        val dexClassLoader = DexClassLoader(
            dexFilePath,
            null,
            applicationInfo.nativeLibraryDir,
            classLoader
        )

        //替换mClassLoader
        // 获取 ActivityThread 实例
        currentActivityThread = Ref.invokeStaticMethod(
            "android.app.ActivityThread",
            "currentActivityThread",
            null,
            null
        )
        // 通过 ActivityThread 获取 mPackages实例
        val mPackages = Ref.getFieldObject(
            "android.app.ActivityThread",
            currentActivityThread,
            "mPackages"
        ) as ArrayMap<*, *>

        // 获取 loadedApk 实例
        loadedApk = (mPackages[packageName] as WeakReference<*>?)!!.get()

        // 将用来加载我们自己 dex 文件的 DexClassLoader 替换到 loadedApk 中
        Ref.setFieldObject(
            "android.app.LoadedApk",
            "mClassLoader",
            loadedApk,
            dexClassLoader
        )
        // 上面代码执行完， application 就会被返回到 activityThread
    }

    private fun replaceApplication() {
        //清除mApplication
        Ref.setFieldObject(
            "android.app.LoadedApk",
            "mApplication",
            loadedApk,
            null
        )

        //替换className
        val className = "dev.shuanghua.loaddexdemo.RealApplication"
        val applicationInfo: ApplicationInfo? = Ref.getFieldObject(
            "android.app.LoadedApk",
            loadedApk,
            "mApplicationInfo"
        ) as ApplicationInfo?
        applicationInfo!!.className = className

        //移除原先加入 list 里面的 Application
        val oldApplication = Ref.getFieldObject(
            "android.app.ActivityThread",
            currentActivityThread,
            "mInitialApplication"
        ) as Application

        val mAllApplications = Ref.getFieldObject(
            "android.app.ActivityThread",
            currentActivityThread,
            "mAllApplications"
        ) as ArrayList<*>
        mAllApplications.remove(oldApplication)

        //构建原app的application
        val realApplication: Application? = Ref.invokeMethod(
            "android.app.LoadedApk",
            "makeApplication",
            loadedApk,
            arrayOf<Class<*>?>(Boolean::class.javaPrimitiveType, Instrumentation::class.java),
            arrayOf<Any?>(false, null)
        ) as Application?

        Ref.setFieldObject(
            "android.app.ActivityThread",
            "mInitialApplication",
            currentActivityThread,
            realApplication
        )

        realApplication?.onCreate()
    }
}