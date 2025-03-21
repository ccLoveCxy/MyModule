package com.imes.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import okhttp3.internal.Util;

/**
 *
 */

public class ViewUtils {
    /**
     * 获取控件的高度
     */
    public static int getViewMeasuredHeight(View view) {
        calculateViewMeasure(view);
        return view.getMeasuredHeight();
    }

    /**
     * 获取控件的宽度
     */
    public static int getViewMeasuredWidth(View view) {
        calculateViewMeasure(view);
        return view.getMeasuredWidth();
    }

    /**
     * 获取文字的宽度
     */
    public static float getTextWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    public static float getTextHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public static String px2dipStr(float pxValue) {
        return String.format(Locale.getDefault(), "%ddp", px2dip(pxValue));
    }
    /**
     * 测量控件的尺寸
     */
    private static void calculateViewMeasure(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        view.measure(w, h);
    }

    /**
     * 获取通知栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeigh(Activity context) {
        Rect rectangle = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    /**
     * 检查文本框内容是否为空
     */
    public static boolean checkIsEmpty(EditText edit) {
        if (edit == null) {
            return true;
        } else if (edit.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 获取文本框中的值
     */
    public static String getEditString(EditText edit) {
        if (edit == null) {
            return null;
        } else {
            return edit.getText().toString().trim();
        }
    }

    /**
     * 判断文本框中的值是否相同
     */
    public static boolean IsSameStr(EditText edit1, EditText edit2) {
        if (ViewUtils.getEditString(edit1).equals(ViewUtils.getEditString(edit2))) {
            return true;
        }
        return false;
    }

    /**
     * 设置组件左边drawable
     */
    public static void setDrawableLeft(View view, int drawableId) {
        try {

            Drawable drawableImg;
            Resources res = view.getContext().getResources();
            drawableImg = res.getDrawable(drawableId);
            //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
            drawableImg.setBounds(0, 0, drawableImg.getMinimumWidth(), drawableImg.getMinimumHeight());

            if (view instanceof Button) {
                ((Button) view).setCompoundDrawables(drawableImg, null, null, null); //设置左图标
            }
            if (view instanceof TextView) {
                ((TextView) view).setCompoundDrawables(drawableImg, null, null, null); //设置左图标
            }
            if (view instanceof EditText) {
                ((EditText) view).setCompoundDrawables(drawableImg, null, null, null); //设置左图标
            }

        } catch (Exception e) {
        }
    }

    /**
     * 设置组件左边drawable
     */
    public static void setDrawableRight(View view, int drawableId) {
        try {
            Drawable drawableImg;
            Resources res = view.getContext().getResources();
            drawableImg = res.getDrawable(drawableId);
            //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
            drawableImg.setBounds(0, 0, drawableImg.getMinimumWidth(), drawableImg.getMinimumHeight());

            if (view instanceof Button) {
                ((Button) view).setCompoundDrawables(null, null, drawableImg, null); //设置左图标
            }
            if (view instanceof TextView) {
                ((TextView) view).setCompoundDrawables(null, null, drawableImg, null); //设置左图标
            }
            if (view instanceof EditText) {
                ((EditText) view).setCompoundDrawables(null, null, drawableImg, null); //设置左图标
            }

        } catch (Exception e) {
        }
    }


    /**
     * 隐藏输入法键盘
     */
    public static void hideInput(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        hideInput(weakReference);
    }

    /**
     * 隐藏键盘
     */
    public static void hideInput(WeakReference<Activity> activity) {
        InputMethodManager imm = (InputMethodManager) activity.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.get().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.get().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 显示输入法键盘
     */
    public static void showInput(WeakReference<Activity> activity, EditText editText) {
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        height =  context.getResources().getDimensionPixelSize(resourceId);
        if (height > 0){
            return height;
        }

        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int tmpHeight = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            height = context.getResources().getDimensionPixelSize(tmpHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }


    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 3;
            }
        }
        return result;
    }

    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @param type     1:MIUUI 2:Flyme 3:android6.0
     */
    public static void StatusBarLightMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    /**
     * 清除MIUI或flyme或6.0以上版本状态栏黑色字体
     */
    public static void StatusBarDarkMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }


