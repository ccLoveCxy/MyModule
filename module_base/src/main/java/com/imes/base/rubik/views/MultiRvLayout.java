package com.imes.base.rubik.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;

/**
 * author : quintus
 * date : 2021/11/22 09:52
 * description :
 */
public class MultiRvLayout extends LinearLayout implements NestedScrollingChild {

    public MultiRvLayout(Context context) {
        super(context);
    }

    public MultiRvLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiRvLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return true;
    }
}
