package com.chengmao.mapdemo.track;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.query.entity.DriveMode;
import com.amap.api.track.query.entity.Point;
import com.amap.api.track.query.entity.Track;
import com.amap.api.track.query.model.QueryTrackRequest;
import com.amap.api.track.query.model.QueryTrackResponse;
import com.chengmao.mapdemo.R;
import com.chengmao.mapdemo.bean.TrackListBean;
import com.cysion.baselib.base.BaseFragment;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xsy on 2019/4/22 0022.
 */

public class TrailFragment extends BaseFragment {
    private TextureMapView mapView;
    private TrackListBean.TrailBean track;
    private AMapTrackClient aMapTrackClient;
    private List<Polyline> polylines = new LinkedList<>();
    private List<Marker> endMarkers = new LinkedList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_trail;
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        track = (TrackListBean.TrailBean) getArguments().get("track");
        aMapTrackClient = new AMapTrackClient(mActivity.getApplicationContext());
        mapView = view.findViewById(R.id.trail_map);
        mapView.onCreate(savedInstanceState);
        clearTracksOnMap();
        final QueryTrackRequest queryTrackRequest = new QueryTrackRequest(
                track.getService_id(),
                track.getTermianl_id(),
                track.getTrackId(),     // 轨迹id，不指定，查询所有轨迹，注意分页仅在查询特定轨迹id时生效，查询所有轨迹时无法对轨迹点进行分页
                System.currentTimeMillis() - 12 * 60 * 60 * 1000,
                System.currentTimeMillis(),
                0,      // 不启用去噪
                1,   // 绑路
                0,      // 不进行精度过滤
                DriveMode.DRIVING,  // 当前仅支持驾车模式
                0,     // 距离补偿
                5000,   // 距离补偿，只有超过5km的点才启用距离补偿
                1,  // 结果应该包含轨迹点信息
                1,  // 返回第1页数据，但由于未指定轨迹，分页将失效
                100    // 一页不超过100条
        );
        aMapTrackClient.queryTerminalTrack(queryTrackRequest, new SimpleOnTrackListener() {
            @Override
            public void onQueryTrackCallback(QueryTrackResponse queryTrackResponse) {
                if (queryTrackResponse.isSuccess()) {
                    List<Track> tracks = queryTrackResponse.getTracks();
                    if (tracks != null && !tracks.isEmpty()) {
                        boolean allEmpty = true;
                        for (Track track : tracks) {
                            List<Point> points = track.getPoints();
                            if (points != null && points.size() > 0) {
                                allEmpty = false;
                                drawTrackOnMap(points);
                            }
                        }
                        if (allEmpty) {
                            Toast.makeText(mActivity,
                                    "所有轨迹都无轨迹点，请尝试放宽过滤限制，如：关闭绑路模式", Toast.LENGTH_SHORT).show();
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("共查询到").append(tracks.size()).append("条轨迹，每条轨迹行驶距离分别为：");
                            for (Track track : tracks) {
                                sb.append(track.getDistance()).append("m,");
                            }
                            sb.deleteCharAt(sb.length() - 1);
                            Toast.makeText(mActivity, sb.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mActivity, "未获取到轨迹", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mActivity, "查询历史轨迹失败，" + queryTrackResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void drawTrackOnMap(List<Point> points) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE).width(20);
        if (points.size() > 0) {
            // 起点
            Point p = points.get(0);
            LatLng latLng = new LatLng(p.getLat(), p.getLng());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            endMarkers.add(mapView.getMap().addMarker(markerOptions));
        }
        if (points.size() > 1) {
            // 终点
            Point p = points.get(points.size() - 1);
            LatLng latLng = new LatLng(p.getLat(), p.getLng());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            endMarkers.add(mapView.getMap().addMarker(markerOptions));
        }
        for (Point p : points) {
            LatLng latLng = new LatLng(p.getLat(), p.getLng());
            polylineOptions.add(latLng);
            boundsBuilder.include(latLng);
        }
        Polyline polyline = mapView.getMap().addPolyline(polylineOptions);
        polylines.add(polyline);
        mapView.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 30));
    }

    private void clearTracksOnMap() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        for (Marker marker : endMarkers) {
            marker.remove();
        }
        endMarkers.clear();
        polylines.clear();
    }
}
