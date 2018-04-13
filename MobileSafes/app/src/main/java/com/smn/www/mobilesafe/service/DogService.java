package com.smn.www.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;


import com.smn.www.mobilesafe.LockActivity;
import com.smn.www.mobilesafe.db.AppInfoDao;

import java.util.List;

/**
 * Created by Administrator on 2018-04-13.
 */

public class DogService extends Service {

    private AppInfoDao infoDao;
    private List<String> lockAppList;
    private ActivityManager am;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
         // 服务被开启后, 需要监控 当前打开的程序是否属于 已加锁  的应用
        // 1 .需要 知道 当前打开了 应用程序是什么
        // 每一次打开 一个应用程序 ,就会开启一个任务栈
        // ,该任务栈的栈顶界面 Activity ,包含后一个 该应用程序的 包名
        // 根据这个包名去 已加锁 数据库中 查询, 如果在已加锁 数据库中,就需要弹出 锁屏界面
        infoDao = new AppInfoDao(this);
        //获取所有的应用程序
        lockAppList = infoDao.QueryAll();
        //得到界面管理者
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        startrun();

    }

    private void startrun() {
        final boolean isRun=true;
        new Thread(){
            @Override
            public void run() {
                while (isRun){
                    SystemClock.sleep(300);
                    //获取当前的运行任务的集合
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    String s = runningTaskInfo.toString();
                    Log.i("DogService",s);
                    //得到当前当前栈顶
                    ComponentName topActivity = runningTaskInfo.topActivity;
                    //取得应用程序包名
                    String packageName = topActivity.getPackageName();
                    Log.i("DogService",packageName);
                    //取得的和加锁和数据库中加锁的程序进行比较
                    boolean contains = lockAppList.contains(packageName);
                    //如果现在检测的应用是已经解锁过的应用，那此应用的包名一定在unLockList集合中
                    if (contains){
                        //打开锁屏界面
                        Intent intent = new Intent(getApplicationContext(), LockActivity.class);
                        intent.putExtra("packageName",packageName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    Log.i("DogService","服务被销毁了");
    }
}
