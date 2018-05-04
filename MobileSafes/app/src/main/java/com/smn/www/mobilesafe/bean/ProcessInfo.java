package com.smn.www.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2018-04-26.
 */

public class ProcessInfo {
    private String packageName;
    private Drawable drawable;
    private String name;
    private int intMemory;
    private boolean isSys;
    private boolean isCheck;

    public ProcessInfo(String packageName, Drawable drawable, String name, int intMemory, boolean isSys) {
        this.packageName = packageName;
        this.drawable = drawable;
        this.name = name;
        this.intMemory = intMemory;
        this.isSys = isSys;
    }

    public ProcessInfo() {
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "packageName='" + packageName + '\'' +
                ", drawable=" + drawable +
                ", name='" + name + '\'' +
                ", intMemory=" + intMemory +
                ", isSys=" + isSys +
                '}';
    }
    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIntMemory() {
        return intMemory;
    }

    public void setIntMemory(int intMemory) {
        this.intMemory = intMemory;
    }

    public boolean isSys() {
        return isSys;
    }

    public void setSys(boolean sys) {
        isSys = sys;
    }
}
