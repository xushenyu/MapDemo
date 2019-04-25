package com.chengmao.mapdemo.track;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chengmao.mapdemo.R;
import com.chengmao.mapdemo.bean.TrackListBean;

import java.util.List;

/**
 * Created by xsy on 2019/4/20 0020.
 */

public class TackAdapter extends RecyclerView.Adapter {

    private final List<TrackListBean.TrailBean> mList;
    private Context mContext;
    private OnItemClickListener mListener;

    public TackAdapter(Context src, List<TrackListBean.TrailBean> list) {
        this.mContext = src;
        this.mList = list;
    }

    public void setOnItemClickListenet(OnItemClickListener listenet) {
        this.mListener = listenet;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_track, null);
        return new TrackHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        TrackHolder viewHolder = (TrackHolder) holder;
        TrackListBean.TrailBean trailBean = mList.get(position);
        viewHolder.tv_name.setText(trailBean.getName());
        viewHolder.tv_time.setText(trailBean.getStart());
        viewHolder.tv_distance.setText(trailBean.getSpace() + "，" + trailBean.getTime() + "，" + trailBean.getDotnum() + "个标记点");
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(mList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class TrackHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_time;
        TextView tv_distance;
        View view;

        public TrackHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_distance = itemView.findViewById(R.id.tv_distance);
        }
    }

    interface OnItemClickListener {
        void onItemClick(TrackListBean.TrailBean bean);
    }
}
