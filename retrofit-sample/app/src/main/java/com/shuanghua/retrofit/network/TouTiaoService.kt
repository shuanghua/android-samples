package com.shuanghua.retrofit.network

import com.shuanghua.retrofit.bean.TouTiao
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TouTiaoService {
    // http://v.juhe.cn/toutiao/index?type=top&key=a1a755458cc22f129942b34904feb820

    @GET("index")
    fun getTouTiaoData(@Query("type") type: String, @Query("key") key: String): Call<TouTiao>
}