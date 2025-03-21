package com.imes.base.rubik.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import com.imes.base.BaseFragment;
import com.imes.base.rubik.cache.Content;
import com.imes.base.rubik.cache.Summary;
import com.imes.base.rubik.model.KeyValueSummary;
import com.imes.base.rubik.recyclerview.KeyValueAdapter;
import com.imes.base.utils.FileUtil;
import com.imes.base.utils.FormatUtil;
import com.imes.base.utils.SimpleTask;
import com.imes.base.utils.Utils;
import com.imes.module_base.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.imes.base.rubik.model.KeyValueSummary.TYPE_CONTENT;
import static com.imes.base.rubik.model.KeyValueSummary.TYPE_EXCEPTION;
import static com.imes.base.rubik.model.KeyValueSummary.TYPE_TITLE;

/**
 * author : quintus
 * date : 2021/11/18 14:22
 * description :
 */
public class NetSummaryFragment extends BaseFragment {
    private Summary originData;
    private RecyclerView recyclerView;
    private KeyValueAdapter mAdapter;

    @Override
    public void initData(Bundle state) {
        final long id = getArguments().getLong(PARAM1);
        loadData(id);
        mAdapter.setListener(new KeyValueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, KeyValueSummary item) {
                Bundle bundle = new Bundle();
                if ("request body".equals(item.key)){
                    bundle.putBoolean(PARAM1, false);
                    bundle.putString(PARAM3, originData.request_content_type);
                    bundle.putLong(PARAM2, id);
                    launch(NetContentFragment.class, bundle);
                }else if ("response body".equals(item.key)){
                    if (!TextUtils.isEmpty(originData.response_content_type)
                            && originData.response_content_type.contains("image")) {
                        tryOpen(originData.id);
                        return;
                    }
                    bundle.putBoolean(PARAM1, true);
                    bundle.putString(PARAM3, originData.response_content_type);
                    bundle.putLong(PARAM2, id);
                    launch(NetContentFragment.class, bundle);
                } else {
                        String value = item.value;
                        if (!TextUtils.isEmpty(value)) {
                            Utils.copy2ClipBoard(value);
                        }
                    }
            }
        });
    }

    @Override
    protected View getLayoutView() {
        mAdapter = new KeyValueAdapter();
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

    private void loadData(final long id) {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, Summary>() {
            @Override
            public Summary doInBackground(Void[] params) {
                Summary summary =  Summary.query(id);
                summary.request_header = FormatUtil.parseHeaders(summary.requestHeader);
                summary.response_header = FormatUtil.parseHeaders(summary.responseHeader);
                return summary;
            }

            @Override
            public void onPostExecute(Summary summary) {
                hideLoading();
                if (summary == null) {
                    showError(null);
                    return;
                }
                originData = summary;
                getToolbar().setTitle(summary.url);
                getToolbar().setSubtitle(String.valueOf(summary.code == 0 ? "- -" : summary.code));

                List<KeyValueSummary> data = new ArrayList<>();

                if (summary.status == 1) {
                    Content content = Content.query(id);
                    data.add(new KeyValueSummary(TYPE_EXCEPTION,content.responseBody));
                }

                data.add(new KeyValueSummary(TYPE_TITLE,"GENERAL"));
                data.add(new KeyValueSummary(TYPE_CONTENT,"url", summary.url));
                data.add(new KeyValueSummary(TYPE_CONTENT,"host", summary.host));
                data.add(new KeyValueSummary(TYPE_CONTENT,"method", summary.method));
                data.add(new KeyValueSummary(TYPE_CONTENT,"protocol", summary.protocol));
                data.add(new KeyValueSummary(TYPE_CONTENT,"ssl", String.valueOf(summary.ssl)));
                data.add(new KeyValueSummary(TYPE_CONTENT,"start_time", Utils.millis2String(summary.start_time)));
                data.add(new KeyValueSummary(TYPE_CONTENT,"end_time", Utils.millis2String(summary.end_time)));
                data.add(new KeyValueSummary(TYPE_CONTENT,"req content-type", summary.request_content_type));
                data.add(new KeyValueSummary(TYPE_CONTENT,"res content-type", summary.response_content_type));
                data.add(new KeyValueSummary(TYPE_CONTENT,"request_size", Utils.formatSize(summary.request_size)));
                data.add(new KeyValueSummary(TYPE_CONTENT,"response_size", Utils.formatSize(summary.response_size)));

                if (!TextUtils.isEmpty(summary.query)) {
                    data.add(new KeyValueSummary(TYPE_TITLE,"QUERY"));
                    data.add(new KeyValueSummary(TYPE_CONTENT,"query", summary.query));
                }

                data.add(new KeyValueSummary(TYPE_TITLE,"BODY"));
                KeyValueSummary request = new KeyValueSummary(TYPE_CONTENT,"request body", "tap to view",true);
                data.add(request);
                if (summary.status == 2) {
                    KeyValueSummary response = new KeyValueSummary(TYPE_CONTENT,"response body", "tap to view",true);
                    data.add(response);
                }


                if (Utils.isNotEmpty(summary.request_header)) {
                    data.add(new KeyValueSummary(TYPE_TITLE,"REQUEST HEADER"));
                    for (Pair<String, String> pair : summary.request_header) {
                        data.add(new KeyValueSummary(TYPE_CONTENT,pair.first, pair.second));
                    }
                }

                if (Utils.isNotEmpty(summary.response_header)) {
                    data.add(new KeyValueSummary(TYPE_TITLE,"RESPONSE HEADER"));
                    for (Pair<String, String> pair : summary.response_header) {
                        data.add(new KeyValueSummary(TYPE_CONTENT,pair.first, pair.second));
                    }
                }

                mAdapter.setList(data);
            }
        }).execute();
    }

    private void tryOpen(final long id) {
        new SimpleTask<>(new SimpleTask.Callback<Void, String>() {
            @Override
            public String doInBackground(Void[] params) {
                return Content.query(id).responseBody;
            }

            @Override
            public void onPostExecute(String result) {
                if (TextUtils.isEmpty(result)) {
                    toast("faild");
                    return;
                }
                tryOpenInternal(result);
            }
        }).execute();
    }

    private void tryOpenInternal(String path) {
        new SimpleTask<>(new SimpleTask.Callback<File, Intent>() {
            @Override
            public Intent doInBackground(File[] params) {
                String result = FileUtil.fileCopy2Tmp(params[0]);
                if (!TextUtils.isEmpty(result)) {
                    return FileUtil.getFileIntent(result, "image/*");
                }
                return null;
            }

            @Override
            public void onPostExecute(Intent result) {
                hideLoading();
                if (result != null) {
                    try {
                        startActivity(result);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        toast(t.getMessage());
                    }
                } else {
                    toast("not support");
                }
            }
        }).execute(new File(path));
        showLoading();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }


}
