package com.shuanghua.retrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shuanghua.retrofit.bean.GithubRepo
import com.shuanghua.retrofit.bean.TouTiao
import com.shuanghua.retrofit.network.ApiFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getTouTiao()
        getGitHubRepo()
    }

    private fun getTouTiao() {
        val toutiaoCall =
            ApiFactory.getTouTiaoApi().getTouTiaoData("top", "a1a755458cc22f129942b34904feb820")
        toutiaoCall.enqueue(object : Callback<TouTiao> {
            override fun onFailure(call: Call<TouTiao>, t: Throwable) {
                println("onError:toutiaoCall-> ${t.message}")
            }

            override fun onResponse(call: Call<TouTiao>, response: Response<TouTiao>) {
                println("toutiao-> ${response.body()?.reason}")
            }
        })
    }

    private fun getGitHubRepo() {
        val githubCall = ApiFactory.getGitHubApi().getGitHubRepoData("shuanghua")
        githubCall.enqueue(object : Callback<List<GithubRepo>> {
            override fun onFailure(call: Call<List<GithubRepo>>, t: Throwable) {
                println("onError:githubCall-> ${t.message}")

            }

            override fun onResponse(
                call: Call<List<GithubRepo>>,
                response: Response<List<GithubRepo>>
            ) {
                println("github-> ${response.body()?.get(0)?.name}")
            }
        })
    }
}