package com.imes.base.rubik.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Quintus on 2021.11.1
 */


public class KeyValueSummary implements MultiItemEntity {
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_CONTENT = 1;
    public static final int TYPE_EXCEPTION = 2;
    public String key, value;
    public boolean clickable;
    private int viewType;


    public KeyValueSummary(int type, String value) {
        this.viewType = type;
        this.value = value;
    }

    public KeyValueSummary(int type, String key, String value) {
        this(type, key, value, false);
    }

    public KeyValueSummary(int type, String key, String value, boolean clickable) {
        this.viewType = type;
        this.key = key;
        this.value = value;
        this.clickable = clickable;
    }

    @Override
    public int getItemType() {
        return viewType;
    }
}
