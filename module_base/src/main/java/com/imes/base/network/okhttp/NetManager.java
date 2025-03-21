package com.imes.base.network.okhttp;

import android.app.Application;

import com.imes.base.network.loading.ILoadingHandler;
import com.imes.base.network.loading.LoadingDelegate;
import com.imes.base.network.module.CodeCheckDelegate;
import com.imes.base.network.module.ICodeChecker;
import com.imes.base.rubik.Rubik;
import com.imes.module_base.BuildConfig;


/**
 * 网络库管理类，需要在application里初始化
 *
 *         NetManager.init(getApplication(),new CustomClientOption());
 *         NetManager.domain("http://47.99.105.168:8380");
 *         NetManager.debugMode(true);
 *         NetManager.loading(new LoadingHandler(getApplication()));
 *         NetManager.codeCheck(new CodeCheckerImpl());
 */
public class NetManager {
    public static void init(Application context,BaseClientOption option){
        OkHttpServerClientImpl.init(option);
        context.registerActivityLifecycleCallbacks(RequestLifecycle.getInstance());
        if (BuildConfig.DEBUG){

        }
    }

    public static void debugMode(boolean debug) {
        RuntimeHelper.DEBUG = debug;
        if (!debug){
//            Rubik.get().disableShakeSwitch();
        }
    }

    public static void domain(String domain) {
        RuntimeHelper.DOMAIN = domain;
    }

    public static void loading(ILoadingHandler loadingHandler) {
        LoadingDelegate.init(loadingHandler);
    }
    public static void codeCheck(ICodeChecker checker) {
        CodeCheckDelegate.init(checker);
    }
}
