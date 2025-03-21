package com.imes.base.rubik;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.imes.base.rubik.ui.Dispatcher;
import com.imes.base.rubik.ui.connector.Type;
import com.imes.base.rubik.views.FuncView;
import com.imes.base.utils.Utils;
import com.imes.module_base.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quintus on 2021.11.1
 */

class FuncController implements Application.ActivityLifecycleCallbacks, FuncView.OnItemClickListener {
    private Activity currentAct;
    private final FuncView funcView;
    private int activeCount;
    private final List<IFunc> functions = new ArrayList<>();

    FuncController(Application app) {
        funcView = new FuncView(app);
        funcView.setOnItemClickListener(this);
        app.registerActivityLifecycleCallbacks(this);
        addDefaultFunctions();
    }

    void addFunc(IFunc func) {
        if (!functions.contains(func)) {
            functions.add(func);
            funcView.addItem(func);
        }
    }

    void open() {
        if (funcView.isVisible()) {
            boolean succeed = funcView.open();
            if (!succeed) {
                Dispatcher.start(Utils.getContext(), Type.PERMISSION);
            }
        }
    }

    void close() {
        funcView.close();
    }

    private void showOverlay() {
        funcView.setVisibility(View.VISIBLE);
    }

    private void hideOverlay() {
        funcView.setVisibility(View.GONE);
    }

    @Override
    public boolean onItemClick(int index) {
        return functions.get(index).onClick();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        activeCount++;
        if (activeCount == 1) {
            showOverlay();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentAct = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
//        if (activity instanceof Dispatcher) {
//            if (activeCount > 0) {
//                showOverlay();
//            }
//        }
//        curInfoView.updateText(null);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activeCount--;
        if (activeCount <= 0) {
            hideOverlay();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private void addDefaultFunctions() {
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_network;
            }

            @Override
            public String getName() {
                return "网络日志";
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getContext(), Type.NET);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_disk;
            }

            @Override
            public String getName() {
                return "沙盒文件";
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getContext(), Type.FILE);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_select;
            }

            @Override
            public String getName() {
                return "UI工具";
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getContext(), Type.SELECT);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_bug;
            }

            @Override
            public String getName() {
                return "Crash";
            }

            @Override
            public boolean onClick() {
                Dispatcher.start(Utils.getContext(), Type.BUG);
                return false;
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_bug;
            }

            @Override
            public String getName() {
                return "Activity";
            }

            @Override
            public boolean onClick() {
                Toast.makeText(Utils.getContext(),currentAct.getClass().getName(),Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
}
