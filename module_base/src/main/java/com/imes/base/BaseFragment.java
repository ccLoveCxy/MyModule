package com.imes.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.imes.base.mvp.IView;
import com.imes.base.network.loading.LoadingDelegate;
import com.imes.base.rubik.ui.connector.UIStateCallback;
import com.imes.base.utils.KeyboardUtil;
import com.imes.base.utils.ViewUtils;
import com.imes.module_base.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * author : quintus
 * date : 2021/11/17 16:29
 * description :
 */
public abstract class BaseFragment extends Fragment implements IView {
    protected final String TAG = getClass().getSimpleName();
    protected static final String PARAM1 = "param1";
    protected static final String PARAM2 = "param2";
    protected static final String PARAM3 = "param3";
    protected static final String PARAM4 = "param4";
    protected static final String PARAM_TITLE = "param_title";
    protected static final int CODE1 = 0x01;
    protected static final int CODE2 = 0x02;
    protected View mRootView;

    public BaseFragment() {
        setArguments(new Bundle());
    }

    protected final void launch(Class<? extends BaseFragment> target, Bundle extra) {
        launch(target, null, extra, -1);
    }

    protected final void launch(Class<? extends BaseFragment> target, Bundle extra, int reqCode) {
        launch(target, null, extra, reqCode);
    }

    protected final void launch(Class<? extends BaseFragment> target, String title, Bundle extra) {
        launch(target, title, extra, -1);
    }

    protected final void launch(Class<? extends BaseFragment> target, String title, Bundle extra, int reqCode) {
        if (getActivity() == null) {
            return;
        }
        closeSoftInput();
        if (extra == null) {
            extra = new Bundle();
        }
        extra.putString(PARAM_TITLE, title);
        try {
            Fragment fragment = target.newInstance();
            fragment.setArguments(extra);
            if (reqCode >= 0) {
                fragment.setTargetFragment(this, reqCode);
            }
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(Window.ID_ANDROID_CONTENT, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private Toolbar toolbar;
    private UIStateCallback uiState;
    private TextView tvError;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UIStateCallback) {
            uiState = (UIStateCallback) context;
        }
        // BUG FIX
        // setArguments is not allowed when fragment is active,
        // it is related to the diff in the version of the fragment.
    }

    @Override
    public void onDetach() {
        super.onDetach();
        uiState = null;

        if (enableEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        View view = getLayoutView();
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container,false);
        }
        View finalView;
        finalView = installToolbar(view);
        finalView.setClickable(true);
        mRootView = view;


        return finalView;
    }

    /**
     * 这个方法当onCreateView方法中的view创建完成之后，执行
     * 在inflate完成view的创建之后，可以将对应view中的各个控件进行查找findViewById
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (enableEventBus()) {
            EventBus.getDefault().register(this);
        }
        initView(view);
        initData(savedInstanceState);

    }

    protected void initView(View view) {

    }

    /**
     * 是否开启eventBus刷新
     */
    protected boolean enableEventBus() {
        return false;
    }

    /**
     * 初始化views
     *
     * @param state
     */
    public abstract void initData(Bundle state);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (uiState != null) {
            // Do not remove but choose to go out,
            // otherwise it will crash during animation
            uiState.hideHint();
        }
    }

    @Override
    public final Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0 || !enter) {
            if (enter) {
                if (getView() != null) {
                    onViewEnterAnimEnd(getView());
                }
            }
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Closes the fragment when the animation has not finished yet,
                // causing getView() is null
                if (getView() != null) {
                    onViewEnterAnimEnd(getView());
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return anim;
    }


    protected abstract @LayoutRes
    int getLayoutId();

    public boolean haveTitle() {
        return true;
    }

    protected View getLayoutView() {
        return null;
    }

    protected boolean enableSwipeBack() {
        return true;
    }

    /**
     * Provide an opportunity to start asynchronous tasks<p>
     * If we perform an asynchronous task in onViewCreated and the task is completed before
     * the animation completes. At this point, the padding data triggers the re-measure
     * and causes an animation exception.<p>
     * <p>
     * Callback when the fragment animation ends, later than onViewCreated
     *
     * @param container
     */
    protected void onViewEnterAnimEnd(View container) {

    }

    protected Toolbar onCreateToolbar() {
        return new Toolbar(getContext());
    }


    private View installToolbar(View view) {
        toolbar = onCreateToolbar();
        if (toolbar == null) {
            return view;
        }
        toolbar.setId(R.id.pd_toolbar_id);
        toolbar.setTitle(getArguments().getString(PARAM_TITLE, "Base"));
        toolbar.setBackgroundColor(getResources().getColor(R.color.pd_toolbar_bg));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.pd_close));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ViewGroup.LayoutParams toolbarParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar.setPadding(toolbar.getPaddingLeft(),
                    toolbar.getPaddingTop() + ViewUtils.getStatusBarHeight(getActivity()),
                    toolbar.getPaddingRight(), toolbar.getPaddingBottom());
        }
        RelativeLayout layout = new RelativeLayout(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        layout.addView(toolbar, toolbarParams);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlParams.addRule(RelativeLayout.BELOW, R.id.pd_toolbar_id);
        layout.addView(view, rlParams);
        return layout;
    }

    protected View afterInflateAndBeforeAny(View view) {
        return view;
    }

    protected final Toolbar getToolbar() {
        return toolbar;
    }

    protected final void onBackPressed() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    protected final void openSoftInput() {
        if (getContext() == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        } catch (Throwable ignore) {

        }
    }

    protected final void closeSoftInput() {
        if (getContext() == null) {
            return;
        }
        try {
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getActivity().getWindow().getDecorView().getWindowToken(), 0);
        } catch (Throwable ignore) {
        }
    }

    protected final void showError(String msg) {
        hideLoading();
        if (tvError == null) {
            tvError = new TextView(getContext());
            tvError.setGravity(Gravity.CENTER);
            tvError.setTextSize(16);
            tvError.setTextColor(getResources().getColor(R.color.pd_label));
            tvError.setBackgroundColor(getResources().getColor(R.color.pd_main_bg));
            tvError.setClickable(true);
            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rlParams.addRule(RelativeLayout.BELOW, R.id.pd_toolbar_id);
            ((RelativeLayout) toolbar.getParent()).addView(tvError, rlParams);
        }
        if (tvError.getVisibility() != View.VISIBLE) {
            tvError.setVisibility(View.VISIBLE);
        }
        tvError.setText(TextUtils.isEmpty(msg) ? "暂无数据" : msg);
    }

    protected final void hideError() {
        if (tvError != null) {
            tvError.setVisibility(View.GONE);
        }
    }

    @Override
    public void toast(String str) {
        ToastUtils.show(str);
    }

    @Override
    public void showLoading() {
        LoadingDelegate.getInstance().create(getActivity());
        LoadingDelegate.getInstance().showLoading();
    }

    @Override
    public void hideLoading() {
        LoadingDelegate.getInstance().dismissLoading();
        KeyboardUtil.closeKeyBoard(getActivity());
    }

}
