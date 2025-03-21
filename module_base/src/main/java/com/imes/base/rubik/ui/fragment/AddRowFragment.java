package com.imes.base.rubik.ui.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hjq.toast.ToastUtils;
import com.imes.base.BaseFragment;
import com.imes.base.database.Column;
import com.imes.base.database.DatabaseResult;
import com.imes.base.rubik.Rubik;
import com.imes.base.rubik.model.BaseItem;
import com.imes.base.rubik.model.KeyEditItem;
import com.imes.base.rubik.model.KeyValueItem;
import com.imes.base.rubik.model.NameItem;
import com.imes.base.rubik.model.TitleItem;
import com.imes.base.rubik.views.ExtraEditTextView;
import com.imes.base.utils.SimpleTask;
import com.imes.base.utils.Utils;
import com.imes.base.utils.ViewKnife;
import com.imes.module_base.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddRowFragment extends BaseFragment {
    private int key;
    private String table;
    private RecyclerView recyclerView;
    private AddAdapter adapter;

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
        key = getArguments().getInt(PARAM1);
        table = getArguments().getString(PARAM2);
        getArguments().remove(PARAM3);
        adapter = new AddAdapter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("添加");
        getToolbar().getMenu().add(-1, -1, 0, R.string.pd_name_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<BaseItem> datas = adapter.getData();
                if (Utils.isNotEmpty(datas)) {
                    ContentValues values = new ContentValues();
                    for (int i = 0; i < datas.size(); i++) {
                        if (datas.get(i) instanceof KeyEditItem) {
                            if (!((KeyEditItem)datas.get(i)).editable) {
                                continue;
                            }
                            String[] data = ((KeyEditItem)datas.get(i)).data;
//                            if (((KeyEditItem)datas.get(i)).isNotNull && data[1] == null) {
//                                Utils.toast("failed, [" + data[0] + "] need valid value");
//                                return true;
//                            }
                            values.put(data[0], data[1]);
                        }
                    }
                    if (values.size() > 0) {
                        insert(values);
                    }
                }
                return true;
            }
        });
        recyclerView.setAdapter(adapter);
    }



    @Override
    protected int getLayoutId() {
        return 0;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeSoftInput();
    }

    @Override
    protected void onViewEnterAnimEnd(View container) {
        loadData();
    }

    private void loadData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
            @Override
            public DatabaseResult doInBackground(Void[] params) {
                return Rubik.get().getDatabases().getTableInfo(key, table);
            }

            @Override
            public void onPostExecute(DatabaseResult result) {
                List<BaseItem> data = new ArrayList<>();
                if (result.sqlError == null) {
                    data.add(new TitleItem(String.format(Locale.getDefault(),
                            "%d COLUMNS", result.values.size())));
                    data.add(new KeyValueItem(new String[]{"KEY", "VALUE"}, true));
                    Map<String, Integer> keyMapIndex = new HashMap<>();
                    for (int i = 0; i < result.columnNames.size(); i++) {
                        if (TextUtils.equals(result.columnNames.get(i), Column.NAME)) {
                            keyMapIndex.put(Column.NAME, i);
                        } else if (TextUtils.equals(result.columnNames.get(i), Column.TYPE)) {
                            keyMapIndex.put(Column.TYPE, i);
                        } else if (TextUtils.equals(result.columnNames.get(i), Column.NOT_NULL)) {
                            keyMapIndex.put(Column.NOT_NULL, i);
                        } else if (TextUtils.equals(result.columnNames.get(i), Column.DEF_VALUE)) {
                            keyMapIndex.put(Column.DEF_VALUE, i);
                        } else if (TextUtils.equals(result.columnNames.get(i), Column.PK)) {
                            keyMapIndex.put(Column.PK, i);
                        }
                    }
                    for (int i = 0; i < result.values.size(); i++) {
                        boolean isPrimaryKey = result.values.get(i).get(keyMapIndex.get(Column.PK)).equals("1");
                        boolean isNotNull = (result.values.get(i).get(keyMapIndex.get(Column.NOT_NULL)).equals("1"));
                        String typeName = result.values.get(i).get(keyMapIndex.get(Column.TYPE));
                        boolean isInteger = "INTEGER".equalsIgnoreCase(typeName);
                        data.add(new KeyEditItem(
                                isPrimaryKey && isInteger,
                                new String[]{
                                        result.values.get(i).get(keyMapIndex.get(Column.NAME)) + (isPrimaryKey ? "  (primaryKey)" : ""),
                                        (isPrimaryKey && isInteger) ? "AUTO"
                                                : result.values.get(i).get(keyMapIndex.get(Column.DEF_VALUE))
                                },
                                typeName + (isNotNull ? "" : "  (optional)")
                        ));
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

    private void insert(ContentValues values) {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
            @Override
            public DatabaseResult doInBackground(Void[] params) {
                return Rubik.get().getDatabases().insert(key, table, values);
            }

            @Override
            public void onPostExecute(DatabaseResult result) {
                hideLoading();
                if (result.sqlError == null) {
                    ToastUtils.show(R.string.pd_success);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                } else {
                    ToastUtils.show(result.sqlError.message);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                }
            }
        }).execute();
    }

    static class AddAdapter extends BaseMultiItemQuickAdapter<BaseItem, BaseViewHolder>{
        private final static int COMMON = 1;
        private final static int TITLE = 0;
        public AddAdapter() {
            addItemType(TITLE,R.layout.pd_item_title);
            addItemType(COMMON,R.layout.pd_item_key_value);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, BaseItem item) {
            switch (baseViewHolder.getItemViewType()) {
                case TITLE:
                    baseViewHolder.setText(R.id.item_title_id, item.data+"");
                    break;
                case COMMON:
                    if (item instanceof KeyEditItem) {
                        ((ExtraEditTextView)baseViewHolder.getView(R.id.item_edit)).clearTextChangedListeners();
                        ((EditText)baseViewHolder.getView(R.id.item_edit)).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (((KeyEditItem)item).data != null && ((KeyEditItem)item).data.length >= 2) {
                                    ((KeyEditItem)item).data[1] = s.toString();
                                }
                            }
                        });
                        ((EditText)baseViewHolder.getView(R.id.item_edit)).setHint(((KeyEditItem)item).hint);
                        baseViewHolder.setText(R.id.item_key, ((KeyEditItem)item).data[0])
                                .setText(R.id.item_edit, ((KeyEditItem)item).data[1])
                                .setGone(R.id.item_value,true)
                        .setVisible(R.id.item_edit,true)
                        .setEnabled(R.id.item_edit,((KeyEditItem)item).editable);
                        if (((KeyEditItem)item).editable) {
                            ((EditText)baseViewHolder.getView(R.id.item_edit)).setSingleLine(true);
                        } else {
                            ((EditText)baseViewHolder.getView(R.id.item_edit)).setSingleLine(false);
                        }
                    }else if (item instanceof KeyValueItem){
                        baseViewHolder.setText(R.id.item_prefix,((KeyValueItem)item).getPrefix())
                                .setGone(R.id.item_prefix,TextUtils.isEmpty(((KeyValueItem)item).getPrefix()))
                                .setText(R.id.item_key,((KeyValueItem)item).data[0])
                                .setBackgroundColor(R.id.item_value,((KeyValueItem)item).isTitle? ViewKnife.getColor(R.color.pd_item_key) : Color.WHITE)
                                .setText(R.id.item_value,((KeyValueItem)item).data[1])
                                .setVisible(R.id.item_value,true)
                                .setGone(R.id.item_edit,true)
                                .setVisible(R.id.item_arrow,((KeyValueItem)item).clickable);

                    }
                    break;
                default:
                    break;
            }
        }
    }
}
