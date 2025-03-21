package com.imes.base.network.loading;

import android.app.Activity;

public interface ILoadingHandler {
    void create(Activity context);

    void showLoading();

    void dismissLoading();


}
