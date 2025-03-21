package com.imes.base.rubik.model;

import androidx.annotation.LayoutRes;

import com.chad.library.adapter.base.entity.MultiItemEntity;


public abstract class BaseItem<T> implements MultiItemEntity {

    public T data;

    public BaseItem(T data) {
        this.data = data;
    }

    private Object tag;

    public final BaseItem setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public final Object getTag() {
        return tag;
    }
}
