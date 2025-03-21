package com.imes.base.rubik.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.imes.base.BaseFragment;
import com.imes.base.rubik.cache.Crash;
import com.imes.base.rubik.model.KeyValueSummary;
import com.imes.base.rubik.recyclerview.KeyValueAdapter;
import com.imes.base.utils.Utils;
import com.imes.module_base.R;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author : quintus
 * date : 2021/12/8 09:51
 * description :
 */
public class CrashStackFragment extends BaseFragment {
    private StringBuilder formatText = new StringBuilder();

    private Crash crash;
    private RecyclerView recyclerView;
    private KeyValueAdapter mAdapter;

    @Override
    public void initData(Bundle state) {
        crash = (Crash) getArguments().getSerializable(PARAM1);

        final String time = Utils.millis2String(crash.createTime, Utils.NO_MILLIS);
        getToolbar().setTitle(time);
        getToolbar().getMenu().add(-1, 0, 0, "copy");
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getOrder() == 0) {
                    Utils.copy2ClipBoard(formatText.toString());
                }
                return true;
            }
        });
        formatText.append("time: ").append(time).append("\n");

        List<KeyValueSummary> items = new ArrayList<>();
        formatText.append(crash.stack).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_EXCEPTION,crash.stack));

        formatText.append("duration: ").append(Utils.formatDuration(crash.createTime - crash.startTime)).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_CONTENT,"duration", Utils.formatDuration(crash.createTime - crash.startTime)));

        formatText.append("versionCode: ").append(String.valueOf(crash.versionCode)).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_CONTENT,"versionCode", String.valueOf(crash.versionCode)));

        formatText.append("versionName: ").append(String.valueOf(crash.versionName)).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_CONTENT,"versionName", String.valueOf(crash.versionName)));

        formatText.append("androidSDK: ").append(String.valueOf(crash.systemSDK)).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_CONTENT,"androidSDK", String.valueOf(crash.systemSDK)));

        formatText.append("androidVersion: ").append(String.valueOf(crash.systemVersion)).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_CONTENT,"androidVersion", String.valueOf(crash.systemVersion)));

        formatText.append("rom: ").append(String.valueOf(crash.rom)).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_CONTENT,"rom", String.valueOf(crash.rom)));

        formatText.append("cpuABI: ").append(String.valueOf(crash.cpuABI)).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_CONTENT,"cpuABI", String.valueOf(crash.cpuABI)));

        formatText.append("phoneName: ").append(String.valueOf(crash.phoneName)).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_CONTENT,"phoneName", String.valueOf(crash.phoneName)));

        formatText.append("locale: ").append(String.valueOf(crash.locale)).append("\n");
        items.add(new KeyValueSummary(KeyValueSummary.TYPE_CONTENT,"locale", String.valueOf(crash.locale)));
        mAdapter.setList(items);
    }

    @Override
    protected int getLayoutId() {
        return 0;
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
}
