package com.chengmao.mapdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.List;

/**
 * Created by xsy on 2019/2/22 0022.
 */

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeHolder> {
    private String item;
    private List<String> mList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public TypeAdapter(List<String> list, Context ctx, String item) {
        this.mList = list;
        this.mContext = ctx;
        this.item = item;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public TypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_type, null);
        return new TypeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeHolder holder, final int position) {
        holder.tv_type.setChecked(item.equals(mList.get(position)));
        holder.tv_type.setText(mList.get(position));
        holder.tv_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = mList.get(position);
                mOnItemClickListener.onItemClick(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class TypeHolder extends RecyclerView.ViewHolder {
        RadioButton tv_type;

        public TypeHolder(View itemView) {
            super(itemView);
            tv_type = itemView.findViewById(R.id.tv_type);
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }
}
