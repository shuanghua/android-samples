package com.shuanghua.retrofit.github

import com.shuanghua.retrofit.github.GithubRepo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    // https://api.github.com/users/shuanghua/repos
    @GET("users/{user}/repos")
    fun getGitHubRepoData(@Path("user") user: String): Call<List<GithubRepo>>
}