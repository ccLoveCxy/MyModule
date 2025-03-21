package com.imes.base.network.okhttp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 生命周期管理
 */
class RequestLifecycle implements Application.ActivityLifecycleCallbacks {

    private static RequestLifecycle sInstance;

    private HashMap<Activity,ArrayList<BaseRequest>> mMap = new HashMap<Activity,ArrayList<BaseRequest>>();

    private RequestLifecycle(){
    }

    public static RequestLifecycle getInstance(){
        if(sInstance == null){
            sInstance = new RequestLifecycle();
        }
        return sInstance;
    }

    public void register(Activity activity,BaseRequest request){
        synchronized (mMap){
            if(mMap.containsKey(activity)){
                mMap.get(activity).add(request);
            }else{
                mMap.put(activity,new ArrayList<BaseRequest>(Collections.singleton(request)));
            }
        }
    }

    public void unregister(Activity activity,BaseRequest request){
        synchronized (mMap){
            if(mMap.get(activity) != null){
                mMap.get(activity).remove(request);
                if(mMap.get(activity).size() == 0){
                    mMap.remove(activity);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        synchronized (mMap){
            if(mMap.containsKey(activity)){
                for(BaseRequest request: mMap.get(activity)){
                    request.cancel();
                }
                mMap.remove(activity);
            }
        }
    }
}
