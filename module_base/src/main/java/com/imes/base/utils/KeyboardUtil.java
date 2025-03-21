package com.imes.base.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

/**
 * author : quintus
 * date : 2021/6/25 14:22
 * description : 软键盘相关
 */
public class KeyboardUtil {
    /**
     * 强制关闭软键盘
     */
    public static void closeKeyBoard(Activity ac) {
        try {
            InputMethodManager imm = (InputMethodManager) ac.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && ac.getCurrentFocus() != null && ac.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(ac.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeKeyBoard(Fragment fragment){
        try {
            View v = fragment.getActivity().getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
