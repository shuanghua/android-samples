package com.shuanghua.retrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.shuanghua.retrofit.bean.GithubRepo
import com.shuanghua.retrofit.bean.TouTiao
import com.shuanghua.retrofit.network.NetWorkData
import com.shuanghua.retrofit.network.ResultJava

class MainActivity : AppCompatActivity(),
    NetWorkData<List<GithubRepo>> {
    private val useRetrofit = DataRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getTouTiao()
        getTouTiaoLiveData()
        getGitHubRepo()
    }

    private fun getTouTiao() {
        Thread(Runnable {
            val result = useRetrofit.touTiaoData
            when (result) {
                is ResultJava.Success -> showTouTiao(result.data)
                is ResultJava.Error -> println("出现错误！！")
            }
        }).start()
    }

    /**
     * LiveData
     */
    private fun getTouTiaoLiveData() {
        useRetrofit.touTiaoLiveData.observe(this, Observer { result ->
            if (result != null) {
                when (result) {
                    is ResultJava.Success -> showTouTiao(result.data)
                    is ResultJava.Error -> println("出现错误！！")
                }
            }
        })
    }

    //接口回调获取数据
    private fun getGitHubRepo() {
        useRetrofit.getGitHubData("shuanghua", this)
    }

    //----------------------------------------------------------------------------------------------

    override fun success(result: List<GithubRepo>?) {
        if (result != null) showGitHub(result)
    }

    override fun error(e: Exception?, data: List<GithubRepo>?) {
        //toast("数据获取出现错误！！")
        println("orror->${e?.message}")
    }

    private fun showTouTiao(toutiao: TouTiao) {
        // setAdapter(toutiao)
        println("toutiao->${toutiao.result.data[1].title}")
    }

    private fun showGitHub(githubs: List<GithubRepo>) {
        // setAdapter(githubs)
        println("github->${githubs[1].name}")
    }
}