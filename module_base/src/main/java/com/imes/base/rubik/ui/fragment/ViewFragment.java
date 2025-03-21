package com.imes.base.rubik.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.imes.base.BaseFragment;
import com.imes.base.rubik.Rubik;
import com.imes.base.rubik.model.ViewBean;
import com.imes.base.rubik.views.GeneralDialog;
import com.imes.base.rubik.views.OperableView;
import com.imes.base.utils.ViewUtils;
import com.imes.module_base.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author : quintus
 * date : 2021/11/22 09:52
 * description :
 */
public class ViewFragment extends BaseFragment implements View.OnClickListener {
    private BottomSheetBehavior behavior;
    private OperableView operableView;
    private View targetView;
    private TextView tvType, tvClazz, tvPath, tvId, tvSize;
    private RecyclerView parentRv, currentRv, childRv;
    private ViewAdapter parentAdapter = new ViewAdapter(),
            currentAdapter = new ViewAdapter(),
            childAdapter = new ViewAdapter();
    @Override
    protected Toolbar onCreateToolbar() {
        return null;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.view_panel_hierarchy).setOnClickListener(v -> {
            targetView.setTag(R.id.pd_view_tag_for_unique, new Object());
            launch(HierarchyFragment.class, null);
        });
        tvType = view.findViewById(R.id.view_panel_type);
        tvClazz = view.findViewById(R.id.view_panel_clazz);
        tvPath = view.findViewById(R.id.view_panel_path);
        tvId = view.findViewById(R.id.view_panel_id);
        tvSize = view.findViewById(R.id.view_panel_size);
        parentRv = view.findViewById(R.id.view_panel_parent);
        parentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        parentRv.setAdapter(parentAdapter);
        parentAdapter.setListener(clickListener);
        currentRv = view.findViewById(R.id.view_panel_current);
        currentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        currentRv.setAdapter(currentAdapter);
        currentAdapter.setListener(clickListener);
        childRv = view.findViewById(R.id.view_panel_child);
        childRv.setLayoutManager(new LinearLayoutManager(getContext()));
        childRv.setAdapter(childAdapter);
        childAdapter.setListener(clickListener);

        GeneralDialog.build(-1)
                .title("提示")
                .message("① 单击以选中，再次单击取消选中，最多能选中两个.\n② 长按可以移动已选中的View.\n③ 点击下方面板查看View更多属性.\n④ 向上拖动面板查看更多操作.\n⑤ 点击返回键退出.")
                .positiveButton("OK")
                .show(this);
    }

    @Override
    public void initData(Bundle state) {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        View panelView = LayoutInflater.from(getContext()).inflate(R.layout.pd_layout_view_panel, null);
        operableView = new OperableView(getContext());
        operableView.tryGetFrontView(Rubik.get().getTopActivity());
        operableView.setOnClickListener(this);

        CoordinatorLayout layout = new CoordinatorLayout(getContext());
        CoordinatorLayout.LayoutParams selectViewParams = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addView(operableView, selectViewParams);
        CoordinatorLayout.LayoutParams panelViewParams = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        panelViewParams.setBehavior(behavior = new BottomSheetBehavior());
        // shadow's height is 18dp
        behavior.setPeekHeight(ViewUtils.dip2px(122));
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        layout.addView(panelView, panelViewParams);

        return layout;


    }

    @Override
    public void onClick(View v) {
        if (operableView.isSelectedEmpty()) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        targetView = v;
        refreshViewInfo(v);
    }

    private void refreshViewInfo(View target) {
        tvType.setText(target instanceof ViewGroup ? "group" : "view");
        tvClazz.setText(target.getClass().getSimpleName());
        tvPath.setText(target.getClass().getName());
        tvId.setText(ViewUtils.getIdString(target));
        int widthText = ViewUtils.px2dip(target.getWidth());
        int heightText = ViewUtils.px2dip(target.getHeight());
        tvSize.setText(String.format("%d x %d dp", widthText, heightText));
        parentAdapter.setList(null);
        currentAdapter.setList(null);
        childAdapter.setList(null);
        if (target instanceof ViewGroup) {
            List<ViewBean> childData = new ArrayList<>();
            for (int i = 0; i < ((ViewGroup)target).getChildCount(); i++) {
                View item = ((ViewGroup)target).getChildAt(i);
                childData.add(new ViewBean(item, false, true));
            }
            childAdapter.setList(childData);
        }
        if (target.getParent() != null && target.getParent() instanceof ViewGroup) {
            ViewGroup parentGroup = (ViewGroup) target.getParent();
            List<ViewBean> parentGroupData = new ArrayList<>();
            for (int i = 0; i < parentGroup.getChildCount(); i++) {
                View item = parentGroup.getChildAt(i);
                parentGroupData.add(new ViewBean(item, item == target, false));
            }
            currentAdapter.setList(parentGroupData);
            if (parentGroup.getParent() != null && parentGroup.getParent() instanceof ViewGroup) {
                ViewGroup grandGroup = (ViewGroup) parentGroup.getParent();
                List<ViewBean> grandGroupData = new ArrayList<>();
                for (int i = 0; i < grandGroup.getChildCount(); i++) {
                    View item = grandGroup.getChildAt(i);
                    grandGroupData.add(new ViewBean(item, false, item == target.getParent()));
                }
                parentAdapter.setList(grandGroupData);
            }
        }
    }


    static class ViewAdapter extends BaseQuickAdapter<ViewBean, BaseViewHolder>{
        private OnItemClickListener listener;

        public ViewAdapter() {
            super(R.layout.pd_item_view_name);
        }

        public void setListener(OnItemClickListener listener){
            this.listener  = listener;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        protected void convert(@NonNull BaseViewHolder holder, ViewBean viewBean) {
            holder.setText(R.id.view_name_title, viewBean.view.getClass().getSimpleName())
                    .setText(R.id.view_name_subtitle, ViewUtils.getIdString(viewBean.view));
            if (viewBean.selected) {
                holder.getView(R.id.view_name_wrapper).setBackgroundColor(getContext().getResources().getColor(R.color.pd_blue));
                holder.setTextColor(R.id.view_name_title, Color.WHITE)
                        .setTextColor(R.id.view_name_subtitle, Color.WHITE);
            } else {
                ViewCompat.setBackground(holder.getView(R.id.view_name_wrapper),
                        getContext().getResources().getDrawable(viewBean.related ? R.drawable.pd_shape_btn_bg_related : R.drawable.pd_shape_btn_bg));
                holder.setTextColor(R.id.view_name_title, 0xff000000)
                        .setTextColor(R.id.view_name_subtitle, getContext().getResources().getColor(R.color.pd_label_dark));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(holder.getAdapterPosition(),viewBean);
                    }
                }
            });

        }

        interface OnItemClickListener {
            void onItemClick(int position, ViewBean viewBean);
        }
    }

    private ViewAdapter.OnItemClickListener clickListener = new ViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, ViewBean item) {
                View clickItem = item.view;
                boolean selected = item.selected;
                if (!selected) {
                    boolean success = operableView.handleClick(clickItem);
                    if (!success) {
                        toast("view不可见");
                    }
                }
            }
        };

}
