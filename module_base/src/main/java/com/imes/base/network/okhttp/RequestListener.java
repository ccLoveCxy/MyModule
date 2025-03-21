package com.imes.base.network.okhttp;

/**
 * 服务器请求 回调
 * @param <T>
 */
public interface RequestListener<T> {
    void onSuccess(T result);
    void onFailed(Throwable e);
}
