package com.frida.chinese.jchess;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;



public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        LogUtils.getConfig().setLogSwitch(true)
                .setLogHeadSwitch(false).setBorderSwitch(false);
    }
}
