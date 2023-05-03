package dev.shuanghua.aidl.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dev.shuanghua.aidl.IAppInterface
import kotlin.concurrent.thread

/**
 * 服务端 Service
 */
class ServerService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder? {
        println("------>>${intent.action}")
        return if (IAppInterface::class.java.name.equals(intent.action)) {
            binder
        } else {
            null
        }
    }

    private val binder = object : IAppInterface.Stub() {
        override fun login(aString: String?) {
            println("server:收到登录请求:$aString")
            // 收到客户端消息,接下来可以做别的事情,例如拉起自己某个页面的 Activity 去处理


            // 一般登录成功,需要把状态返回给客户端,那么就需要进行双向发消息
            // 发消息给客户端,告诉它登录的状态
            // 这一部分的代码应该放到 MainActivity 中处理
            thread {
                val intent = Intent(this@ServerService, ServerLoginMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

        }

        override fun loginStatus(status: Boolean) {

        }
    }
}