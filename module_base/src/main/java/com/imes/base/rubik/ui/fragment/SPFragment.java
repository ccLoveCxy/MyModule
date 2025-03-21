package com.imes.base.rubik.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
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
import com.imes.base.BaseFragment;
import com.imes.base.rubik.Rubik;
import com.imes.base.rubik.model.BaseItem;
import com.imes.base.rubik.model.KeyEditItem;
import com.imes.base.rubik.model.KeyValueItem;
import com.imes.base.rubik.model.TitleItem;
import com.imes.base.rubik.ui.item.GridItem;
import com.imes.base.rubik.views.ExtraEditTextView;
import com.imes.base.rubik.views.GeneralDialog;
import com.imes.base.utils.SimpleTask;
import com.imes.base.utils.Utils;
import com.imes.base.utils.ViewKnife;
import com.imes.module_base.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SPFragment extends BaseFragment {
    private File descriptor;
    private String clickKey;
    private SPAdapter adapter;
    private RecyclerView recyclerView;
    private int pos;

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
        adapter = new SPAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setListener(new SPAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof KeyValueItem) {
                    if (((KeyValueItem) item).isTitle) {
                        return;
                    }
                    clickKey = ((KeyValueItem) item).data[0];
                    Bundle bundle = new Bundle();
                    bundle.putString(PARAM1, ((KeyValueItem) item).data[1]);
                    launch(EditFragment.class, bundle, CODE1);
                }
            }

            @Override
            public boolean onLongItemClick(int position, BaseItem item) {
                if (item instanceof KeyValueItem) {
                    if (((KeyValueItem) item).clickable) {
                        pos = position;
                        // make sure that only the content can be response for menuEvent
                        return false;
                    }
                }
                return false;
            }
        });
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        descriptor = (File) getArguments().getSerializable(PARAM1);
        getToolbar().setTitle(descriptor.getName());
        getToolbar().getMenu().add(0, 0, 0, R.string.pd_name_help).setIcon(R.drawable.pd_help)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    GeneralDialog.build(-1)
                            .title(R.string.pd_help_title)
                            .message(R.string.pd_help_sp)
                            .positiveButton(R.string.pd_ok)
                            .show(SPFragment.this);
                }
                return false;
            }
        });
        registerForContextMenu(recyclerView);
        loadData();
    }

    @Override
    public void initData(Bundle state) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (adapter.getItem(pos) instanceof KeyValueItem) {
            if (!((KeyValueItem) adapter.getItem(pos)).isTitle) {
                menu.add(-1, 0, 0, R.string.pd_name_copy_value);
                menu.add(-1, 0, 1, R.string.pd_name_delete_key);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
            BaseItem baseItem = adapter.getItem(pos);

            if (baseItem instanceof KeyValueItem) {
                KeyValueItem keyValueItem = (KeyValueItem) baseItem;

                if (keyValueItem.isTitle) {
                    return true;
                }

                if (item.getOrder() == 0) {
                    Utils.copy2ClipBoard(
                            "KEY :: " + keyValueItem.data[0] + "\nVALUE  :: " + keyValueItem.data[1]
                    );
                    return true;
                } else if (item.getOrder() == 1) {
                    String clickedKey = keyValueItem.data[0];
                    Rubik.get().getSharedPref().removeSharedPrefKey(descriptor, clickedKey);
                    loadData();
                    return true;
                }
            }
        return super.onContextItemSelected(item);
    }

    private void loadData() {
        Map<String, String> contents = Rubik.get().getSharedPref().getSharedPrefContent(descriptor);
        if (contents != null && !contents.isEmpty()) {
            List<BaseItem> data = new ArrayList<>();
            data.add(new TitleItem(String.format(Locale.getDefault(), "%d ITEMS", contents.size())));
            data.add(new KeyValueItem(new String[]{"KEY", "VALUE"}, true));
            for (Map.Entry<String, String> entry : contents.entrySet()) {
                data.add(new KeyValueItem(new String[]{entry.getKey(), entry.getValue()}, false, true));
            }
            adapter.setList(data);
            adapter.notifyDataSetChanged();
        } else {
            showError(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE1 && resultCode == Activity.RESULT_OK) {
            final String value = data.getStringExtra("value");
            if (!TextUtils.isEmpty(clickKey)) {

                new SimpleTask<>(new SimpleTask.Callback<Void, String>() {
                    @Override
                    public String doInBackground(Void[] params) {
                        return Rubik.get().getSharedPref().updateSharedPref(descriptor, clickKey, value);
                    }

                    @Override
                    public void onPostExecute(String result) {
                        hideLoading();
                        if (TextUtils.isEmpty(result)) {
                            Utils.toast(R.string.pd_success);
                        } else {
                            Utils.toast(result);
                        }
                        loadData();
                    }
                }).execute();
                showLoading();
            }
        }
    }

    static class SPAdapter extends BaseMultiItemQuickAdapter<BaseItem, BaseViewHolder> {
        private final static int COMMON = 1;
        private final static int TITLE = 0;
        private OnItemClickListener listener;

        public SPAdapter() {
            addItemType(TITLE, R.layout.pd_item_title);
            addItemType(COMMON, R.layout.pd_item_key_value);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, BaseItem item) {
            switch (baseViewHolder.getItemViewType()) {
                case TITLE:
                    baseViewHolder.setText(R.id.item_title_id, item.data + "");
                    break;
                case COMMON:
                    if (item instanceof KeyEditItem) {
                        ((ExtraEditTextView) baseViewHolder.getView(R.id.item_edit)).clearTextChangedListeners();
                        ((EditText) baseViewHolder.getView(R.id.item_edit)).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (((KeyEditItem) item).data != null && ((KeyEditItem) item).data.length >= 2) {
                                    ((KeyEditItem) item).data[1] = s.toString();
                                }
                            }
                        });
                        ((EditText) baseViewHolder.getView(R.id.item_edit)).setHint(((KeyEditItem) item).hint);
                        baseViewHolder.setText(R.id.item_key, ((KeyEditItem) item).data[0])
                                .setText(R.id.item_edit, ((KeyEditItem) item).data[1])
                                .setGone(R.id.item_value, true)
                                .setVisible(R.id.item_edit, true)
                                .setEnabled(R.id.item_edit, ((KeyEditItem) item).editable);
                        if (((KeyEditItem) item).editable) {
                            ((EditText) baseViewHolder.getView(R.id.item_edit)).setSingleLine(true);
                        } else {
                            ((EditText) baseViewHolder.getView(R.id.item_edit)).setSingleLine(false);
                        }
                    } else if (item instanceof KeyValueItem) {
                        baseViewHolder.setText(R.id.item_prefix, ((KeyValueItem) item).getPrefix())
                                .setGone(R.id.item_prefix, TextUtils.isEmpty(((KeyValueItem) item).getPrefix()))
                                .setText(R.id.item_key, ((KeyValueItem) item).data[0])
                                .setBackgroundColor(R.id.item_value, ((KeyValueItem) item).isTitle ? ViewKnife.getColor(R.color.pd_item_key) : Color.WHITE)
                                .setText(R.id.item_value, ((KeyValueItem) item).data[1])
                                .setVisible(R.id.item_value, true)
                                .setGone(R.id.item_edit, true)
                                .setVisible(R.id.item_arrow, ((KeyValueItem) item).clickable);

                    }
                    break;
                default:
                    break;
            }
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