    /**
     * MIUI的沉浸支持透明白色字体和透明黑色字体
     * https://dev.mi.com/console/doc/detail?pId=1159
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean darkmode) {
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            Class<? extends Window> clazz = window.getClass();
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 兼容状态栏透明（沉浸式）
     *
     * @param activity
     */
    public static void setImmersionStateMode(Activity activity) {
        StatusBarLightMode(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP) {
            // 透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }


    public static void setTitleBarByTop(View titleBarView, Context context) {
        if (Build.VERSION.SDK_INT >= 19 && titleBarView != null) {
            final ViewGroup.LayoutParams layoutParams = titleBarView.getLayoutParams();
            layoutParams.height = getViewMeasuredHeight(titleBarView) + getStatusBarHeight(context);
            titleBarView.setPadding(titleBarView.getPaddingLeft(), titleBarView.getPaddingTop() + getStatusBarHeight(context), titleBarView.getPaddingRight(), titleBarView.getPaddingBottom());
            titleBarView.setLayoutParams(layoutParams);
        }
    }

    public static void addStatuHeight(View titleBarView, Context context) {
        if (Build.VERSION.SDK_INT >= 19 && titleBarView != null) {
            final ViewGroup.LayoutParams layoutParams = titleBarView.getLayoutParams();
            layoutParams.height = getStatusBarHeight(context);
            titleBarView.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置某个View的margin
     *
     * @param view   需要设置的view
     * @param isDp   需要设置的数值是否为DP
     * @param left   左边距
     * @param right  右边距
     * @param top    上边距
     * @param bottom 下边距
     * @return
     */
    public static ViewGroup.LayoutParams setViewMargin(View view, boolean isDp, int left, int right, int top, int bottom) {
        if (view == null) {
            return null;
        }

        int leftPx = left;
        int rightPx = right;
        int topPx = top;
        int bottomPx = bottom;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = null;
        //获取view的margin设置参数
        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginParams = (ViewGroup.MarginLayoutParams) params;
        } else {
            //不存在时创建一个新的参数
            marginParams = new ViewGroup.MarginLayoutParams(params);
        }

       /* //根据DP与PX转换计算值
        if (isDp) {
            leftPx = SizeUtils.dp2px(left);
            rightPx = SizeUtils.dp2px(right);
            topPx = SizeUtils.dp2px(top);
            bottomPx = SizeUtils.dp2px(bottom);
        }*/
        //设置margin
        marginParams.setMargins(leftPx, topPx, rightPx, bottomPx);
        view.setLayoutParams(marginParams);
        return marginParams;
    }

    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static void setStatusBarColor(@NonNull Window window, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    public static void transStatusBar(@NonNull Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view = window.getDecorView();
            if (view != null) {
                view.setSystemUiVisibility(view.getSystemUiVisibility() | 1280);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    
    
    public static View tryGetTheFrontView(Activity targetActivity) {
        try {
            WindowManager windowManager = targetActivity.getWindowManager();
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                Field mWindowManagerField = Class.forName("android.view.WindowManagerImpl$CompatModeWrapper").getDeclaredField("mWindowManager");
                mWindowManagerField.setAccessible(true);
                Field mViewsField = Class.forName("android.view.WindowManagerImpl").getDeclaredField("mViews");
                mViewsField.setAccessible(true);
                List<View> views = Arrays.asList((View[]) mViewsField.get(mWindowManagerField.get(windowManager)));
                for (int i = views.size() - 1; i >= 0; i--) {
                    View targetView = getTargetDecorView(targetActivity, views.get(i));
                    if (targetView != null) {
                        return targetView;
                    }
                }
            }
            Field mGlobalField = ReflectUtil.getDeclaredField( ReflectUtil.forName("android.view.WindowManagerImpl"),"mGlobal");
            mGlobalField.setAccessible(true);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                Field mViewsField = Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mViews");
                mViewsField.setAccessible(true);
                List<View> views = (List<View>) mViewsField.get(mGlobalField.get(windowManager));
                for (int i = views.size() - 1; i >= 0; i--) {
                    View targetView = getTargetDecorView(targetActivity, views.get(i));
                    if (targetView != null) {
                        return targetView;
                    }
                }
            } else {
                Field mRootsField = ReflectUtil.getDeclaredField(ReflectUtil.forName("android.view.WindowManagerGlobal"),"mRoots");
                mRootsField.setAccessible(true);
                List viewRootImpls;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    viewRootImpls = (List) mRootsField.get(mGlobalField.get(windowManager));
                } else {
                    viewRootImpls = Arrays.asList((Object[]) mRootsField.get(mGlobalField.get(windowManager)));
                }
                for (int i = viewRootImpls.size() - 1; i >= 0; i--) {
                    Class clazz = ReflectUtil.forName("android.view.ViewRootImpl");
                    Object object = viewRootImpls.get(i);
                    Field mWindowAttributesField = ReflectUtil.getDeclaredField(clazz, "mWindowAttributes");
                    mWindowAttributesField.setAccessible(true);
                    Field mViewField = ReflectUtil.getDeclaredField(clazz, "mView");
                    mViewField.setAccessible(true);
                    View decorView = (View) mViewField.get(object);
                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mWindowAttributesField.get(object);
                    if (layoutParams.getTitle().toString().contains(targetActivity.getClass().getName())
                            || getTargetDecorView(targetActivity, decorView) != null) {
                        return decorView;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return targetActivity.getWindow().peekDecorView();
    }
    
    private static View getTargetDecorView(Activity targetActivity, View decorView) {
        View targetView = null;
        Context context = decorView.getContext();
        if (context == targetActivity) {
            targetView = decorView;
        } else {
            while (context instanceof ContextWrapper && !(context instanceof Activity)) {
                Context baseContext = ((ContextWrapper) context).getBaseContext();
                if (baseContext == null) {
                    break;
                }
                if (baseContext == targetActivity) {
                    targetView = decorView;
                    break;
                }
                context = baseContext;
            }
        }
        return targetView;
    }

    public static String getIdString(View view) {
        Context c = view.getContext();
        StringBuilder out = new StringBuilder();
        int id = view.getId();
        if (id != View.NO_ID && !isViewIdGenerated(id)) {
            try {
                String pkgName;
                switch (id&0xff000000) {
                    case 0x7f000000:
                        pkgName="app";
                        break;
                    case 0x01000000:
                        pkgName="android";
                        break;
                    default:
                        pkgName = c.getResources().getResourcePackageName(id);
                        break;
                }
                String typename = c.getResources().getResourceTypeName(id);
                String entryName = c.getResources().getResourceEntryName(id);
                out.append(pkgName);
                out.append(":");
                out.append(typename);
                out.append("/");
                out.append(entryName);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                out.append(Integer.toHexString(id));
            }
        } else {
            out.append("NO_ID");
        }
        return out.toString();
    }
    // see View.java
    // generateViewId(), isViewIdGenerated()
    private static boolean isViewIdGenerated(int id) {
        return (id & 0xFF000000) == 0 && (id & 0x00FFFFFF) != 0;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    public static int getScreenWidth(){
        WindowManager manager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(){
        WindowManager manager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}
