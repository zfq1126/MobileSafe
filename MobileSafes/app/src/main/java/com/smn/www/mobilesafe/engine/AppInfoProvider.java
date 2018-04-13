package com.smn.www.mobilesafe.engine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.smn.www.mobilesafe.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-04-08.
 */

public class AppInfoProvider {
    //给程序的页面提供数据
    public static List<AppInfo> AppInfoList(Context context) {
        //提供所有安装的应用程序
        //得到包的管理者
        PackageManager packageManager = context.getPackageManager();
        //得到已安装包的信息集合
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        //循环取得安装包信息的一部分
        List<AppInfo> appInfos = new ArrayList<>();
        //开始遍历已经安装的包的信息
        for (PackageInfo packageInfo : installedPackages) {
            //首先获取包名
            String packageName = packageInfo.packageName;
            //获取应用程序的名称
            String name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            //获取应用程序的图标
            Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
            AppInfo appInfo = new AppInfo(packageName, drawable ,name);
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
