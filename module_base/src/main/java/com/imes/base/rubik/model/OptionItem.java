package com.imes.base.rubik.model;


import com.imes.module_base.R;

/**
 * Created by linjiang on 2018/6/20.
 */

public class OptionItem extends BaseItem<String> {
    public OptionItem(String data) {
        super(data);
    }


    @Override
    public int getItemType() {
        return 0;
    }
}
