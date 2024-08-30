package dev.shuanghua.aidl.client

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.os.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.shuanghua.aidl.IAppInterface
import java.lang.ref.WeakReference

/**
 * 客户端
 */
class ClientMainActivity : AppCompatActivity(){
    private var serverBinder: IAppInterface? = null
    private var isBindServer = false

    private lateinit var button: Button

    private lateinit var uiHandler: UiHandler

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.login)
        login()
    }

    override fun onStart() {
        super.onStart()
        bindWeChatLoginService()
    }

    class CallBackService(
        handler: UiHandler,
        private val weakHandler: WeakReference<UiHandler> = WeakReference<UiHandler>(handler),
    ) : Service() {

        private val binder = CallBackBinder()
        override fun onBind(intent: Intent?): IBinder {
            return binder
        }

        inner class CallBackBinder() : Binder() {
            fun getService(): CallBackService = this@CallBackService
        }

        /**
         * 当收到 Service 的消息时,使用 handler 从 binder线程 切换 到 ui线程
         */
        fun loginStatus(status: Boolean) {
            println("CallBackService:微信登录返回的结果:$status - ${Thread.currentThread().name}")
            weakHandler.get()?.sendMessage(
                weakHandler.get()!!.obtainMessage(666, status)
            )
        }
    }

    /**
     * 普通内部类 会默认持有外部类的"强引用", 静态内部类又不持有外部类的任何引用;
     * 因此为了能安全调用外部类方法, 设置为静态内部类后, 我们自己给它传一个"弱引用"
     */
    class UiHandler(
        context: Context,
        private val weakContext: WeakReference<Context> = WeakReference(context),
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                666 -> Toast.makeText(weakContext.get(), "张三登录成功!", Toast.LENGTH_SHORT).show()
                else -> super.handleMessage(msg)
            }
        }
    }

    /**
     * 跨应用绑定到 登录 服务
     * 收消息: 被绑定方
     * 发消息: 发起绑定方 (可以不需要 Service )
     */
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // 这里返回的实际是: new IAppInterface.Stub.Proxy(obj)
            // 所以 asInterface 获取到的是 Binder 代理对象
            serverBinder = IAppInterface.Stub.asInterface(service)
            service?.let { isBindServer = true }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBindServer = false
            serverBinder = null
        }
    }

    // 同一个应用内的 Server (可以不同进程)
    // val intent = Intent(this@ClientMainActivity, MyService::class.java)
    // 不同应用的跨进程, 客户端需要在清单文件配置 <queries>  <queries>
    private fun bindWeChatLoginService() {
        val intent = Intent()
        intent.action = IAppInterface::class.java.name  // (必须)
        intent.`package` = "dev.shuanghua.aidl.server"// (必须) 服务端 Service 类 包名
        val s = bindService(intent, conn, BIND_AUTO_CREATE) // 绑定时自动启动对应的服务, 调用后,上面的连接就会触发
        if (s) println("绑定成功") else println("绑定失败")
    }

    private fun login() {
        button.setOnClickListener {
            println("$isBindServer - $serverBinder--${IAppInterface::class.java.name}")
            if (isBindServer) {
                println("client:准备登录")
                serverBinder?.login("张三使用微信登录")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBindServer) unbindService(conn)
    }
}