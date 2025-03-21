package com.imes.base.rubik.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imes.base.BaseFragment;
import com.imes.base.rubik.Rubik;
import com.imes.base.rubik.model.BaseItem;
import com.imes.base.rubik.model.DBItem;
import com.imes.base.rubik.model.FileItem;
import com.imes.base.rubik.model.SPItem;
import com.imes.base.rubik.model.TitleItem;
import com.imes.base.sandbox.Sandbox;
import com.imes.base.utils.Config;
import com.imes.base.utils.SimpleTask;
import com.imes.module_base.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SandboxFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private SandboxAdapter mAdapter;

    @Override
    public void initData(Bundle state) {
        getToolbar().setTitle("sandbox");
        loadData();
    }

    private void loadData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<BaseItem>>() {
            @Override
            public List<BaseItem> doInBackground(Void[] params) {
                SparseArray<String> databaseNames = null;
                try {
                    databaseNames = Rubik.get().getDatabases().getDatabaseNames();
                } catch (Exception e) {

                }
                List<BaseItem> data = new ArrayList<>();
                data.add(new TitleItem(getString(R.string.pd_name_database)));
                if (databaseNames != null) {
                    for (int i = 0; i < databaseNames.size(); i++) {
                        data.add(new DBItem(databaseNames.valueAt(i), databaseNames.keyAt(i)));
                    }
                }
                data.add(new TitleItem(getString(R.string.pd_name_sp)));
                try {
                    List<File> spFiles = Rubik.get().getSharedPref().getSharedPrefDescs();
                    for (int i = 0; i < spFiles.size(); i++) {
                        data.add(new SPItem(spFiles.get(i).getName(), spFiles.get(i)));
                    }
                } catch (Exception e) {

                }


                data.add(new TitleItem(getString(R.string.pd_name_file)));
                try {
                    List<File> descriptors = Sandbox.getRootFiles();
                    for (int i = 0; i < descriptors.size(); i++) {
                        data.add(new FileItem(descriptors.get(i)));
                    }
                } catch (Exception e) {

                }


                if (Config.getSANDBOX_DPM() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    data.add(new TitleItem("Device-protect-mode Files"));
                    List<File> dpm = Sandbox.getDPMFiles();
                    for (int i = 0; i < dpm.size(); i++) {
                        data.add(new FileItem(dpm.get(i)));
                    }
                }

                return data;
            }

            @Override
            public void onPostExecute(List<BaseItem> result) {
                hideLoading();
                if (result != null) {
                    mAdapter = new SandboxAdapter();
                    mAdapter.setList(result);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.setListener(new SandboxAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, BaseItem item) {
                            Bundle bundle = new Bundle();
                            if (item instanceof DBItem) {
                                bundle.putInt(PARAM1, ((DBItem) item).key);
                                launch(DBFragment.class, (String) item.data, bundle);
                            } else if (item instanceof SPItem) {
                                bundle.putSerializable(PARAM1, ((SPItem) item).descriptor);
                                launch(SPFragment.class, bundle);
                            } else if (item instanceof FileItem) {
                                bundle.putSerializable(PARAM1, (File) item.data);
                                if (((File) item.data).isDirectory()) {
                                    launch(FileFragment.class, bundle, CODE1);
                                } else {
                                    launch(FileAttrFragment.class, bundle);
                                }
                            }
                        }
                    });
                }
            }
        }).execute();
    }

    @Override
    protected View getLayoutView() {
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

    @Override
    protected int getLayoutId() {
        return 0;
    }


    static class SandboxAdapter extends BaseMultiItemQuickAdapter<BaseItem,BaseViewHolder> {
        private final static int COMMON = 1;
        private final static int TITLE = 0;
        private OnItemClickListener listener;

        public SandboxAdapter() {

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
