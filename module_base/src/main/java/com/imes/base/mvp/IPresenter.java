package com.imes.base.mvp;

import android.app.Activity;

/**
 * author : quintus
 * date : 3/4/21 8:45 AM
 * description :
 */
public interface IPresenter<V extends IView> {

    /**
     * 做一些初始化操作
     */
    void onStart();

    /**
     * 在框架中 {@link Activity#onDestroy()} 时会默认调用 {@link IPresenter#onDestroy()}
     */
    void onDestroy();
    /**
     * 绑定视图
     *
     * @param view
     */
    void attachView(V view);

    V getView();
}
