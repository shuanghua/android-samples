package com.shuanghua.retrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.shuanghua.retrofit.bean.TestBean
import com.shuanghua.retrofit.github.GithubRepo
import com.shuanghua.retrofit.network.NetWorkResult
import com.shuanghua.retrofit.network.ResultJava
import com.shuanghua.retrofit.toutiao.TouTiao

class MainActivity : AppCompatActivity(), NetWorkResult<List<GithubRepo>> {
    private val useRetrofit = DataRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //getTouTiao()
        //getTouTiaoLiveData()
        //getGitHubRepo()

        testJosn()
    }

    private fun testJosn() {
        val gson = Gson()
        val s =
            "{\"hostSearch\":[{\"advertImg\":\"\",\"advertType\":\"html5\",\"advertParam\":\"546456546\",\"advertTargetId\":\"\",\"advertSecondTargetId\":\"\",\"advertExtend\":{},\"advertStyle\":\"\",\"advertTitle\":\"54645645645645645\",\"advertTable\":\"search_word\",\"advertShowNavTitle\":\"\",\"id\":74,\"keyword\":\"54645645645645645\",\"image\":\"\",\"searchWordSpecialStyle\":{\"fontColor\":\"#D92B3A\",\"fontColorClarity\":100.0,\"backgroundColor\":\"#1AD92B3A\",\"backgroundColorClarity\":0.1}}]}"
        val fromJson = gson.fromJson<TestBean>(s, TestBean::class.java)
        println(fromJson)
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