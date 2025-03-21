package com.imes.base.network.okhttp;

import com.imes.base.network.exception.EmptyInstanceException;
import com.imes.base.network.exception.ServiceException;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpServerClientImpl {
    private static final String TAG = "OkHttpServerClientImpl";
    private static OkHttpServerClientImpl sInstance;

    private OkHttpClient mClient;
    private BaseClientOption mOption;

    private OkHttpServerClientImpl(BaseClientOption mOption) {
        this.mOption = mOption;
        mClient = new ClientBuilder().buildClient(mOption);
    }

    public static void init(BaseClientOption option){
        sInstance = new OkHttpServerClientImpl(option);
    }

    public static OkHttpServerClientImpl getInstance() {
        if (sInstance == null) {
            throw new EmptyInstanceException("OkHttpServerClientImpl without initialized,please invoke init method first!");
        }
        return sInstance;
    }

    /**
     * 同步请求
     * @param baseRequest
     * @param <R>
     * @return
     * @throws Exception
     */
    public <R> String execute(BaseRequest<R> baseRequest) throws Exception {
        Request request = baseRequest.build(mOption);
        printlnRequestLog(request.url(), baseRequest);
        return post(request);
    }

    /**
     * 异步请求
     * @param request
     * @param <R>
     * @throws Exception
     */
    public <R> void enqueue(BaseRequest<R> request) {
        Executor executor = Executors.newFixedThreadPool(25);
        Observable.just(request)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.from(executor))
                .map(new Function<BaseRequest, Request>() {
                    @Override
                    public Request apply(BaseRequest baseRequest) throws Exception {
                        Request request = baseRequest.build(mOption);
                        printlnRequestLog(request.url(), baseRequest);
                        return request;
                    }
                })

                .map(new Function<Request, String>() {
                    @Override
                    public String apply(Request request) throws Exception {
                        return post(request);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);
    }

    private String post(Request request) throws IOException, JSONException, ServiceException {
        Response response = mClient.newCall(request).execute();
        String result = response.body().string();

        if (response.code() == 200) {
            RuntimeHelper.logDebug("response-->" + "[" + response.request().url() + "]:" + result);
            return result;
        } else {
            String message = "Server service is down, status code is:" + response.code() + " Message is: " + result;
            RuntimeHelper.logDebug(message);
            throw new ServiceException(response.code(),message);
        }
    }
    /**
     * 打印请求
     * @param url
     * @param request
     * @param <R>
     */
    private <R> void printlnRequestLog(HttpUrl url, BaseRequest<R> request){
        try {
            RuntimeHelper.logDebug(TAG+"request-->" + "[" + url.toString() + "]:" + request.info());
        } catch (Exception e) {
            RuntimeHelper.logDebug(TAG+"request-->" + "[" + url.toString() + "]:");
        }
    }

}
