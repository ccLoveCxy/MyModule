package com.imes.base.rubik.model;

import android.graphics.Color;
import android.view.View;

import androidx.core.view.ViewCompat;

/**
 * author : quintus
 * date : 2021/11/23 11:05
 * description :
 */
public class ViewBean {
    public View view;
    public boolean selected;
    public boolean related;

    public ViewBean() {
    }

    public ViewBean(View view, boolean selected, boolean related) {
        this.view = view;
        this.selected = selected;
        this.related = related;
    }


}
