package com.chengmao.mapdemo.track;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chengmao.mapdemo.R;
import com.chengmao.mapdemo.bean.PointBean;

import java.util.List;

/**
 * Created by xsy on 2019/4/24 0024.
 */

public class PointAdapter extends RecyclerView.Adapter {
    private List<PointBean> mList;
    private Context mContext;

    public PointAdapter(Context ctx, List<PointBean> list) {
        this.mContext = ctx;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_point, null);
        return new PointHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PointHolder viewHolder = (PointHolder) holder;
        List<String> pic = mList.get(position).getPic();
        if (pic != null && pic.size() > 0)
            Glide.with(mContext).load(pic.get(0)).into(viewHolder.imageView);
        viewHolder.tv_name.setText(mList.get(position).getName());
        viewHolder.tv_time.setText(mList.get(position).getCdate());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class PointHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tv_name;
        TextView tv_time;

        public PointHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
