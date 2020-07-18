package com.shuanghua.retrofit.network;

public interface NetWorkData<T> {
    void success(T result);

    void error(Exception e, T data);
}
