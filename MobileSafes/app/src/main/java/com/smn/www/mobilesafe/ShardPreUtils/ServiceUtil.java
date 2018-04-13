package com.smn.www.mobilesafe.ShardPreUtils;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2018-03-20.
 */

public class ServiceUtil {
    public  static boolean isRunning(Context context,Class clazz){
        //class传入服务的字节码文件
        //activityManager获取手机上所有的正在运行的服务
        //拿到所有服务开始循环遍历，和传递过来的服务进行比较
        //如果拿到的服务是一致的，则说明传递过来的服务是开启的
        //否则服务是关闭的
        //
        ActivityManager manager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取当前的服务状态，正在运行的状态
        List<ActivityManager.RunningServiceInfo> runningServiceInfos=manager.getRunningServices(1000);
        String clazzName = clazz.getName();
        for (ActivityManager.RunningServiceInfo serviceinfo: runningServiceInfos)
        {
            //获取每个服务运行的名称
            ComponentName service = serviceinfo.service;
            String serviceName = service.getClassName();
            //通过类的字节码文件，获取服务的类名
            if (serviceName.equals(clazzName)){
                return true;
            }

        }
        return false;
    }
}
