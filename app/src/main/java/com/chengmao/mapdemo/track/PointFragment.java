package com.chengmao.mapdemo.track;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chengmao.mapdemo.Alert;
import com.chengmao.mapdemo.MapApi;
import com.chengmao.mapdemo.R;
import com.chengmao.mapdemo.bean.PointBean;
import com.chengmao.mapdemo.bean.TrackListBean;
import com.cysion.baselib.base.BaseFragment;
import com.cysion.baselib.cache.ACache;
import com.cysion.baselib.net.Caller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xsy on 2019/4/22 0022.
 */

public class PointFragment extends BaseFragment {

    private RecyclerView rv_point;
    private MySmartRefreshLayout smart_refresh;
    private TrackListBean.TrailBean track;
    private List<PointBean> mList = new ArrayList<>();
    private PointAdapter pointAdapter;
    private int page = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_point;
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        track = (TrackListBean.TrailBean) getArguments().get("track");
        rv_point = view.findViewById(R.id.rv_point);
        smart_refresh = view.findViewById(R.id.smart_refresh);
        smart_refresh.setEnableLoadMore(true);
        smart_refresh.setEnableAutoLoadMore(true);
        rv_point.setNestedScrollingEnabled(false);
        pointAdapter = new PointAdapter(mActivity, mList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rv_point.setLayoutManager(layoutManager);
        rv_point.setAdapter(pointAdapter);
        smart_refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
            }
        });
        smart_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData();
            }
        });
    }

    private void loadMore() {
        String signature = ACache.get(mActivity).getAsString("signature");
        Caller.obj().load(MapApi.class).pointList(signature, track.getTrail_id() + "", page).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Alert.obj().loaded();
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray data = jsonObject.getJSONArray("data");
                    Gson gson = new Gson();
                    List<PointBean> list = gson.fromJson(data.toString(), new TypeToken<List<PointBean>>() {
                    }.getType());
                    mList.addAll(list);
                    pointAdapter.notifyDataSetChanged();
                    setLoadMoreEnable();
                    page++;
                } catch (JSONException e) {
                }
                smart_refresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Alert.obj().loaded();
                smart_refresh.finishLoadMore();
            }
        });
    }

    @Override
    protected void initData() {
        getData();
    }

    private void getData() {
        page = 1;
        String signature = ACache.get(mActivity).getAsString("signature");
        Caller.obj().load(MapApi.class).pointList(signature, track.getTrail_id() + "", page).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Alert.obj().loaded();
                smart_refresh.finishRefresh();
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = data.getJSONArray("data");
                    Gson gson = new Gson();
                    List<PointBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<PointBean>>() {
                    }.getType());
                    mList.clear();
                    mList.addAll(list);
                    pointAdapter.notifyDataSetChanged();
                    page++;
                } catch (Exception e) {
                }
                setLoadMoreEnable();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Alert.obj().loaded();
                smart_refresh.finishRefresh();
            }
        });
    }

    private void setLoadMoreEnable() {
        if (smart_refresh == null) return;
        if (mList.size() > page * 9) {
            smart_refresh.setEnableLoadMore(true);
        } else {
            smart_refresh.setEnableLoadMore(false);
        }
    }
}
