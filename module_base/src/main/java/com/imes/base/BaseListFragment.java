package com.imes.base;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imes.module_base.R;

public class BaseListFragment extends BaseFragment{
    private RecyclerView recyclerView;
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
        return recyclerView;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    protected boolean needDefaultDivider() {
        return true;
    }
    @Override
    public void initData(Bundle state) {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }
}
