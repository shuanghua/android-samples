package dev.shuanghua.aidl.server

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import dev.shuanghua.aidl.IAppInterface
import kotlin.concurrent.thread

class ServerLoginMainActivity : AppCompatActivity() {

    private var clientBinder: IAppInterface? = null
    private var isBindClient = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_server)

        bindClient()
        sendLoginStatusToClient()
    }

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (!isBindClient) {
                clientBinder = IAppInterface.Stub.asInterface(service)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBindClient = false
            clientBinder = null
        }
    }

    private fun bindClient() {
        Intent().apply {
            action = IAppInterface::class.java.name
            `package` = "dev.shuanghua.aidl.client" // 这里的包名是客户端 Service 类所在的报名,而不是 aidl
        }.also {
            val s = bindService(it, conn, BIND_AUTO_CREATE)
            if (s) println("绑定client成功") else println("绑定client失败")

        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private fun sendLoginStatusToClient() {
        thread {
            handler.postDelayed({
                clientBinder?.loginStatus(true)
                finish()
            }, 2000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBindClient) unbindService(conn)
    }
}