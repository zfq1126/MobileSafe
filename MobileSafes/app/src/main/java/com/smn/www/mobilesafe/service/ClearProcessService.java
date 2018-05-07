package com.smn.www.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.smn.www.mobilesafe.engine.ProcessProvider;

/**
 * Created by Administrator on 2018-05-07.
 */
public class ClearProcessService extends Service{

    private CloseScreenReceiver closeScreenReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("","服务被开启了");
        // 监听 锁屏广播 ,如果锁屏广播被接收到了,需要响应 (杀死所有进程)
        // 通过动态 建立一个广播接收者
        // 接收的广播事件 Intent.ACTION_SCREEN_OFF
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        closeScreenReceiver = new CloseScreenReceiver();

        registerReceiver(closeScreenReceiver,intentFilter);

        super.onCreate();
    }

    class CloseScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProcessProvider.KillAllProcess(context);
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(closeScreenReceiver);
        Log.i("","服务被销毁了");
        super.onDestroy();
    }
}
