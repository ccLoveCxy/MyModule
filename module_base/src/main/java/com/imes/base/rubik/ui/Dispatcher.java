package com.imes.base.rubik.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.imes.base.rubik.ui.connector.Type;
import com.imes.base.rubik.ui.connector.UIStateCallback;
import com.imes.base.rubik.ui.fragment.CrashFragment;
import com.imes.base.rubik.ui.fragment.NetFragment;
import com.imes.base.rubik.ui.fragment.PermissionReqFragment;
import com.imes.base.rubik.ui.fragment.SandboxFragment;
import com.imes.base.rubik.ui.fragment.ViewFragment;
import com.imes.base.utils.ViewUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Created by Quintus on 2021.11.1
 */

public class Dispatcher extends AppCompatActivity implements UIStateCallback {

    public static final String PARAM1 = "param1";

    public static void start(Context context, @Type int type) {
        boolean needTrans = type == Type.SELECT;
        Intent intent = new Intent(context, needTrans ? TransActivity.class : Dispatcher.class)
                .putExtra(PARAM1, type);
        // This flag is very ridiculous in different android versions, like a bug
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private @Type
    int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra(PARAM1, Type.FILE);
        ViewUtils.setStatusBarColor(getWindow(), Color.TRANSPARENT);
        ViewUtils.transStatusBar(getWindow());
        dispatch(savedInstanceState);
    }

    private void dispatch(Bundle savedInstanceState) {
        switch (type) {
            case Type.SELECT:
                if (savedInstanceState == null) {
                    addFragment(ViewFragment.class);
                } else {
                    finish();
                }
                break;
            case Type.NET:
                if (savedInstanceState == null) {
                    addFragment(NetFragment.class);
                }
                break;
            case Type.FILE:
                if (savedInstanceState == null) {
                    addFragment(SandboxFragment.class);
                }
                break;
            case Type.BUG:
                if (savedInstanceState == null) {
                    addFragment(CrashFragment.class);
                }
                break;
            case Type.PERMISSION:
                addFragment(PermissionReqFragment.class);
                break;
        }

    }

    private void addFragment(Class<? extends Fragment> clazz) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .add(Window.ID_ANDROID_CONTENT, clazz.newInstance())
                    .commit();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private View hintView;

    @Override
    public void showHint() {
        if (hintView == null) {
            hintView = new ProgressBar(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            hintView.setLayoutParams(params);
        }
        if (hintView.getParent() == null) {
            if (getWindow() != null) {
                if (getWindow().getDecorView() instanceof ViewGroup) {
                    ((ViewGroup) getWindow().getDecorView()).addView(hintView);
                }
            }
        }
        if (hintView.getVisibility() == View.GONE) {
            hintView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideHint() {
        if (hintView != null) {
            if (hintView.getVisibility() != View.GONE) {
                hintView.setVisibility(View.GONE);
            }
        }
    }

}
