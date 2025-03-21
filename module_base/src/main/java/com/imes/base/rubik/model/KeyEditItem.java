package com.imes.base.rubik.model;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.imes.module_base.R;


/**
 * Created by linjiang on 07/06/2018.
 */

public class KeyEditItem extends BaseItem<String[]> {

    public boolean editable = true;
    public String hint;

    /**
     *
     * @param disable   if can edit
     * @param data      [0]: key [1]: value
     * @param hint      hint of editText
     */
    public KeyEditItem(boolean disable, String[] data, String hint) {
        super(data);
        this.editable = !disable;
        this.hint = hint;
    }

    public KeyEditItem(boolean disable, String[] data) {
        this(disable, data, null);
    }


    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (data != null && data.length >= 2) {
                data[1] = s.toString();
            }
        }
    };

    @Override
    public int getItemType() {
        return 1;
    }
}
