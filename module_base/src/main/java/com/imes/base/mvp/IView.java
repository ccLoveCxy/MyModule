package com.imes.base.mvp;

import android.content.Context;
import android.view.View;

/**
 * author : quintus
 * date : 3/4/21 8:36 AM
 * description :
 */
public interface IView {

    Context getContext();

//    /**
//     * 显示加载
//     */
//    void showLoading();
//
//    /**
//     * 隐藏加载
//     */
//    void hideLoading();
//
//    /**
//     * 显示加载
//     */
//    void showLoadingDialog();
//
//    /**
//     * 显示加载
//     */
//    void showLoadingDialog(String str);
//
//    /**
//     * 隐藏加载
//     */
//    void hideLoadingDialog();

    /**
     * 显示信息
     */
    void toast(String str);

    void showLoading();
    void hideLoading();
}
