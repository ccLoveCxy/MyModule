package com.imes.base.rubik.ui.item;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import com.imes.base.database.Column;
import com.imes.base.rubik.model.BaseItem;
import com.imes.module_base.R;


/**
 * Created by linjiang on 03/06/2018.
 */

public class GridItem extends BaseItem<String> {

    public boolean isColumnName;
    private boolean isPrimaryKey;
    public String primaryKeyValue;
    public String columnName;

    public void setIsPrimaryKey() {
        isPrimaryKey = true;
    }

    public boolean isEnable() {
        return !isColumnName && !isPrimaryKey && !Column.ROW_ID.equals(columnName);
    }


    public GridItem(String columnValue, String primaryKeyValue, String columnName) {
        super(columnValue);
        this.primaryKeyValue = primaryKeyValue;
        this.columnName = columnName;
    }

    public GridItem(String data, boolean isColumnName) {
        super(data);
        this.isColumnName = isColumnName;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
