package com.shuanghua.retrofit.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 单例
 */
public class ApiFactory {
    private static final Object monitor = new Object();

    private static TouTiaoService mTouTiaoApi = null;
    private static GitHubService mGitHubApi = null;

    public static TouTiaoService getTouTiaoApi() {
        synchronized (monitor) {
            if (mTouTiaoApi == null) {
                mTouTiaoApi = new Retrofit.Builder()
                        .baseUrl("http://v.juhe.cn/toutiao/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(TouTiaoService.class);
            }
        }
        return mTouTiaoApi;
    }

    public static GitHubService getGitHubApi() {
        synchronized (monitor) {
            if (mGitHubApi == null) {
                mGitHubApi = new Retrofit.Builder()
                        .baseUrl("https://api.github.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(GitHubService.class);
            }
        }
        return mGitHubApi;
    }
}
