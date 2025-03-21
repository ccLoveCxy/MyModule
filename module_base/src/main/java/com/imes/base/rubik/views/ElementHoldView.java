package com.imes.base.rubik.views;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.imes.base.rubik.model.Element;
import com.imes.base.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author : quintus
 * date : 2021/11/22 09:52
 * description :
 */
public class ElementHoldView extends View {
    private static final String TAG = "ElementHoldView";

    public ElementHoldView(Context context) {
        super(context);
    }

    private List<Element> elements = new ArrayList<>();


    private void traverse(View view) {
        if (view.getAlpha() == 0 || view.getVisibility() != View.VISIBLE) return;
        elements.add(new Element(view));
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i++) {
                traverse(parent.getChildAt(i));
            }
        }
    }

    protected final Element getTargetElement(float x, float y) {
        Element target = null;
        for (int i = elements.size() - 1; i >= 0; i--) {
            final Element element = elements.get(i);
            if (element.getRect().contains((int) x, (int) y)) {
                if (isParentNotVisible(element.getParentElement())) {
                    continue;
                }
                target = element;
                break;
            }
        }
        if (target == null) {
            Log.w(TAG, "getTargetElement: not find");
        }
        return target;
    }

    protected final Element getTargetElement(View v) {
        Element target = null;
        for (int i = elements.size() - 1; i >= 0; i--) {
            final Element element = elements.get(i);
            if (element.getView() == v) {
                target = element;
                break;
            }
        }
        if (target == null) {
            Log.w(TAG, "getTargetElement: not find");
        }
        return target;
    }


    protected final void resetAll() {
        for (Element e : elements) {
            if (e != null) {
                e.reset();
            }
        }
    }

    private boolean isParentNotVisible(Element parent) {
        if (parent == null) {
            return false;
        }
        if (parent.getRect().left >= getMeasuredWidth()
                || parent.getRect().top >= getMeasuredHeight()) {
            return true;
        } else {
            return isParentNotVisible(parent.getParentElement());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        elements.clear();
    }

    public void tryGetFrontView(Activity targetActivity) {
        View decor = ViewUtils.tryGetTheFrontView(targetActivity);
        if (decor != null) {
            traverse(decor);
        }
    }

}
