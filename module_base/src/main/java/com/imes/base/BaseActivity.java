package com.imes.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.imes.base.mvp.IView;
import com.imes.base.network.loading.LoadingDelegate;
import com.imes.base.utils.KeyboardUtil;
import com.imes.base.utils.ViewUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * author : quintus
 * date : 2021/6/25 13:58
 * description :
 */
public abstract class BaseActivity extends AppCompatActivity implements IView {
    public String KEY;
    //解除绑定控件
    protected Dialog mLoadingDialog;
    @Nullable
    private ImmersionBar mImmersionBar;
    protected View contentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initBar();
        try {
//            ActivityManager.activityList.add(this);
            int layoutResID = getLayoutResId(savedInstanceState);

            if (layoutResID != 0) {

                contentView = LayoutInflater.from(this).inflate(layoutResID, null);
                setContentView(layoutResID);
                initView();
                //子类返回指定的标题导航，为标题setPadding.避免遮挡状态栏
                ViewUtils.setTitleBarByTop(getTitleBar(), this);
                initData(savedInstanceState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (enableEventBus()){
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityManager.activityList.add(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ActivityManager.activityList.remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    /**
     * 是否开启eventBus刷新
     */
    protected boolean enableEventBus() {
        return false;
    }

    /**
     * 初始化ImmersionBar
     */
    private void initBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar
//                .transparentStatusBar()  //透明状态栏，不写默认透明色
//                .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
                .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .autoDarkModeEnable(true) //自动状态栏字体和导航栏图标变色，必须指定状态栏颜色和导航栏颜色才可以自动变色哦
                .autoStatusBarDarkModeEnable(true, 0.2f) //自动状态栏字体变色，必须指定状态栏颜色才可以自动变色哦
                .autoNavigationBarDarkModeEnable(true, 0.2f) //自动导航栏图标变色，必须指定导航栏颜色才可以自动变色哦
                .fullScreen(false)      //有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
                .removeSupportAllView() //移除全部view支持
                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                .init();  //必须调用方可应用以上所配置的参数
    }

    @Override
    protected void onDestroy() {
//        ActivityManager.activityList.remove(this);
        super.onDestroy();
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        if (enableEventBus()){
            EventBus.getDefault().unregister(this);
        }
    }

    public abstract View getTitleBar();

    public abstract int getLayoutResId(Bundle savedInstanceState);
    public abstract void initView();
    public abstract void initData(Bundle savedInstanceState);

    @Override
    public Context getContext() {
        return this;
    }

    public void toast(String str) {
        ToastUtils.show(str);
    }

    @Override
    public void finish() {
        super.finish();
        KeyboardUtil.closeKeyBoard(this);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void showLoading(){
        LoadingDelegate.getInstance().create(this);
        LoadingDelegate.getInstance().showLoading();
    }

    @Override
    public void hideLoading(){
        LoadingDelegate.getInstance().dismissLoading();
        KeyboardUtil.closeKeyBoard(this);
    }

    public void logout(){
        new XPopup.Builder(this).asConfirm("提示", "退出登录？", "取消", "确定", new OnConfirmListener() {
            @Override
            public void onConfirm() {
                Intent intent = new Intent();
                //通过该方式打开不显示账号和密码
                intent.putExtra("clean",true);
                intent.setClassName(BaseActivity.this,"com.example.pms.LoginActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        },null,false).show();

    }
}

