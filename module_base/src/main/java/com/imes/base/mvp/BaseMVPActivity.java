package com.imes.base.mvp;

import android.os.Bundle;

import com.imes.base.BaseActivity;

/**
 * author : quintus
 * date : 2021/6/28 13:39
 * description :
 */
public abstract class BaseMVPActivity<P extends BasePresenter> extends BaseActivity {
    protected P mPresenter;


    @Override
    public void initData(Bundle savedInstanceState) {
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        init(savedInstanceState);
    }
    public abstract P createPresenter();
    public abstract void init(Bundle savedInstanceState);

    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter != null){
            mPresenter.onStart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
            this.mPresenter = null;
        }
    }
}
