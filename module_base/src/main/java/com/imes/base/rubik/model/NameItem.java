package com.imes.base.rubik.model;

import android.view.View;



public class NameItem extends BaseItem<String> {


    public NameItem(String data) {
        super(data);
    }

    @Override
    public int getItemType() {
        return 1;
    }
}
