package com.imes.base.network.loading;

import android.app.Activity;

public class LoadingDelegate implements ILoadingHandler {

    private static LoadingDelegate sInstance;
    private ILoadingHandler mDelegate;

    private LoadingDelegate(){}

    public synchronized static LoadingDelegate getInstance(){
        if(sInstance == null){
            sInstance = new LoadingDelegate();
        }
        return sInstance;
    }

    public static synchronized void init(ILoadingHandler delegate){
        sInstance = new LoadingDelegate();
        sInstance.mDelegate = delegate;
    }
    @Override
    public void create(Activity context) {
        if (mDelegate != null){
            mDelegate.create(context);
        }
    }

    @Override
    public void showLoading() {
        if(mDelegate!=null){
            mDelegate.showLoading();
        }
    }

    @Override
    public void dismissLoading() {
        if(mDelegate!=null){
            mDelegate.dismissLoading();
        }
    }

}
