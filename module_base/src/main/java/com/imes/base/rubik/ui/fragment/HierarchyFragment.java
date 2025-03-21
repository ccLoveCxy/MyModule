package com.imes.base.rubik.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imes.base.BaseFragment;
import com.imes.base.rubik.Rubik;
import com.imes.base.rubik.cache.Summary;
import com.imes.base.rubik.model.ViewHierarchy;
import com.imes.base.rubik.views.TreeNodeLayout;
import com.imes.base.utils.Config;
import com.imes.base.utils.ViewUtils;
import com.imes.module_base.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author : quintus
 * date : 2021/11/24 10:21
 * description :
 */
public class HierarchyFragment extends BaseFragment {
    private LevelAdapter mAdapter;
    private RecyclerView recyclerView;
    private boolean isExpand = true;
    private View targetView;
    private int sysLayerCount;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = ViewUtils.tryGetTheFrontView(Rubik.get().getTopActivity());
        if (!Config.getUI_IGNORE_SYS_LAYER()) {
            sysLayerCount = countSysLayers();
        } else {
            if (rootView != null) {
                rootView = rootView.findViewById(android.R.id.content);
            }
            sysLayerCount = 0;
        }
        targetView = findViewByDefaultTag();
        if (targetView != null) {
            // clear flag
            targetView.setTag(R.id.pd_view_tag_for_unique, null);
        }
    }
    private int countSysLayers() {
        View content = rootView.findViewById(android.R.id.content);
        int layer = 0;
        if (content != null) {
            View current = content;
            while (current.getParent() != null) {
                layer++;
                if (current.getParent() instanceof View) {
                    current = (View) current.getParent();
                } else {
                    break;
                }
            }
        }
        return layer;
    }

    private View findViewByDefaultTag() {
        return findViewByDefaultTag(rootView);
    }
    private View findViewByDefaultTag(View root) {
        if (root.getTag(R.id.pd_view_tag_for_unique) != null) {
            return root;
        }
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = findViewByDefaultTag(parent.getChildAt(i));
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

    @Override
    public void initData(Bundle state) {
        getToolbar().setTitle("视图层级");

        recyclerView.setBackgroundColor(Color.WHITE);
        mAdapter.setListener(new LevelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ViewHierarchy vh) {
                if (vh.isGroup() && vh.getChildCount() > 0) {
                    if (!vh.isExpand) {
                        List<ViewHierarchy> expands = vh.assembleChildren();
                        insertItems(expands, position + 1);
                    } else {
                        List<ViewHierarchy> expands = getAllExpandItems(vh, position + 1);
                        removeItems(expands);
                    }
                    vh.toggleIcon();
                    mAdapter.notifyDataSetChanged();
                }
            }

        });

        expandAllViews();
    }

    private void removeItems(List<ViewHierarchy> data) {
        final List<ViewHierarchy> tmpData = new ArrayList<>();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            tmpData.add(mAdapter.getItem(i));
        }
        mAdapter.getData().removeAll(data);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getNewListSize() {
                return mAdapter.getItemCount();
            }

            @Override
            public int getOldListSize() {
                return tmpData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                ViewHierarchy newHierarchyItem = mAdapter.getItem(newItemPosition);
                ViewHierarchy oldHierarchyItem =  tmpData.get(oldItemPosition);
                return oldHierarchyItem.view == newHierarchyItem.view;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                // NOTICE: position also needs be compared
                return oldItemPosition == newItemPosition;
            }
        });
        result.dispatchUpdatesTo(mAdapter);
    }
    private void insertItems(List<ViewHierarchy> data, int pos) {
        final List<ViewHierarchy> tmpData = new ArrayList<>();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            tmpData.add(mAdapter.getItem(i));
        }
        mAdapter.getData().addAll(pos, data);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getNewListSize() {
                return mAdapter.getItemCount();
            }

            @Override
            public int getOldListSize() {
                return tmpData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                ViewHierarchy newHierarchyItem = mAdapter.getItem(newItemPosition);
                ViewHierarchy oldHierarchyItem =  tmpData.get(oldItemPosition);
                return oldHierarchyItem.view == newHierarchyItem.view;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                // NOTICE: position also needs be compared
                return oldItemPosition == newItemPosition;
            }
        });
        result.dispatchUpdatesTo(mAdapter);
    }


    private List<ViewHierarchy> getAllExpandItems(ViewHierarchy hierarchyItem, int pos) {
        List<ViewHierarchy> result = new ArrayList<>();
        if (hierarchyItem.isExpand && hierarchyItem.getChildCount() > 0) {
            for (int i = pos; i < mAdapter.getItemCount(); i++) {
                ViewHierarchy curItem = mAdapter.getItem(i);
                if (hierarchyItem.layerCount >= curItem.layerCount) {
                    break;
                }
                result.add(curItem);
                if (curItem.isGroup()) {
                    List<ViewHierarchy> subChildren = getAllExpandItems(curItem, i + 1);
                    result.addAll(subChildren);
                    i += subChildren.size();
                }
            }
        }
        return result;
    }

    private void expandAllViews() {
        List<ViewHierarchy> data = new ArrayList<>();
        ViewHierarchy rootItem = ViewHierarchy.createRoot(rootView);
        rootItem.sysLayerCount = sysLayerCount;
        data.add(rootItem);
        assembleItems(data, rootItem);
        mAdapter.setList(data);
    }

    private void assembleItems(List<ViewHierarchy> container, ViewHierarchy hierarchyItem) {
        if (hierarchyItem.view == targetView) {
            hierarchyItem.isTarget = true;
        }
        if (hierarchyItem.isGroup() && hierarchyItem.getChildCount() > 0) {
            hierarchyItem.isExpand = true;
            List<ViewHierarchy> expands = hierarchyItem.assembleChildren();
            for (int i = 0; i < expands.size(); i++) {
                ViewHierarchy childItem = expands.get(i);
                container.add(childItem);
                assembleItems(container, childItem);
            }
        }
    }
    @Override
    protected View getLayoutView() {
        mAdapter = new LevelAdapter();
        recyclerView = new RecyclerView(getContext());
        recyclerView.setBackgroundColor(getResources().getColor(R.color.pd_main_bg));
        recyclerView.setLayoutManager(onCreateLayoutManager());
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(0xffE5E5E5);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(mAdapter);
        return recyclerView;
    }

    private RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getContext()) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView,
                                               RecyclerView.State state, final int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        // let scroll smooth more
                        return 120f / displayMetrics.densityDpi;
                    }

                    @Override
                    protected int getVerticalSnapPreference() {
                        return SNAP_TO_START;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        rootView = null;
        targetView = null;
    }

    static class LevelAdapter extends BaseQuickAdapter<ViewHierarchy, BaseViewHolder> {
        private OnItemClickListener listener;
        public LevelAdapter() {
            super(R.layout.pd_item_hierachy);
        }

        public void setListener(OnItemClickListener listener){
            this.listener = listener;
        }
        @Override
        protected void convert(@NonNull BaseViewHolder holder, ViewHierarchy vh) {
            int color = vh.isVisible() ? vh.isTarget ? getContext().getResources().getColor(R.color.pd_blue) : 0xff000000 : 0xff959595;
            TextView textView = holder.getView(R.id.view_name_title);

            holder.setText(R.id.view_name_title, vh.viewToTitleString(vh.view))
                    .setTextColor(R.id.view_name_title, color)
                    .setText(R.id.view_name_subtitle, vh.viewToSummaryString(vh.view))
                    .setTextColor(R.id.view_name_subtitle, color);

            TreeNodeLayout layout = holder.getView(R.id.view_name_wrapper);
            layout.setLayerCount(vh.layerCount, vh.sysLayerCount);
            if (vh.isGroup() && vh.getChildCount() > 0) {

                textView.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(vh.isExpand ? R.drawable.pd_expand : R.drawable.pd_collapse), null, null, null);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(holder.getAdapterPosition(),vh);
                    }
//                    if (vh.isGroup() && vh.getChildCount() > 0) {
//
//                        ((TextView) itemView.findViewById(R.id.view_name_title)).setCompoundDrawablesWithIntrinsicBounds(
//                                ViewKnife.getDrawable(isExpand ? R.drawable.pd_expand : R.drawable.pd_collapse), null, null, null);
//                    }
                }
            });
        }

        public interface OnItemClickListener{
            void onItemClick(int position, ViewHierarchy vh);
        }

    }
}
