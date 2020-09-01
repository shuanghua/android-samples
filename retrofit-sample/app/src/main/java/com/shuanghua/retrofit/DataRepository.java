package com.shuanghua.retrofit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.shuanghua.retrofit.github.GithubRepo;
import com.shuanghua.retrofit.toutiao.TouTiao;
import com.shuanghua.retrofit.network.ApiFactory;
import com.shuanghua.retrofit.network.NetWorkResult;
import com.shuanghua.retrofit.network.ResultJava;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 数据仓库
 */
class DataRepository {
    
    /**
     * 直接获取数据方式 （ 注意这个函数必须在子线程中调用 ）
     */
    public ResultJava<TouTiao> getTouTiaoData() {
        Call<TouTiao> call = ApiFactory.getTouTiaoApi().getTouTiaoData("top", "a1a755458cc22f129942b34904feb820");
        try {
            Response<TouTiao> response = call.execute();
            if (response.isSuccessful()) {
                return new ResultJava.Success<>(response.body());
            } else {
                return new ResultJava.Error<>(new Exception(response.message()), null);
            }
        } catch (IOException e) {
            return new ResultJava.Error<>(e, null);
        }
    }


    /**
     * 接口回调获取数据的封装
     */
    public void getGitHubData(String user, NetWorkResult<List<GithubRepo>> netWorkResult) {
        Call<List<GithubRepo>> call = ApiFactory.getGitHubApi().getGitHubRepoData(user);
        call.enqueue(new Callback<List<GithubRepo>>() {
            @Override
            public void onResponse(@NotNull Call<List<GithubRepo>> call, @NotNull Response<List<GithubRepo>> response) {
                if (response.isSuccessful()) {
                    if (netWorkResult != null) {
                        netWorkResult.success(response.body());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<GithubRepo>> call, @NotNull Throwable t) {
                // 网络请求失败，可用别的数据替代 null
                netWorkResult.error(new Exception(t.getMessage()), null);
            }
        });
    }

    /**
     * 其他方式显示数据的封装，比如用 LiveData 显示数据
     */
    public LiveData<ResultJava<TouTiao>> getTouTiaoLiveData() {
        MutableLiveData<ResultJava<TouTiao>> liveData = new MutableLiveData<>();
        Call<TouTiao> call = ApiFactory.getTouTiaoApi().getTouTiaoData("top", "a1a755458cc22f129942b34904feb820");
        call.enqueue(new Callback<TouTiao>() {
            @Override
            public void onResponse(@NotNull Call<TouTiao> call, @NotNull Response<TouTiao> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(new ResultJava.Success<>(response.body()));
                }
            }

            @Override
            public void onFailure(@NotNull Call<TouTiao> call, @NotNull Throwable t) {
                liveData.setValue(new ResultJava.Error<>(new Exception(t.getMessage()), null));
            }
        });
        return liveData;
    }
}
