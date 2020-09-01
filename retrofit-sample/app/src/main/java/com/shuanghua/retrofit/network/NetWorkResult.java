package com.shuanghua.retrofit.network;

public interface NetWorkResult<T> {
    void success(T result);

    void error(Exception e, T data);
}
