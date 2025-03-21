package com.imes.base.rubik.ui.fragment;

import static com.imes.base.rubik.cache.Crash.TYPE_CONTENT;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imes.base.BaseFragment;
import com.imes.base.rubik.Rubik;
import com.imes.base.rubik.cache.Crash;
import com.imes.base.rubik.cache.Summary;
import com.imes.base.rubik.model.BaseItem;
import com.imes.base.rubik.model.DBItem;
import com.imes.base.rubik.model.FileItem;
import com.imes.base.rubik.model.NameItem;
import com.imes.base.rubik.model.SPItem;
import com.imes.base.rubik.model.TitleItem;
import com.imes.base.utils.Utils;
import com.imes.module_base.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DBFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private DBAdapter mAdapter;

    @Override
    protected View getLayoutView() {
        recyclerView = new RecyclerView(getContext());
        recyclerView.setBackgroundColor(getResources().getColor(R.color.pd_main_bg));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(0xffE5E5E5);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        recyclerView.addItemDecoration(divider);
        return recyclerView;
    }

    @Override
    public void initData(Bundle state) {
        final int key = getArguments().getInt(PARAM1);
        List<String> tables = Rubik.get().getDatabases().getTableNames(key);
        Collections.sort(tables);
        List<BaseItem> data = new ArrayList<>(tables.size());
        data.add(new TitleItem(String.format(Locale.getDefault(), "%d TABLES", tables.size())));
        for (int i = 0; i < tables.size(); i++) {
            data.add(new NameItem(tables.get(i)));
        }
        mAdapter = new DBAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new DBAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof NameItem) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PARAM1, key);
                    bundle.putString(PARAM2, ((NameItem) item).data);
                    launch(TableFragment.class, ((NameItem) item).data, bundle);
                }
            }
        });
        mAdapter.setList(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }


    static class DBAdapter extends BaseMultiItemQuickAdapter<BaseItem,BaseViewHolder>{
        private OnItemClickListener listener;
        private final static int COMMON = 1;
        private final static int TITLE = 0;
        public DBAdapter() {
            addItemType(TITLE, R.layout.pd_item_title);
            addItemType(COMMON,R.layout.pd_item_common);
        }
        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, BaseItem item) {
            switch (baseViewHolder.getItemViewType()) {
                case TITLE:
                    baseViewHolder.setText(R.id.item_title_id, item.data+"");
                    break;
                case COMMON:
                    baseViewHolder
                            .setVisible(R.id.common_item_arrow,false)
                            .setVisible(R.id.common_item_info, false)
                            .setText(R.id.common_item_title,((NameItem) item).data);
                    break;
                default:
                    break;
            }
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(baseViewHolder.getAdapterPosition(),item);
                    }
                }
            });
        }
        public void setListener(OnItemClickListener listener){
            this.listener = listener;
        }
        public interface OnItemClickListener{
            void onItemClick(int position, BaseItem item);
        }
    }
}
