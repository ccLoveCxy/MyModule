package com.imes.base.rubik.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imes.base.BaseFragment;
import com.imes.base.rubik.Rubik;
import com.imes.base.rubik.cache.Content;
import com.imes.base.rubik.cache.Summary;
import com.imes.base.rubik.network.NetStateListener;
import com.imes.base.rubik.ui.connector.SimpleOnActionExpandListener;
import com.imes.base.rubik.ui.connector.SimpleOnQueryTextListener;
import com.imes.base.utils.Config;
import com.imes.base.utils.SimpleTask;
import com.imes.base.utils.Utils;
import com.imes.module_base.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author : quintus
 * date : 2021/11/17 16:16
 * description :
 */
public class NetFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener, NetStateListener {
    private RecyclerView recyclerView;
    private NetListAdapter mAdapter;
    private List<Summary> originData = new ArrayList<>();
    private List<Summary> tmpFilter = new ArrayList<>();

    @Override
    public void initData(Bundle state) {
        getToolbar().setTitle("network");
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_2, 0, "search")
                .setActionView(new SearchView(getContext()))
                .setIcon(R.drawable.pd_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_3, 1,"clear");
        setSearchView();
        getToolbar().setOnMenuItemClickListener(this);
        Rubik.get().getInterceptor().setListener(this);
        mAdapter.setListener(new NetListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Summary item) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(PARAM1, item.id);
                    launch(NetSummaryFragment.class, bundle);
            }
        });
        loadData();
    }
    private void loadData() {
        hideError();
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<Summary>>() {
            @Override
            public List<Summary> doInBackground(Void[] params) {
                return Summary.queryList();
            }

            @Override
            public void onPostExecute(List<Summary> result) {
                hideLoading();
                if (Utils.isNotEmpty(result)) {
                    mAdapter.setList(result);

                    originData.clear();
                    originData.addAll(mAdapter.getData());
                } else {
                    showError(null);
                }
            }
        }).execute();
    }
    private void setSearchView() {
        MenuItem menuItem = getToolbar().getMenu().findItem(R.id.pd_menu_id_2);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        searchView.setQueryHint("query url");
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                closeSoftInput();
                filter(query);
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(menuItem, new SimpleOnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                loadData();
                return true;
            }
        });
    }

    private void filter(String condition) {
        tmpFilter.clear();
        if (TextUtils.isEmpty(condition)) {
            loadData();
            return;
        }
        if (Utils.isNotEmpty(originData)) {
            for (int i = originData.size() - 1; i >= 0; i--) {
                String url = originData.get(i).url;
                if (url.contains(condition)) {
                    tmpFilter.add(originData.get(i));
                }
            }
            mAdapter.setList(tmpFilter);
        }
    }
    
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        mAdapter = new NetListAdapter();
        recyclerView = new RecyclerView(getContext());
        recyclerView.setBackgroundColor(getResources().getColor(R.color.pd_main_bg));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        GradientDrawable horizontalDrawable = new GradientDrawable();
        horizontalDrawable.setColor(0xffE5E5E5);
        horizontalDrawable.setSize(0, 1);
        divider.setDrawable(horizontalDrawable);
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(mAdapter);
        return recyclerView;
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.pd_menu_id_3) {
            if (!Config.isNetLogEnable()) {
                return false;
            }
            clearData();
        }
        closeSoftInput();
        return true;
    }
    private void clearData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, Void>() {
            @Override
            public Void doInBackground(Void[] params) {
                Summary.clear();
                Content.clear();
                return null;
            }

            @Override
            public void onPostExecute(Void result) {
                mAdapter.setList(null);
                hideLoading();
                showError(null);
            }
        }).execute();
    }

    @Override
    public void onRequestStart(long id) {
        refreshSingleData(true, id);
    }

    @Override
    public void onRequestEnd(long id) {
        refreshSingleData(false, id);
    }

    private void refreshSingleData(final boolean isNew, final long id) {
        new SimpleTask<>(new SimpleTask.Callback<Void, Summary>() {
            @Override
            public Summary doInBackground(Void[] params) {
                return Summary.query(id);
            }

            @Override
            public void onPostExecute(Summary result) {
                hideLoading();
                if (result == null) {
                    return;
                }
                if (!isNew) {
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                            if ( mAdapter.getData().get(i).id == result.id) {
                                mAdapter.getData().set(i,result);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    } else {
                    mAdapter.addData(0,result);
                }
                originData.clear();
                originData.addAll(mAdapter.getData());
            }
        }).execute();
    }
    
    static class NetListAdapter extends BaseQuickAdapter<Summary, BaseViewHolder>{
        private OnItemClickListener listener;
        public NetListAdapter() {
            super(R.layout.pd_item_net);
        }

        public void setListener(OnItemClickListener listener){
            this.listener  = listener;
        }
        @Override
        protected void convert(@NonNull BaseViewHolder holder, Summary data) {
            int position = getItemPosition(data);
            if (position % 2 == 0) {
                holder.itemView.setBackgroundResource(R.color.pd_item_bg);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }

            boolean done = data.status != 0;
            holder.setImageResource(R.id.item_net_status, !done ? R.drawable.pd_transform :
                    (data.status == 1 ? R.drawable.pd_error : R.drawable.pd_done));

            holder.setTextColor(R.id.item_net_url, done && data.code > 0 && data.code != 200 ? Color.BLUE : Color.BLACK);
            holder.setText(R.id.item_net_url, data.url)
                    .setText(R.id.item_net_host, data.host)
                    .setText(R.id.item_net_info,
                            String.format(Locale.getDefault(), "%s    %s    %s%s%s",
                                    Utils.millis2String(data.start_time, Utils.HHMMSS),
                                    data.method,
                                    done && data.code > 0 ? String.valueOf(data.code) + "    " : "",
                                    (done && data.response_size > 0)
                                            ? Utils.formatSize(data.response_size) + "    " : "",
                                    done && data.end_time > 0 && data.start_time > 0
                                            ? String.valueOf(data.end_time - data.start_time) + "ms" : ""));

            TextView tv = holder.getView(R.id.item_net_url);
            if (done) {
                tv.setCompoundDrawables(isImage(data.response_content_type) ? ContextCompat.getDrawable(getContext(),R.drawable.pd_image) : null,null,null,null);
            } else {
                tv.setCompoundDrawables(null, null,null,null);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(holder.getAdapterPosition(),data);
                    }
                }
            });
        }
        private boolean isImage(String contentType) {
            return !TextUtils.isEmpty(contentType) && contentType.contains("image");
        }

        interface OnItemClickListener {
            void onItemClick(int position, Summary summary);
        } 
    }
}
