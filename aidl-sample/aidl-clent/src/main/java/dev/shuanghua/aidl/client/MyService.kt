package dev.shuanghua.aidl.client

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dev.shuanghua.aidl.IAppInterface

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return if (IAppInterface::class.java.name == intent.action) {
            binder
        } else {
            null
        }
    }

    // 存根对象 是运行在用户空间的进程对象，它封装了要暴露给其他进程的 API 方法。
    // Stub 存根对象继承系统 binder , 并实现 aidl 接口
    // Stub 存根对象是一个抽象类,所以需要用户进一步做具体实现
    private val binder = object : IAppInterface.Stub() {
        // 暴露登录方法 (这些方法需要在 AIDL 中定义)
        override fun login(aString: String?) {
            // 模拟微信登录
            // 省略 请求网络登录代码
            println("---------->>$aString")
        }

        // 暴露登录状态方法
        override fun loginStatus(status: Boolean) {
        }

    }
}