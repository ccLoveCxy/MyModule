package com.imes.base.rubik.model;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by linjiang on 06/06/2018.
 */

public class ContentItem extends NameItem {

    private boolean focus;

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public ContentItem(String data) {
        super(data);
    }

}
