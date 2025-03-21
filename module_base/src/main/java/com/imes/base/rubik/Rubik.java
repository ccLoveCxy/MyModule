package com.imes.base.rubik;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.imes.base.database.Databases;
import com.imes.base.preference.SharedPref;
import com.imes.base.rubik.crash.CrashHandler;
import com.imes.base.rubik.network.OkHttpInterceptor;
import com.imes.base.rubik.sensor.SensorDetector;
import com.imes.base.utils.Utils;

import androidx.core.content.FileProvider;

/**
 * author : quintus
 * date : 2021/11/1 08:57
 * description :
 */
public final class Rubik extends FileProvider implements SensorDetector.Callback {
    private static Rubik INSTANCE;
    private SharedPref sharedPref;
    private boolean notHostProcess;
    private OkHttpInterceptor interceptor;
    private Databases databases;
    private CrashHandler crashHandler;
    private HistoryRecorder historyRecorder;
    private FuncController funcController;
    private SensorDetector sensorDetector;

    public OkHttpInterceptor getInterceptor() {
        return interceptor;
    }

    public Rubik() {
        if (INSTANCE != null) {
            throw new RuntimeException();
        }
    }

    public static Rubik get() {
        if (INSTANCE == null) {
            // Not the host process
            Rubik mB = new Rubik();
            mB.notHostProcess = true;
            mB.onCreate();
        }
        return INSTANCE;
    }
    @Override
    public void shakeValid() {
        open();
    }
    public Activity getTopActivity() {
        return historyRecorder.getTopActivity();
    }

    @Override
    public boolean onCreate() {
        INSTANCE = this;
        Context context = Utils.makeContextSafe(getContext());
        init(((Application) context));
        return super.onCreate();
    }

    private void init(Application app) {
        Utils.init(app);
        funcController = new FuncController(app);
        sensorDetector = new SensorDetector(notHostProcess ? null : this);
        interceptor = new OkHttpInterceptor();
        databases = new Databases();
        sharedPref = new SharedPref();
//        attrFactory = new AttrFactory();
        CrashHandler.getInstance().init(app);
        historyRecorder = new HistoryRecorder(app);
    }

    public void open() {
        if (notHostProcess) {
            return;
        }
        funcController.open();
    }

    public void close() {
        funcController.close();
    }

    public void disableShakeSwitch() {
        sensorDetector.unRegister();
    }


    public Databases getDatabases() {
        return databases;
    }

    public SharedPref getSharedPref() {
        return sharedPref;
    }
}
