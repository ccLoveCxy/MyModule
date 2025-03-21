package com.imes.base.rubik.views.canvas;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.imes.base.utils.ViewUtils;

/**
 * author : quintus
 * date : 2021/11/22 10:52
 * description :
 */

public class GridCanvas {

    private static final int LINE_INTERVAL = ViewUtils.dip2px(5);

    private View container;

    public GridCanvas(View container) {
        this.container = container;
    }

    private Paint paint = new Paint() {
        {
            setAntiAlias(true);
            setColor(0xff555555);
            setStrokeWidth(1);
        }
    };

    private int getMeasuredWidth() {
        return container.getMeasuredWidth();
    }

    private int getMeasuredHeight() {
        return container.getMeasuredHeight();
    }

    public void draw(Canvas canvas, float alpha) {
        canvas.save();
        int startX = 0;
        paint.setAlpha((int) (255 * alpha));
        while (startX < getMeasuredWidth()) {
            canvas.drawLine(startX, 0, startX, getMeasuredHeight(), paint);
            startX = startX + LINE_INTERVAL;
        }

        int startY = 0;
        while (startY < getMeasuredHeight()) {
            canvas.drawLine(0, startY, getMeasuredWidth(), startY, paint);
            startY = startY + LINE_INTERVAL;
        }
        canvas.restore();
    }
}
