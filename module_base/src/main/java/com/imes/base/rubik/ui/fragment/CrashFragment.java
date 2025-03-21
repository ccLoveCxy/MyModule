package com.imes.base.rubik.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imes.base.BaseFragment;
import com.imes.base.rubik.cache.Crash;
import com.imes.base.rubik.views.GeneralDialog;
import com.imes.base.utils.SimpleTask;
import com.imes.base.utils.Utils;
import com.imes.module_base.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.imes.base.rubik.cache.Crash.TYPE_CONTENT;

/**
 * author : quintus
 * date : 2021/12/6 11:12
 * description :
 */
public class CrashFragment extends BaseFragment {
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private CrashListAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    public void initData(Bundle state) {
        getToolbar().setTitle("Crash");
        getToolbar().getMenu().add(-1, 0, 0, "删除").setIcon(R.drawable.pd_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                GeneralDialog.build(CODE1)
                        .title(getString(R.string.pd_help_title))
                        .message(getString(R.string.pd_make_sure), true)
                        .positiveButton(getString(R.string.pd_ok))
                        .negativeButton(getString(R.string.pd_cancel))
                        .show(CrashFragment.this);
                return true;
            }
        });
        mAdapter.setListener((position, data) -> {
            if (data.viewType == TYPE_CONTENT){
                Bundle bundle = new Bundle();
                bundle.putSerializable(PARAM1, data);
                launch(CrashStackFragment.class, bundle);
            }
        });
        loadData();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        mAdapter = new CrashListAdapter();
        recyclerView = new RecyclerView(getActivity());
        recyclerView.setBackgroundColor(getResources().getColor(R.color.pd_main_bg));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(0xffE5E5E5);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(mAdapter);
        return recyclerView;
    }

    private void loadData() {
        hideError();
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<Crash>>() {
            @Override
            public List<Crash> doInBackground(Void[] params) {
                return Crash.query();
            }

            @Override
            public void onPostExecute(List<Crash> result) {
                hideLoading();
                List<Crash> data = new ArrayList<>(result.size());
                if (Utils.isNotEmpty(result)) {
                    String title = null;
                    for (Crash crash : result) {
                        String tmp = Utils.millis2String(crash.createTime, FORMAT);
                        if (!TextUtils.equals(title, tmp)) {
                            Crash c = new Crash(Crash.TYPE_TITLE);
                            c.createTime = crash.createTime;
                            data.add(c);
                            title = tmp;
                        }
                        data.add(crash);
                    }
                    mAdapter.setList(data);
                } else {
                    showError(null);
                }
            }
        }).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            Crash.clear();
            mAdapter.setList(null);
            Utils.toast(R.string.pd_success);
        }
    }

    static class CrashListAdapter extends BaseMultiItemQuickAdapter<Crash, BaseViewHolder> {
        private OnItemClickListener listener;
        public CrashListAdapter() {
            addItemType(Crash.TYPE_TITLE, R.layout.pd_item_title);
            addItemType(TYPE_CONTENT,R.layout.pd_item_common);
        }

        public void setListener(OnItemClickListener listener){
            this.listener = listener;
        }
        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, Crash data) {
            switch (baseViewHolder.getItemViewType()){
                case Crash.TYPE_TITLE:
                    baseViewHolder.setText(R.id.item_title_id, Utils.millis2String(data.createTime, FORMAT));
                    break;
                case TYPE_CONTENT:
                    baseViewHolder
                            .setVisible(R.id.common_item_arrow,true)
                            .setText(R.id.common_item_info, TextUtils.isEmpty(data.cause) ? data.type : data.cause)
                            .setText(R.id.common_item_title, Utils.millis2String(data.createTime, Utils.HHMMSS));
                    break;
            }
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(baseViewHolder.getAdapterPosition(),data);
                    }
                }
            });

        }

        public interface OnItemClickListener{
            void onItemClick(int position, Crash crash);
        }
    }
}
