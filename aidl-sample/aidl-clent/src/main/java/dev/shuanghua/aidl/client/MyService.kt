package dev.shuanghua.aidl.client

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dev.shuanghua.aidl.IAppInterface

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return if (IAppInterface::class.java.name.equals(intent.action)) {
            binder
        } else {
            null
        }
    }

    private val binder = object : IAppInterface.Stub() {
        override fun login(aString: String?) {
            println("---------->>$aString")
        }

        override fun loginStatus(status: Boolean) {
        }

    }
}