package com.imes.base.mvp;

import android.os.Bundle;

import com.imes.base.BaseFragment;

/**
 * author : quintus
 * date : 2021/6/28 14:58
 * description : MVP模式的fragment 基类模型
 */
public abstract class BaseMVPFragment<P extends BasePresenter> extends BaseFragment {

    protected P mPresenter;

    @Override
    public void initData(Bundle state) {
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        init(state);
    }
    public abstract P createPresenter();
    public abstract void init(Bundle state);

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }


}
