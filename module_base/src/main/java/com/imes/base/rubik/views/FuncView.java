package com.imes.base.rubik.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imes.base.rubik.IFunc;
import com.imes.base.utils.Config;
import com.imes.base.utils.Utils;
import com.imes.base.utils.ViewUtils;
import com.imes.module_base.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Quintus on 2021.11.1
 */

public class FuncView extends LinearLayout {

    private static final String TAG = "PanelView";

    private FuncAdapter adapter;
    private float lastY;
    private float lastX;
    private RecyclerView recyclerView;
    private ImageView closeView;

    @SuppressLint("ClickableViewAccessibility")
    public FuncView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.drawable.pd_shadow_131124);
        ImageView moveView = new ImageView(context);
        recyclerView = new RecyclerView(context);
        closeView = new ImageView(context);

        moveView.setImageResource(R.drawable.pd_drag);
        moveView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        moveView.setOnTouchListener(touchListener);
        moveView.setOnClickListener(new MoveClick());

        closeView.setImageResource(R.drawable.pd_close);
        closeView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter = new FuncAdapter(new ArrayList<>()));

        addView(moveView, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        addView(recyclerView, new LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1
        ));
        addView(closeView, new LayoutParams(
                ViewUtils.dip2px(40), ViewGroup.LayoutParams.MATCH_PARENT
        ));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // drag + close + 5*func + 0.5*func
            maxWidth = ViewUtils.dip2px(64) + ViewUtils.dip2px(50) * 5 + ViewUtils.dip2px(24);
        } else {
            maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(
                Math.min(MeasureSpec.getSize(widthMeasureSpec), maxWidth),
                MeasureSpec.getMode(widthMeasureSpec)
        ), heightMeasureSpec);
    }

    private float downX,downY;
    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = lastY = event.getRawY();
                    downX = lastX = event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
                    params.y += event.getRawY() - lastY;
                    params.y = Math.max(0, params.y);
                    params.x += event.getRawX() - lastX;
                    params.x = Math.max(0, params.x);
                    Utils.updateViewLayoutInWindow(FuncView.this, params);
                    lastY = event.getRawY();
                    lastX = event.getRawX();
                    Utils.cancelTask(task);
                    Utils.postDelayed(task, 200);
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(lastX - downX) < 10 && Math.abs(lastY - downY) < 10) {
                      try {
                                Field field = View.class.getDeclaredField("mListenerInfo");
                                field.setAccessible(true);
                                Object object = field.get(v);
                                field = object.getClass().getDeclaredField("mOnClickListener");
                                field.setAccessible(true);
                                object = field.get(object);
                                if (object != null && object instanceof View.OnClickListener) {
                                    ((View.OnClickListener) object).onClick(v);
                                }
                            } catch (Exception e) {

                            }
                    }else {
                        params = (WindowManager.LayoutParams) getLayoutParams();
                        if (event.getRawX() <= ViewUtils.getScreenWidth()/2){
                            params.x = 0;
                        }else {
                            params.x = ViewUtils.getScreenWidth()-getMeasuredWidth();
                        }
                        Utils.updateViewLayoutInWindow(FuncView.this, params);
                        lastY = event.getRawY();
                        lastX = params.x;
                        Utils.cancelTask(task);
                        Utils.postDelayed(task, 200);
                    }

            }
            return true;
        }
    };

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            Config.setDragY(lastY);
            Config.setDragX(lastX);
        }
    };

    public void addItem(IFunc func) {
        adapter.addItem(func);
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        adapter.setListener(new FuncAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, IFunc item) {
                listener.onItemClick(position);
            }
        });
    }

    public boolean open() {
        if (ViewCompat.isAttachedToWindow(this)) {
            return true;
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = ViewUtils.dip2px(62);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP | Gravity.START;
        params.x =(int) Config.getDragX();
        params.y = (int) Config.getDragY();
        return Utils.addViewToWindow(this, params);
    }

    public void close() {
        if (ViewCompat.isAttachedToWindow(this)) {
            Utils.removeViewFromWindow(this);
        }
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }


    public interface OnItemClickListener {
        boolean onItemClick(int index);
    }


    public static class FuncAdapter extends BaseQuickAdapter<IFunc, BaseViewHolder>{
        private OnItemClickListener listener;

        public FuncAdapter(List<IFunc> list) {
            super(R.layout.pd_item_func,list);
        }

        public void setListener(OnItemClickListener listener){
            this.listener = listener;
        }
        public void addItem(IFunc item){
            getData().add(item);
            notifyDataSetChanged();
        }
        @Override
        protected void convert(@NonNull BaseViewHolder holder, IFunc funcItem) {
            holder.setImageResource(R.id.icon,funcItem.getIcon())
                    .setText(R.id.title,funcItem.getName());
            ImageView imageView = holder.getView(R.id.icon);
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(holder.getAdapterPosition(),funcItem);
                    }
                }
            });
        }

        interface OnItemClickListener {
            void onItemClick(int position, IFunc item);
        }
    }

    class MoveClick implements OnClickListener{

        @Override
        public void onClick(View v) {
            if (recyclerView.getVisibility() == VISIBLE){
                recyclerView.setVisibility(GONE);
                closeView.setVisibility(GONE);
            }else {
                recyclerView.setVisibility(VISIBLE);
                closeView.setVisibility(VISIBLE);
            }
        }
    }
}
