package com.imes.base.network.loading;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.imes.module_base.R;

import java.lang.ref.WeakReference;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * 默认的loading框
 */
public class LoadingHandler implements ILoadingHandler {
    private static final String TAG = LoadingHandler.class.getSimpleName();

    private WeakReference<Activity> mActivity;
    protected AlertDialog dialog;
    protected Toast toast;
    private int count;
    private Handler handler;

    private Application context;

    public LoadingHandler(Application context){
        this.context = context;
        count = 0;
    }

    @Override
    public void create(Activity activity) {
        if (dialog == null && activity != null) {
            mActivity = new WeakReference<>(activity);
            handler = new Handler();
            initDialog();
        }
    }

    public synchronized void showLoading() {
        if(handler == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(dialog == null){
                    return;
                }
                if (dialog.isShowing()) {

                } else {
                    try {
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setContentView(R.layout.m_loading_bg);
                    } catch (Exception e) {
                        Log.e(TAG,e.getMessage());
                    }
                }
                count++;
            }
        });
    }

    public synchronized void dismissLoading() {
        if (count <= 0 || dialog == null || handler == null) {
            return;
        }
        count--;
        if (mActivity != null && null != mActivity.get() && !mActivity.get().isFinishing()){
            if (Looper.myLooper() == handler.getLooper()){
                dismissDialog();
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                    }
                });
            }
        } else {
            dialog = null;
            count = 0;
        }
    }

    private void dismissDialog(){
        try {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    private void initDialog() {
        if (mActivity != null && mActivity.get() != null) {
            dialog = new AlertDialog.Builder(mActivity.get(), AlertDialog.THEME_HOLO_LIGHT).create();
            //dialog不要再dismiss回调中计数清0，有一种情况如：
            //前一个调了dismiss，又有一个dialog进来，计数加1，这个时候dismiss的回调把计数清零了，导致第二个dialog消失不掉
            dialog.setOnCancelListener(null);
            dialog.setCanceledOnTouchOutside(false);
        }
    }
}
