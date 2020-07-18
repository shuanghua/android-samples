package com.shuanghua.retrofit.network;

public class ResultJava<T> {
    public static final class Success<T> extends ResultJava<T>{
        public T data;
        public Success(T data){
            this.data = data;
        }
    }

    public static final class Error<T> extends ResultJava<T>{
        public Exception e;
        public T data;  //失败状态下看情况否需要显示旧的数据
        public Error(Exception e, T data){
            this.e = e;
            this.data = data;
        }
    }
}
