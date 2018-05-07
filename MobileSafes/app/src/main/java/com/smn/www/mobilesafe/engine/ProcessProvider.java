package com.smn.www.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;

import com.smn.www.mobilesafe.R;
import com.smn.www.mobilesafe.bean.ProcessInfo;
import com.superuser.processmanager.ProcessManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2018-04-23.
 */

public class ProcessProvider {

    public static  int  getRunningProcess(){
        /*ctx.getPackageManager();可以管理 四大组件
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();
        考虑到安全的性质，增加了权限，需要导入新的jar包
        导入老师从网上找的jar包
        */
        List<ProcessManager.Process> runningProcesses = ProcessManager.getRunningProcesses();
        return runningProcesses.size();
    }
    public static int getAllProcess(Context ctx){

        PackageManager pm = ctx.getPackageManager();
        // 取得已经安装的所有的应用程序
        //  如何取得没有运行的 app 的 进程  ,通过 取得已经安装的包 里面的  四大组件 全部取出来
        // HashSet  能够确保 内部存储的 每条记录 是唯一的,也就是说不会出现重复记录
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
            // 在所有service 中查找, 如果声明了 process ,就把进程名加入 集合
            ServiceInfo[] services = packageInfo.services;
            if (services != null) {
                for ( ServiceInfo serviceInfo :services) {
                    String ServiceProcessName = serviceInfo.processName;
                    processNameList.add(ServiceProcessName);
                }
            }
            //在 providers 中查找, 如果声明了 process ,就把进程名加入 集合
            ProviderInfo[] providers = packageInfo.providers;
            if (providers != null) {
                for (ProviderInfo providerInfo:providers) {
                    String providerProcessName = providerInfo.processName;
                    processNameList.add(providerProcessName);
                }
            }
            //在 receivers 中查找, 如果声明了 process ,就把进程名加入 集合
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
    // 取得正在运行 应用程序需要的内存
    public  static  long  getRunningMemory(Context ctx){
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        // getMemoryInfo 获取 内存信息 的, 取出来后放在memoryInfo 中
        am.getMemoryInfo(memoryInfo);
        long availMem = memoryInfo.availMem;
        return  availMem;
    }
    //取得总的内存值
    public static long getAllMemory(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;//单位Kb
    }
    // 用来提供 正在运行的应用程序 的( 名称 , 图标 ,占用内存数 , 是否是系统进程)
    public static List getProgressInfo(Context context){
        List<ProcessInfo> processInfos = new ArrayList<>();
        List<ProcessManager.Process> runningProcesses = ProcessManager.getRunningProcesses();
        PackageManager packageManager = context.getPackageManager();
        for (ProcessManager.Process process:runningProcesses) {
            String packageName = process.getPackageName();
            //取得应用程序的图标、名称、
            Drawable drawable=null;
            String name=null;
            boolean isSys=false;//判断是否是系统的应用程序
            try {
                ApplicationInfo applicationInfo = process.getApplicationInfo(context, 0);
                  drawable = applicationInfo.loadIcon(packageManager);
                  name = applicationInfo.loadLabel(packageManager).toString();
                if( (applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                    isSys=true;//系统进程
                }else {
                    isSys=false;//用户进程
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //处理异常情况
                 drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
                 name=packageName;
                 isSys=true;
            }
            int pid = process.pid;
            //当前应用的内存数
            ActivityManager  am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int [] pids=new int[]{pid};
            //取得当前进程的内存信息
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(pids);
            //当前进程的所有值
            int totalPss = processMemoryInfo[0].getTotalPss();
            ProcessInfo processInfo = new ProcessInfo(packageName, drawable, name, totalPss, isSys);
            Log.i("aaaaaaa",processInfo.toString());
            processInfos.add(processInfo);
        }
        return processInfos;
    }

    public static void killProcess(Context context, String packageName) {
      ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
       am.killBackgroundProcesses(packageName);
    }

    public static void KillAllProcess(final Context context) {
    new Thread(){
        @Override
        public void run() {
            ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ProcessManager.Process> runningProcesses = ProcessManager.getRunningProcesses();
            for (ProcessManager.Process process: runningProcesses) {
                if (!context.getPackageName().equals(process.getPackageName())){
                    Log.i("aaa",process.getPackageName());
                    am.killBackgroundProcesses(process.getPackageName());
                }
            }
        }
    }.start();
    }
}
