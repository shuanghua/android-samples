package dev.shuanghua.aidl.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dev.shuanghua.aidl.IAppInterface
import java.lang.Thread.sleep


/**
 * 服务端 Service
 */
class ServerService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return if (IAppInterface::class.java.name == intent.action) {
            serverBinder
        } else {
            null
        }
    }

    private val serverBinder = object : IAppInterface.Stub() {

        override fun login(aString: String?) {
            println("server:收到登录请求:$aString")
            sleep(5000) // 5 秒后拉取登录页面处理客户端的请求
            val intent = Intent(this@ServerService, ServerLoginMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        override fun loginStatus(status: Boolean) {
        }
    }
}