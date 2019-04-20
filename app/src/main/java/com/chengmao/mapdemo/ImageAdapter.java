package com.chengmao.mapdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by xsy on 2019/2/22 0022.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private Context mContext;
    private List<String> mList;

    private OnItemClickListener mOnItemClickListener;

    public ImageAdapter(Context ctx) {
        this.mContext = ctx;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, null);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        if (position != getItemCount() - 1) {
            Glide.with(mContext).load(mList.get(position)).placeholder(R.mipmap.img_default_error)
                    .error(R.mipmap.img_default_error).centerCrop().into(holder.image);
        } else {
            Glide.with(mContext).load(R.mipmap.papers).centerCrop().into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 1 : mList.size() + 1;
    }

    public void setData(List<String> list){
        this.mList = list;
        notifyDataSetChanged();
    }
    class ImageHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ImageHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

    interface OnItemClickListener {
        void onItemClick();
    }
}
