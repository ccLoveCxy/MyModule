package com.imes.base.rubik.views.canvas;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.imes.base.rubik.model.Element;
import com.imes.base.utils.ViewUtils;


/**
 * author : quintus
 * date : 2021/11/22 10:52
 * description :
 */

public class ClickInfoCanvas {
    private View container;

    public ClickInfoCanvas(View container) {
        this.container = container;
    }

    public ClickInfoCanvas(View container, boolean showInfoAlways) {
        this.container = container;
        this.showInfoAlways = showInfoAlways;
    }

    private final int cornerRadius = ViewUtils.dip2px(1.5f);
    private final int textBgFillingSpace = ViewUtils.dip2px(3);
    private final int textLineDistance = ViewUtils.dip2px(6);
    private Paint textPaint = new Paint() {
        {
            setAntiAlias(true);
            setTextSize(ViewUtils.dip2px(10));
            setColor(Color.RED);
            setStyle(Style.FILL);
            setStrokeWidth(ViewUtils.dip2px(1));
            setFlags(FAKE_BOLD_TEXT_FLAG);
        }
    };
    private Paint cornerPaint = new Paint() {
        {
            setAntiAlias(true);
            setStrokeWidth(ViewUtils.dip2px(1));
            setColor(Color.WHITE);
            setStyle(Style.FILL);
        }
    };
    private RectF tmpRectF = new RectF();
    private Element infoElement;
    private ValueAnimator infoAnimator;
    private boolean showInfoAlways = false;


    public void setInfoElement(Element infoElement) {
        this.infoElement = infoElement;
        if (!showInfoAlways) {
            animInfo();
        }
    }

    private void animInfo() {
        if (infoAnimator != null) {
            infoAnimator.removeAllUpdateListeners();
            infoAnimator.cancel();
        }
        infoAnimator = ObjectAnimator.ofInt(255, 0).setDuration(1400);
        infoAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                container.invalidate();
            }
        });
        infoAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                infoElement = null;
                container.invalidate();
            }

        });
        infoAnimator.start();
    }

    public void draw(Canvas canvas) {
        if (infoElement == null) {
            return;
        }
        boolean show = showInfoAlways;
        if (!show) {
            show = infoAnimator != null && infoAnimator.isRunning();
        }
        if (show) {
            int alpha = showInfoAlways ? 255 : (int) infoAnimator.getAnimatedValue();
            cornerPaint.setAlpha(alpha);
            textPaint.setAlpha(alpha);
            Rect rect = infoElement.getRect();
            String widthText = ViewUtils.px2dipStr(rect.width());
            drawText(canvas,
                    widthText,
                    rect.centerX() - ViewUtils.getTextWidth(textPaint, widthText) / 2,
                    rect.top - textLineDistance);

            String heightText = ViewUtils.px2dipStr(rect.height());
            drawText(canvas,
                    heightText,
                    rect.right + textLineDistance,
                    rect.centerY());
        }
    }

    private void drawText(Canvas canvas, String text, float x, float y) {
        float left = x - textBgFillingSpace;
        float top = y - ViewUtils.getTextHeight(textPaint, text);
        float right = x + ViewUtils.getTextWidth(textPaint, text) + textBgFillingSpace;
        float bottom = y + textBgFillingSpace;
        // ensure text in screen bound
        if (left < 0) {
            right -= left;
            left = 0;
        }
        if (top < 0) {
            bottom -= top;
            top = 0;
        }
        if (bottom > canvas.getHeight()) {
            float diff = top - bottom;
            bottom = canvas.getHeight();
            top = bottom + diff;
        }
        if (right > canvas.getWidth()) {
            float diff = left - right;
            right = canvas.getWidth();
            left = right + diff;
        }
        tmpRectF.set(left, top, right, bottom);
        canvas.drawRoundRect(tmpRectF, cornerRadius, cornerRadius, cornerPaint);
        canvas.drawText(text, left + textBgFillingSpace, bottom - textBgFillingSpace, textPaint);
    }
}
