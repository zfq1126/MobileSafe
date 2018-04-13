package com.smn.www.mobilesafe.ShardPreUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smn.www.mobilesafe.bean.smsInfo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018-03-26.
 */

public class SmsUtil {
    //通过访问内容提供者数据库，查找数据库包表的内容 Uri=conten://sms

    public static  void  backUp(final Activity activity, final SmsBackupListener smsBackupListener){
        new Thread(){
            @Override
            public void run() {
                ContentResolver contentResolver=activity.getContentResolver();
                String [] strings={"address","date","type","body"};
                Cursor cursor = contentResolver.query(Uri.parse("content://sms"), strings, null, null, null);
                ArrayList<smsInfo> list = new ArrayList<>();
                smsBackupListener.setMaxs(cursor.getCount());
                int count=0;
                while (cursor.moveToNext()){
                    SystemClock.sleep(500);
                    String address=cursor.getString(0);
                    int date=cursor.getInt(1);
                    int type=cursor.getInt(2);
                    String body=cursor.getString(3);
                    list.add(new smsInfo(address,date,type,body));
                    count++;
                    smsBackupListener.setProgress(count);
                    Log.i("SmsUtil",address+date+type+body);

                }
                cursor.close();
                //需要将集合中的内容保存到一个文件中
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    String path=Environment.getExternalStorageDirectory()+ File.separator+"sms.json";//file spearater相当于、
                    File file=new File(path);
                    Gson gson=new Gson();
                    String json=gson.toJson(list);
                    //初始化File Writer对象
                    try {
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(json);
                        fileWriter.flush();
                        fileWriter.close();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                smsBackupListener.onSuccess();
                            }
                        });
                        Log.i("json数据显示：",json);
                    } catch (IOException e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                smsBackupListener.onFail();
                            }
                        });
                    }

                }
            }
        }.start();
    }
    //此接口用于回调，设置进度条两个进度条的来回切换
    public  interface SmsBackupListener{
        public void setMaxs(int max);
        public void setProgress(int currentProgress);
        public void onSuccess();
        public  void onFail();
    }
    public static  void restore(final Activity activity, final SmsBackupListener smsBackupListener){
        new Thread(){

            private ContentValues contentValues;

            @Override
            public void run() {
               try {
                   if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                       File file=new File(Environment.getExternalStorageDirectory()+File.separator+"sms.json");
                       //读取内容
                       FileReader fileReader=new FileReader(file);
                       Gson gson=new Gson();
                       //将gson数据转换为arraylist集合
                     Type type= new TypeToken<smsInfo>(){}.getType();
                       ArrayList<smsInfo> smsList = gson.fromJson(fileReader, type);
                       smsBackupListener.setMaxs(smsList.size());
                       int count=0;
                       ContentResolver contentResolver=activity.getContentResolver();
                       for (smsInfo info:smsList){
                           SystemClock.sleep(500);
                           ContentValues values = new ContentValues();
                           values.put("address",info.getAddress());
                           values.put("date",info.getDate());
                           values.put("type",info.getType());
                           values.put("body",info.getBody());
                           contentResolver.insert(Uri.parse("content://sms"),values);
                           count++;
                           smsBackupListener.setProgress(count);
                       }
                       activity.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               smsBackupListener.onSuccess();
                           }
                       });

                   }
               }catch (Exception e){
                   e.printStackTrace();
                   activity.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           smsBackupListener.onFail();
                       }
                   });
               }
            }
        }.start();
    }
}
