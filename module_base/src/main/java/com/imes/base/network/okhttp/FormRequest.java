package com.imes.base.network.okhttp;

import org.json.JSONException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * author : quintus
 * date : 4/25/21 2:38 PM
 * description :以form形式提交
 */
public abstract class FormRequest<T> extends BaseRequest<T> {
    /**
     * 请求体参数
     * @return
     */
    protected abstract LinkedHashMap<String,Object> body();

    @Override
    protected RequestBody buildBody(){
        if (body() == null){
            return null;
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : body().entrySet()) {
            String key = (String) ((Map.Entry) entry).getKey();
            Object val = ((Map.Entry) entry).getValue();
            builder.add(key,val.toString());
        }
        return builder.build();
    }


    @Override
    protected String info() throws Exception {
        Buffer buffer = new Buffer();
        buildBody().writeTo(buffer);
        Charset charset = StandardCharsets.UTF_8;
        return buffer.readString(charset);
    }

    @Override
    protected Headers buildHeader(BaseClientOption mOption) throws JSONException {
        return mOption.headers(body());
    }

}
