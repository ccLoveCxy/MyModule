package com.imes.base.rubik.crash;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.bumptech.glide.util.MarkEnforcingInputStream;
import com.imes.base.rubik.cache.Crash;

/**
 * author : quintus
 * date : 2021/12/6 11:12
 * description :
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private long launchTime;
    private Thread.UncaughtExceptionHandler defHandler;
    private static CrashHandler mInstance;

    private CrashHandler() {
    }

    public static CrashHandler getInstance(){
        if (mInstance == null){
            mInstance = new CrashHandler();
        }
        return mInstance;
    }

    public void init(final Application app){
        launchTime = System.currentTimeMillis();
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                /*
                 Registering here is to prevent other crash-listeners from not callback
                 in case CrashHandler Registered before Application's onCreate.
                 */
                app.unregisterActivityLifecycleCallbacks(this);
                defHandler = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(CrashHandler.this);
            }

            @Override public void onActivityStarted(Activity activity) {}
            @Override public void onActivityResumed(Activity activity) {}
            @Override public void onActivityPaused(Activity activity) {}
            @Override public void onActivityStopped(Activity activity) {}
            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
            @Override public void onActivityDestroyed(Activity activity) {}
        });
    }
//    public CrashHandler(final Application app) {
//
//    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Crash.insert(e, launchTime);
        if (defHandler != null) {
            defHandler.uncaughtException(t, e);
        }
    }

}