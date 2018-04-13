package com.smn.www.mobilesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2018-03-19.
 */

public class AddressDao {

    private static String address ="未知位置";

    /**
     *
     * @param context   上下文
     * @param phone     要查询的电话号码
     * @return           电话号码归属地
     */
    public static String  getAddress(Context context, String phone){

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(context.getFilesDir() + "/address.db", null
                , SQLiteDatabase.OPEN_READONLY);

        // 正则表达式
        //   第一位 1
        //   第二位 3 5 7 8 9
        //   后9位  只要是数字就可以

        String regex = "^1[35789]\\d{9}";
        if(phone.matches(regex)){
            // 如果 正则表达式匹配成功  ,则进行电话号码归属地查询
            String substring = phone.substring(0,7);

            Cursor cursor = sqLiteDatabase.rawQuery("select cardtype from info where mobileprefix=?;", new String[]{substring});
            if(cursor.moveToNext()){
                address = cursor.getString(0);
            }

        }else{
            switch (phone.length()){
                case 3:
                    //可能是报警电话
                    address = "报警电话";
                    break;
                case 5:
                    //可能是运营商电话
                    address ="运营商电话";
                    break;
                case 8:
                    //可能是本地电话
                    address = "本地电话";
                    break;
                case 11:
                    //可能是 区号位3位  电话号码是 8位的 固定电话
                    String substring = phone.substring(0,3);

                    Cursor cursor = sqLiteDatabase.rawQuery("select city from info where area=?;", new String[]{substring});
                    if(cursor.moveToNext()){
                        address = cursor.getString(0);
                    }
                    break;
                case 12:
                    //可能是 区号位4 位  电话号码是 8位的 固定电话

                    String substring1 = phone.substring(0,4);

                    Cursor cursor1 = sqLiteDatabase.rawQuery("select city from info where area=?;", new String[]{substring1});
                    if(cursor1.moveToNext()){
                        address = cursor1.getString(0);
                    }
                    break;
                default:
                    address ="未知号码";
            }
        }

        return  address;
    }
}
