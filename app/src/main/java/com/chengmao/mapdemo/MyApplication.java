package com.chengmao.mapdemo;

import android.app.Application;

import com.cysion.baselib.Box;
import com.cysion.baselib.net.AInjector;
import com.cysion.baselib.net.Caller;

import java.util.Map;

/**
 * Created by xsy on 2019/2/21 0021.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Box.init(this, false);
        Caller.obj().inject("http://trade.5dev.cn/cmmc/", new AInjector() {
            @Override
            public Map<String, String> headers() {
                return null;
            }
        });
        WXCaller.obj().inject("https://api.weixin.qq.com/sns/", new AInjector() {
            @Override
            public Map<String, String> headers() {
                return null;
            }
        });
    }
}
