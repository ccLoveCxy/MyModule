package com.imes.base.mvp;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * author : quintus
 * date : 3/4/21 8:49 AM
 * description :
 */
public class BasePresenter<V extends IView> implements IPresenter<V>, LifecycleObserver {
    protected V mRootView;

    @Override
    public void onStart() {
        //mRootView一定是activity的实现类。所有通过getLifecycle().addObserver(this);方法可以将activity的生命周期监听
        if (getView() != null && getView() instanceof LifecycleOwner) {
            ((LifecycleOwner) getView()).getLifecycle().addObserver(this);
        }
    }

    @Override
    public void onDestroy() {
//        this.mRootView = null;
    }

    @Override
    public void attachView(V view) {
        mRootView = view;
    }

    @Override
    public V getView() {
        return mRootView;
    }
}
