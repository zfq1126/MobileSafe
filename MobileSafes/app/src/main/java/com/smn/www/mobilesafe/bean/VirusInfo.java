package com.smn.www.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2018-05-11.
 */

public class VirusInfo {
    private String name;
    private  String packageName;
    private Drawable drawable;
    private  boolean isSafe;  // 判断 程序是否有病毒 true 安全 false 不安全

    public VirusInfo(String name, String packageName, Drawable drawable, boolean isSafe) {
        this.name = name;
        this.packageName = packageName;
        this.drawable = drawable;
        this.isSafe = isSafe;
    }
    public VirusInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public void setSafe(boolean safe) {
        isSafe = safe;
    }
}
