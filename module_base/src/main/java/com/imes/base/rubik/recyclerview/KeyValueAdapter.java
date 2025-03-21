package com.imes.base.rubik.recyclerview;

import android.graphics.Color;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imes.base.rubik.model.KeyValueSummary;
import com.imes.module_base.R;

import androidx.annotation.NonNull;

import static com.imes.base.rubik.model.KeyValueSummary.TYPE_EXCEPTION;

/**
 * author : quintus
 * date : 2021/12/8 10:03
 * description :
 */
public class KeyValueAdapter extends BaseMultiItemQuickAdapter<KeyValueSummary, BaseViewHolder> {
    private OnItemClickListener listener;

    public KeyValueAdapter() {
        addItemType(KeyValueSummary.TYPE_TITLE, R.layout.pd_item_title);
        addItemType(KeyValueSummary.TYPE_CONTENT, R.layout.pd_item_key_value);
        addItemType(TYPE_EXCEPTION, R.layout.pd_item_exception);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, KeyValueSummary netSummary) {
        switch (holder.getItemViewType()) {
            case KeyValueSummary.TYPE_TITLE:
                holder.setText(R.id.item_title_id, netSummary.value);
                break;
            case KeyValueSummary.TYPE_CONTENT:
                holder.setText(R.id.item_key, netSummary.key)
                        .setBackgroundColor(R.id.item_value, Color.WHITE)
                        .setText(R.id.item_value, netSummary.value);

                holder.setVisible(R.id.item_value, true);
                holder.setGone(R.id.item_edit, true);
                holder.setVisible(R.id.item_arrow, netSummary.clickable);
                if (netSummary.clickable) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) {
                                listener.onItemClick(holder.getAdapterPosition(), netSummary);
                            }
                        }
                    });
                }
                break;
            case TYPE_EXCEPTION:
                holder.setText(R.id.text, netSummary.value);
                break;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, KeyValueSummary summary);
    }
}
