package dev.shuanghua.aidl.client

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import dev.shuanghua.aidl.IAppInterface

class ClientService : Service() {
    private var isBind = false
    private var callBackService: ClientMainActivity.CallBackService? = null

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            callBackService = (service as ClientMainActivity.CallBackService.CallBackBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBind = false
            callBackService = null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return if (IAppInterface::class.java.name.equals(intent.action)) {
            binder
        } else {
            null
        }
    }


    private val binder = object : IAppInterface.Stub() {
        override fun login(aString: String?) {
        }

        override fun loginStatus(status: Boolean) {
            // binder 线程
            println("ClientService:微信登录返回的结果:$status - ${Thread.currentThread().name}")
            // 正常场景是,登录成功回来后直接进入App主页面
            // 登录失败则提醒(将提醒内容保存本地观察,或广播通知界面),不用跳转页面

//            Intent(this@ClientService, ClientMainActivity.CallBackService::class.java).also {
//                bindService(it, conn, BIND_AUTO_CREATE)
//            }
//            callBackService?.loginStatus(status)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBind) {
            unbindService(conn)
        }
    }
}