package com.imes.base.rubik.model;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imes.base.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author : quintus
 * date : 2021/11/24 11:15
 * description :视图层级
 */
public class ViewHierarchy {
    public View view;
    public boolean isTarget;
    public boolean isExpand;
    public int layerCount;
    public int sysLayerCount;


    public static ViewHierarchy createRoot(View data) {
        ViewHierarchy hierarchyItem = new ViewHierarchy(data, 0);
        return hierarchyItem;
    }

    public ViewHierarchy(View data, int layerCount) {
        this.view = data;
        this.layerCount = layerCount;
    }

    public boolean isGroup() {
        return view instanceof ViewGroup;
    }

    public int getChildCount() {
        if (view instanceof ViewGroup){
            return ((ViewGroup) view).getChildCount();
        }
        return 0;
    }

    public void toggleIcon() {
        isExpand = !isExpand;
    }

    public List<ViewHierarchy> assembleChildren() {
        ViewGroup group = (ViewGroup) view;
        List<ViewHierarchy> result = new ArrayList<>();
        int newLayerCount = layerCount + 1;
        for (int i = 0; i < group.getChildCount(); i++) {
            ViewHierarchy item = new ViewHierarchy(group.getChildAt(i), newLayerCount);
            item.sysLayerCount = sysLayerCount;
            result.add(item);
        }
        return result;
    }

    public boolean isVisible() {
        return view.getVisibility() == View.VISIBLE;
    }

    public String viewToTitleString(View view) {
        if (isGroup()) {
            return view.getClass().getSimpleName() + " (" + getChildCount() + ")";
        } else {
            return view.getClass().getSimpleName();
        }
    }

    public String viewToSummaryString(View view) {
        return "{(" +
                view.getLeft() +
                ',' +
                view.getTop() +
                "), (" +
                view.getRight() +
                ',' +
                view.getBottom() +
                ")} " +
                ViewUtils.getIdString(view);
    }
}
