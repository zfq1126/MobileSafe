package com.smn.www.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2018-04-23.
 */

public class ProcessProvider {

    public static  int  getRunningProcess(Context ctx){

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();
    }
    public static int getAllProcess(Context ctx){

        PackageManager pm = ctx.getPackageManager();
        // 取得已经安装的所有的应用程序
        HashSet<String> processNameList = new HashSet<>();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES
                | PackageManager.GET_PROVIDERS
                |PackageManager.GET_RECEIVERS
                |PackageManager.GET_SERVICES);
        for (PackageInfo packageInfo :installedPackages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            String processName = applicationInfo.processName;
            processNameList.add(processName);
            // 该应用程序中包含的所有的acitivity
            ActivityInfo[] activities = packageInfo.activities;
            if (activities != null) {
                for (ActivityInfo activityInfo:activities) {
                    String activityProcessName = activityInfo.processName;
                    processNameList.add(activityProcessName);
                }
            }
            ServiceInfo[] services = packageInfo.services;
            if (services != null) {
                for ( ServiceInfo serviceInfo :services) {
                    String ServiceProcessName = serviceInfo.processName;
                    processNameList.add(ServiceProcessName);
                }
            }
            ProviderInfo[] providers = packageInfo.providers;
            if (providers != null) {
                for (ProviderInfo providerInfo:providers) {
                    String providerProcessName = providerInfo.processName;
                    processNameList.add(providerProcessName);
                }
            }
            ActivityInfo[] receivers = packageInfo.receivers;
            if (receivers != null) {
                for (ActivityInfo Info: receivers) {
                    String receiverProcessName = Info.processName;
                    processNameList.add(receiverProcessName);
                }
            }
        }
        return processNameList.size();
    }
    public  static  long  getRunningMemory(Context ctx){
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        // getMemoryInfo 获取 内存信息 的, 取出来后放在memoryInfo 中
        am.getMemoryInfo(memoryInfo);
        long availMem = memoryInfo.availMem;
        return  availMem;
    }
}
