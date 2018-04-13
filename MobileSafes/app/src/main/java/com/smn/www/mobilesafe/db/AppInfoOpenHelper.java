package com.smn.www.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018-04-08.
 */

public class AppInfoOpenHelper extends SQLiteOpenHelper {
    //这个类用于创建数据库和数据表
    public AppInfoOpenHelper(Context context) {
        //创建数据库
        super(context,DBConstent.DB_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String sql="create table "+DBConstent.TABLE_NAME+"(_id integer primary key, "+DBConstent.PACKAGENAME+" varchar(25));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
