package com.imes.base.network.module;

/**
 * 对后端返回的code进行检测
 */
public class CodeCheckDelegate {
    private ICodeChecker mDelegate;
    private static CodeCheckDelegate sInstance;


    public static synchronized void init(ICodeChecker delegate){
        sInstance = new CodeCheckDelegate();
        sInstance.mDelegate = delegate;
    }

    public synchronized static CodeCheckDelegate getInstance(){
        if(sInstance == null){
            sInstance = new CodeCheckDelegate();
        }
        return sInstance;
    }

    public void checkGlobalCode(String object) throws Exception {
        if(mDelegate != null){
            mDelegate.checkGlobalCode(object);
        }
    }

}
