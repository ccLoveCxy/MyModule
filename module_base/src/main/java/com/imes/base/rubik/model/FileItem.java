package com.imes.base.rubik.model;

import android.view.View;

import com.imes.base.utils.FileUtil;
import com.imes.base.utils.Utils;
import com.imes.module_base.R;

import java.io.File;
import java.util.Locale;


/**
 * Created by linjiang on 04/06/2018.
 */

public class FileItem extends BaseItem<File> {
    private String info;
    private String fileName;

    public FileItem(File data) {
        super(data);
        if (!data.isDirectory()) {
            info = String.format(Locale.getDefault(), "%s    %s",
                    FileUtil.fileSize(data), Utils.millis2String(data.lastModified(), Utils.NO_MILLIS));

        } else {
            info = String.format(Locale.getDefault(), "%d items    %s",
                   Utils.getCount(data.list()), Utils.millis2String(data.lastModified(), Utils.NO_MILLIS));
        }
        fileName = data.getName();
    }

    public String getFileName() {
        return fileName;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public int getItemType() {
        return 1;
    }
}
