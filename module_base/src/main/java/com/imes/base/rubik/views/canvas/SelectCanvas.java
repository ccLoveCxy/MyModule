package com.imes.base.rubik.views.canvas;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.imes.base.rubik.model.Element;
import com.imes.base.utils.ViewUtils;

/**
 * author : quintus
 * date : 2021/11/22 10:52
 * description :
 */
public class SelectCanvas {

    private View container;
    private final int cornerRadius = ViewUtils.dip2px(1.5f);
    private Paint cornerPaint = new Paint() {
        {
            setAntiAlias(true);
            setStrokeWidth(ViewUtils.dip2px(1));
        }
    };
    private Paint areaPaint = new Paint() {
        {
            setAntiAlias(true);
            setColor(Color.RED);
            setStyle(Style.STROKE);
            setStrokeWidth(ViewUtils.dip2px(1));
        }
    };
    private Paint dashLinePaint = new Paint() {
        {
            setAntiAlias(true);
            setColor(0xaaFF0000);
            setStyle(Style.STROKE);
            setPathEffect(new DashPathEffect(new float[]{ViewUtils.dip2px(3), ViewUtils.dip2px(3)}, 0));
        }
    };

    public SelectCanvas(View container) {
        container.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.container = container;
    }

    private int getMeasuredWidth() {
        return container.getMeasuredWidth();
    }

    private int getMeasuredHeight() {
        return container.getMeasuredHeight();
    }


    public void draw(Canvas canvas, Element... elements) {
        canvas.save();
        for (Element element : elements) {
            if (element != null) {
                drawSelected(canvas, element);
            }
        }
        canvas.restore();
    }

    private void drawSelected(Canvas canvas, Element element) {
        Rect rect = element.getRect();
        canvas.drawLine(0, rect.top, getMeasuredWidth(), rect.top, dashLinePaint);
        canvas.drawLine(0, rect.bottom, getMeasuredWidth(), rect.bottom, dashLinePaint);
        canvas.drawLine(rect.left, 0, rect.left, getMeasuredHeight(), dashLinePaint);
        canvas.drawLine(rect.right, 0, rect.right, getMeasuredHeight(), dashLinePaint);
        canvas.drawRect(rect, areaPaint);
        cornerPaint.setColor(Color.WHITE);
        cornerPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(rect.left, rect.top, cornerRadius, cornerPaint);
        canvas.drawCircle(rect.right, rect.top, cornerRadius, cornerPaint);
        canvas.drawCircle(rect.left, rect.bottom, cornerRadius, cornerPaint);
        canvas.drawCircle(rect.right, rect.bottom, cornerRadius, cornerPaint);
        cornerPaint.setColor(Color.RED);
        cornerPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(rect.left, rect.top, cornerRadius, cornerPaint);
        canvas.drawCircle(rect.right, rect.top, cornerRadius, cornerPaint);
        canvas.drawCircle(rect.left, rect.bottom, cornerRadius, cornerPaint);
        canvas.drawCircle(rect.right, rect.bottom, cornerRadius, cornerPaint);
    }
}
