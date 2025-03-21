package com.imes.base.rubik.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imes.base.BaseFragment;
import com.imes.base.database.DatabaseResult;
import com.imes.base.rubik.Rubik;
import com.imes.base.rubik.model.BaseItem;
import com.imes.base.rubik.model.NameItem;
import com.imes.base.rubik.recyclerview.GridDividerDecoration;
import com.imes.base.rubik.ui.connector.SimpleOnActionExpandListener;
import com.imes.base.rubik.ui.connector.SimpleOnQueryTextListener;
import com.imes.base.rubik.ui.item.GridItem;
import com.imes.base.rubik.views.GeneralDialog;
import com.imes.base.utils.SimpleTask;
import com.imes.base.utils.Utils;
import com.imes.base.utils.ViewKnife;
import com.imes.module_base.R;

import java.util.ArrayList;
import java.util.List;

public class TableFragment extends BaseFragment {
    private int key;
    private boolean mode;
    private String table;
    private String primaryKey;
    private TableAdapter adapter;
    private GridItem clickedItem;
    private String realTimeQueryCondition;
    private RecyclerView recyclerView;
    public int position;

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
    protected void onViewEnterAnimEnd(View container) {
        loadData(null);
    }

    @Override
    public void initData(Bundle state) {
        key = getArguments().getInt(PARAM1);
        table = getArguments().getString(PARAM2);
        mode = getArguments().getBoolean(PARAM3);
        primaryKey = Rubik.get().getDatabases().getPrimaryKey(key, table);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!mode) {
            initMenu();
        }
        adapter = new TableAdapter();
        registerForContextMenu(recyclerView);
        recyclerView.addItemDecoration(new GridDividerDecoration.Builder()
                .setColor(ViewKnife.getColor(R.color.pd_divider_light))
                .setThickness(ViewKnife.dip2px(1f))
                .build());
        recyclerView.setAdapter(adapter);
        adapter.setListener(new TableAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof GridItem) {
                    if (mode) {
                        return;
                    }
                    if (!((GridItem) item).isEnable()) {
                        return;
                    }
                    clickedItem = (GridItem) item;
                    Bundle bundle = new Bundle();
                    bundle.putString(PARAM1, ((GridItem) item).data);
                    launch(EditFragment.class, bundle, CODE1);
                }
            }

            @Override
            public boolean onLongItemClick(int pos, BaseItem item) {
                if (item instanceof GridItem) {
                    if (!((GridItem) item).isEnable()) {
                        position = pos;
                        // make sure that only the content can be response for menuEvent
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initMenu() {
        getToolbar().getMenu().add(0, 0, 0, R.string.pd_name_help).setIcon(R.drawable.pd_help)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem searchItem = getToolbar().getMenu().add(0, 0, 1, R.string.pd_name_search);
        searchItem.setActionView(new SearchView(getContext()))
                .setIcon(R.drawable.pd_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        getToolbar().getMenu().add(0, 0, 2, R.string.pd_name_info);
        getToolbar().getMenu().add(0, 0, 3, R.string.pd_name_add);
        getToolbar().getMenu().add(0, 0, 4, R.string.pd_name_delete_all);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(ViewKnife.getString(R.string.pd_search_hint));
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                closeSoftInput();
                realTimeQueryCondition = query;
                loadData(query);
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(searchItem, new SimpleOnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (!TextUtils.isEmpty(realTimeQueryCondition)) {
                    realTimeQueryCondition = null;
                    loadData(null);
                }
                return true;
            }
        });
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    GeneralDialog.build(-1)
                            .title(R.string.pd_help_title)
                            .message(R.string.pd_help_table)
                            .positiveButton(R.string.pd_ok)
                            .show(TableFragment.this);
                }
                if (item.getOrder() == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PARAM1, key);
                    bundle.putString(PARAM2, table);
                    bundle.putBoolean(PARAM3, true);
                    launch(TableFragment.class, bundle);
                } else if (item.getOrder() == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PARAM1, key);
                    bundle.putString(PARAM2, table);
                    launch(AddRowFragment.class, bundle, CODE2);
                } else if (item.getOrder() == 4) {
                    delete(null);
                }
                closeSoftInput();
                return true;
            }
        });
    }

    private void delete(final String pkValue) {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
            @Override
            public DatabaseResult doInBackground(Void[] params) {
                return Rubik.get().getDatabases().delete(
                        key,
                        table,
                        TextUtils.isEmpty(pkValue) ? null : primaryKey,
                        TextUtils.isEmpty(pkValue) ? null : pkValue
                );
            }

            @Override
            public void onPostExecute(DatabaseResult result) {
                hideLoading();
                if (result.sqlError != null) {
                    Utils.toast(result.sqlError.message);
                } else {
                    realTimeQueryCondition = null;
                    Utils.toast(R.string.pd_success);
                    loadData(null);
                }
            }
        }).execute();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(-1, R.id.pd_menu_id_1, 0, R.string.pd_name_copy_value);
        menu.add(-1, R.id.pd_menu_id_2, 1, R.string.pd_name_delete_row);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BaseItem gridItem = adapter.getItem(position);
        if (gridItem instanceof GridItem) {
            if (item.getItemId() == R.id.pd_menu_id_1) {
                Utils.copy2ClipBoard((String) gridItem.data);
                return true;
            } else if (item.getItemId() == R.id.pd_menu_id_2) {
                String pkValue = ((GridItem) gridItem).primaryKeyValue;
                delete(pkValue);
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            final String value = data.getStringExtra("value");
            showLoading();
            new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
                @Override
                public DatabaseResult doInBackground(Void[] params) {
                    return Rubik.get().getDatabases().update(
                            key,
                            table,
                            primaryKey,
                            clickedItem.primaryKeyValue,
                            clickedItem.columnName,
                            value
                    );
                }

                @Override
                public void onPostExecute(DatabaseResult result) {
                    hideLoading();
                    Utils.toast(result.sqlError != null ? R.string.pd_failed : R.string.pd_success);
                    loadData(realTimeQueryCondition);
                }
            }).execute();
        } else if (requestCode == CODE2 && resultCode == Activity.RESULT_OK) {
            loadData(realTimeQueryCondition);
        }
    }

    private void loadData(final String condition) {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
            @Override
            public DatabaseResult doInBackground(Void[] params) {
                if (mode) {
                    return Rubik.get().getDatabases().getTableInfo(key, table);
                } else {
                    return Rubik.get().getDatabases().query(key, table, condition);
                }
            }

            @Override
            public void onPostExecute(DatabaseResult result) {
                List<BaseItem> data = new ArrayList<>();
                if (result.sqlError == null) {
                    recyclerView.setLayoutManager(new GridLayoutManager(
                            getContext(), result.columnNames.size()));
                    int pkIndex = 0;
                    for (int i = 0; i < result.columnNames.size(); i++) {
                        data.add(new GridItem(result.columnNames.get(i), true));
                        if (TextUtils.equals(result.columnNames.get(i), primaryKey)) {
                            pkIndex = i;
                        }
                    }
                    for (int i = 0; i < result.values.size(); i++) {
                        for (int j = 0; j < result.values.get(i).size(); j++) {
                            GridItem item = new GridItem(result.values.get(i).get(j),
                                    result.values.get(i).get(pkIndex),
                                    result.columnNames.get(j));
                            if (!mode && pkIndex == j) {
                                item.setIsPrimaryKey();
                            }
                            data.add(item);
                        }
                    }
                    adapter.setList(data);
                    adapter.notifyDataSetChanged();
                } else {
                    Utils.toast(result.sqlError.message);
                }
                hideLoading();
            }
        }).execute();
    }

    static class TableAdapter extends BaseQuickAdapter<BaseItem, BaseViewHolder> {
        private OnItemClickListener listener;

        public TableAdapter() {
            super(R.layout.pd_item_table_cell);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, BaseItem item) {
            TextView textView = baseViewHolder.getView(R.id.gird_text);
            textView.setTypeface(null,TextUtils.isEmpty(item.data+"")? Typeface.ITALIC : Typeface.NORMAL);
            textView.setText(TextUtils.isEmpty(item.data+"")?"NULL":item.data+"");
            baseViewHolder.setBackgroundColor(R.id.gird_text, !((GridItem) item).isEnable()?ViewKnife.getColor(R.color.pd_item_key) : Color.WHITE);
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(baseViewHolder.getAdapterPosition(), item);
                    }
                }
            });

            baseViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        return listener.onLongItemClick(baseViewHolder.getAdapterPosition(), item);
                    }
                    return false;
                }
            });
        }


        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }


        public interface OnItemClickListener {
            void onItemClick(int position, BaseItem item);

            boolean onLongItemClick(int position, BaseItem item);
        }
    }
}
