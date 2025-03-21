package com.imes.base.utils;

import android.os.SystemClock;

/**
 * author : quintus
 * date : 3/4/21 9:57 AM
 * description :
 */
public class AppUtils {

    //防止重复点击
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = SystemClock.elapsedRealtime();
        if ((curClickTime - lastClickTime) <= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }



}
