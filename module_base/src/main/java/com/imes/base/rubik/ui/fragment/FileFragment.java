package com.imes.base.rubik.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imes.base.BaseFragment;
import com.imes.base.BaseListFragment;
import com.imes.base.rubik.model.BaseItem;
import com.imes.base.rubik.model.DBItem;
import com.imes.base.rubik.model.FileItem;
import com.imes.base.rubik.model.SPItem;
import com.imes.base.rubik.model.TitleItem;
import com.imes.base.rubik.views.GeneralDialog;
import com.imes.base.sandbox.Sandbox;
import com.imes.base.utils.FileUtil;
import com.imes.base.utils.Utils;
import com.imes.module_base.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileFragment extends BaseListFragment {
    private FileAdapter adapter;
    private File file;
    @Override
    public void initData(Bundle state) {

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new FileAdapter();
        getRecyclerView().setAdapter(adapter);
        file = (File) getArguments().getSerializable(PARAM1);
        getToolbar().setTitle(file.getName());
        getToolbar().getMenu().add(0,0,0,R.string.pd_name_delete_key)
                .setIcon(R.drawable.pd_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    GeneralDialog.build(CODE2)
                            .title(R.string.pd_help_title)
                            .message(R.string.pd_make_sure, true)
                            .positiveButton(R.string.pd_ok)
                            .negativeButton(R.string.pd_cancel)
                            .show(FileFragment.this);
                }
                return true;
            }
        });
        refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODE1) {
                refresh();
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
            } else if (requestCode == CODE2) {
                FileUtil.deleteDirectory(file);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                onBackPressed();
            }
        }
    }

    private void refresh() {
        List<File> files = Sandbox.getFiles(file);
        if (Utils.isNotEmpty(files)) {
            List<BaseItem> data = new ArrayList<>();
            data.add(new TitleItem(String.format(Locale.getDefault(), "%d FILES", files.size())));
            for (int i = 0; i < files.size(); i++) {
                data.add(new FileItem(files.get(i)));
            }
            adapter.setList(data);
            adapter.notifyDataSetChanged();
            adapter.setListener(new FileAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, BaseItem item) {
                    Bundle bundle = new Bundle();
                    if (item instanceof FileItem) {
                        bundle.putSerializable(PARAM1, (File) item.data);
                        if (((File) item.data).isDirectory()) {
                            launch(FileFragment.class, bundle, CODE1);
                        } else {
                            launch(FileAttrFragment.class, bundle, CODE1);
                        }
                    }
                }
            });
        } else {
            showError(null);
        }
    }
    @Override
    protected int getLayoutId() {
        return 0;
    }

    static class FileAdapter extends BaseMultiItemQuickAdapter<BaseItem, BaseViewHolder> {
        private final static int COMMON = 1;
        private final static int TITLE = 0;
        private OnItemClickListener listener;

        public FileAdapter() {

            addItemType(TITLE, R.layout.pd_item_title);
            addItemType(COMMON,R.layout.pd_item_common);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, BaseItem item) {
            switch (baseViewHolder.getItemViewType()){
                case TITLE:
                    baseViewHolder.setText(R.id.item_title_id,item.data+"");
                    break;
                case COMMON:
                    if (item instanceof DBItem){
                        baseViewHolder
                                .setVisible(R.id.common_item_arrow,false)
                                .setVisible(R.id.common_item_info, false)
                                .setText(R.id.common_item_title,((DBItem) item).data);

                    }else if (item instanceof SPItem){
                        baseViewHolder
                                .setVisible(R.id.common_item_arrow,false)
                                .setVisible(R.id.common_item_info, false)
                                .setText(R.id.common_item_title,((SPItem) item).data);
                    }else if (item instanceof FileItem){
                        baseViewHolder
                                .setText(R.id.common_item_arrow,"")
                                .setText(R.id.common_item_info, ((FileItem) item).getInfo())
                                .setText(R.id.common_item_title,((FileItem) item).getFileName());
                    }
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
