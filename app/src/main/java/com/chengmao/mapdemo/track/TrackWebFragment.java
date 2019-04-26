package com.chengmao.mapdemo.track;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chengmao.mapdemo.Alert;
import com.chengmao.mapdemo.R;
import com.chengmao.mapdemo.bean.TrackListBean;
import com.cysion.baselib.base.BaseFragment;
import com.cysion.baselib.cache.ACache;

/**
 * Created by xsy on 2019/4/26 0026.
 */

public class TrackWebFragment extends BaseFragment {

    private SimpleWebview webView;
    private String URL = "https://trade.5dev.cn/cmmc/track/#/trackView?trackId=";
    private TrackListBean.TrailBean track;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_web_trail;
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        track = (TrackListBean.TrailBean) getArguments().get("track");
        String signature = ACache.get(mActivity).getAsString("signature");
        webView = view.findViewById(R.id.webView);
        webView.loadUrl(URL + track.getTrackId() + "&serviceId=" + track.getService_id()
                + "&terminalId=" + track.getTermianl_id() + "&signature=" + signature + "&type=" + "Android");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Alert.obj().loading(mActivity);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Alert.obj().loaded();
            }
        });
    }
}
