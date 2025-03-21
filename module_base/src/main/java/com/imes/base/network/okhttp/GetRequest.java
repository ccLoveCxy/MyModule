package com.imes.base.network.okhttp;

import org.json.JSONException;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Headers;

/**
 * author : quintus
 * date : 4/25/21 2:47 PM
 * description : get请求 基类,所有的get请求需继承此类
 */
public abstract class GetRequest<T> extends FormRequest<T> {


    @Override
    protected String url() {
        if (params() == null){
            return getUrl();
        }else {
            StringBuilder sb =  new StringBuilder("?");
            for (Map.Entry<String, Object> entry : params().entrySet()) {
                String key = (String) ((Map.Entry) entry).getKey();
                Object val = ((Map.Entry) entry).getValue();
                sb.append(key).append("=").append(val).append("&");
            }
            String params = sb.toString();
            if (params.endsWith("&")){
                params = params.substring(0,params.lastIndexOf("&"));
            }
            return getUrl()+params;
        }
    }

    protected abstract LinkedHashMap<String,Object> params();

    public abstract String getUrl();

    @Override
    protected LinkedHashMap<String, Object> body() {
        return null;
    }

    @Override
    protected String info() throws Exception {
        return url();
    }

    @Override
    protected Headers buildHeader(BaseClientOption mOption) throws JSONException {
        return mOption.headers(null);
    }

    @Override
    protected Method method() {
        return Method.GET;
    }

}
