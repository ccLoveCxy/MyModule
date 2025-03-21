package com.imes.base.rubik.ui.fragment;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.imes.base.rubik.views.GeneralDialog;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Quintus on 2021.11.1
 */

public class PermissionReqFragment extends Fragment {

    private final int code = 0x10;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        GeneralDialog.build(code)
                .title("权限提示")
                .message("需要悬浮窗权限来展示功能面板，请检查并前往允许。" )
                .positiveButton("OK")
                .negativeButton("取消")
                .cancelable(false)
                .show(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (code == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(getContext())) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            getActivity().finish();
        }
    }
}
