package com.imes.base.rubik.model;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.imes.module_base.R;


/**
 * Created by linjiang on 05/06/2018.
 */

public class KeyValueItem extends BaseItem<String[]> {
    public boolean isTitle;
    public boolean clickable;
    private String prefix;


    public KeyValueItem(String[] data) {
        super(data);
    }

    public KeyValueItem(String[] data, boolean isTitle) {
        super(data);
        this.isTitle = isTitle;
    }

    public KeyValueItem(String[] data, boolean isTitle, boolean clickable) {
        super(data);
        this.isTitle = isTitle;
        this.clickable = clickable;
    }

    public KeyValueItem(String[] data, boolean isTitle, boolean clickable, String prefix) {
        super(data);
        this.isTitle = isTitle;
        this.clickable = clickable;
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public int getItemType() {
        return 1;
    }
}
