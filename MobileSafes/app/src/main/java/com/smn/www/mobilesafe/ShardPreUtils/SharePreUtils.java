package com.smn.www.mobilesafe.ShardPreUtils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2018-03-15.
 */

public class SharePreUtils {
    private static SharedPreferences sp;

    /**
     *
     * @param context  上下文环境
     * @param key       变量
     * @param value    值
     */
    public static void  saveBoolean(Context context,String key,boolean value){
        if (sp == null) {
            sp =context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,value).commit();
    }
    /**
     *
     * @param context
     * @param key
     * @param defvalue   默认值,如果取不到保存的值,就需要给一个默认值
     * @return
     */
    public  static boolean getBoolean(Context context, String key,boolean defvalue){
        if (sp == null) {
            sp =context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        boolean aBoolean= sp.getBoolean(key,defvalue);
        return aBoolean;
    }

    /**
     *
     * @param context  上下文环境
     * @param key       变量
     * @param value    值
     */
    public static void  saveInt(Context context,String key,int value){
        if (sp == null) {
            sp =context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key,value).commit();
    }
    /**
     *
     * @param context
     * @param key
     * @param defvalue   默认值,如果取不到保存的值,就需要给一个默认值
     * @return
     */
    public  static int getInt(Context context, String key,int defvalue){
        if (sp == null) {
            sp =context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        int aInt= sp.getInt(key,defvalue);
        return aInt;
    }
}
