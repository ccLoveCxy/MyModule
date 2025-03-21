package com.imes.base.rubik.model;


/**
 * Created by linjiang on 03/06/2018.
 */

public class TitleItem extends BaseItem<String> {
    public TitleItem(String data) {
        super(data);
    }


    @Override
    public int getItemType() {
        return 0;
    }
}
