package com.imes.base.network.okhttp;

import org.json.JSONException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * author : quintus
 * date : 4/25/21 2:44 PM
 * description : 请求类型为json提交
 */
public abstract class JsonRequest<T> extends BaseRequest<T> {

    @Override
    protected RequestBody buildBody() throws Exception {

        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body());
    }

    protected abstract String body() throws JSONException;

    @Override
    protected Headers buildHeader(BaseClientOption mOption) throws Exception {
        return mOption.headers(body());
    }

    @Override
    protected String info() throws Exception {
        return body();
    }

    @Override
    protected Method method() {
        return Method.POST;
    }
}
