package com.smn.www.mobilesafe;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.smn.www.mobilesafe.view.SettingItemView;

import com.smn.www.mobilesafe.ShardPreUtils.SmsUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018-03-16.
 */
public class CommonToolsActivity extends Activity {
    @InjectView(R.id.siv_query_location)
    SettingItemView sivQueryLocation;
    @InjectView(R.id.siv_sms_save)
    SettingItemView sivSmsSave;
    @InjectView(R.id.siv_sms_backup)
    SettingItemView sivSmsBackup;
    @InjectView(R.id.siv_programm_lock)
    SettingItemView sivProgrammLock;
    @InjectView(R.id.siv_dog_service)
    SettingItemView sivDogService;
    private ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commontools_activity);
        ButterKnife.inject(this);
        pb= (ProgressBar) findViewById(R.id.pb_sms);
        //动态注册权限，在android 6.0以后，默认是一组现相关的权限，只需要获取一个权限就可以了。
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode==1){
            if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //用户已经授权
            }else {
                //用户拒绝授权
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @OnClick({R.id.siv_query_location, R.id.siv_sms_save, R.id.siv_sms_backup, R.id.siv_programm_lock, R.id.siv_dog_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.siv_query_location:
                Intent intent=new Intent(CommonToolsActivity.this,QueryPhoneActivity.class);
                startActivity(intent);
                break;
            case R.id.siv_sms_save:
                final ProgressDialog progressDialog=new ProgressDialog(this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                SmsUtil.backUp(this,new SmsUtil.SmsBackupListener(){

                    @Override
                    public void setMaxs(int max) {
                        pb.setMax(max);
                        progressDialog.setMax(max);
                    }

                    @Override
                    public void setProgress(int currentProgress) {
                       pb.setProgress(currentProgress);
                        progressDialog.setProgress(currentProgress);
                    }

                    @Override
                    public void onSuccess() {
                       progressDialog.dismiss();
                        Toast.makeText(CommonToolsActivity.this,"备份成功",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail() {
                        progressDialog.dismiss();
                        Toast.makeText(CommonToolsActivity.this,"备份失败",Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.siv_sms_backup:
                final ProgressDialog progressDialog1=new ProgressDialog(this);
                progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog1.show();
                SmsUtil.backUp(this,new SmsUtil.SmsBackupListener(){

                    @Override
                    public void setMaxs(int max) {
                        pb.setMax(max);
                        progressDialog1.setMax(max);
                    }

                    @Override
                    public void setProgress(int currentProgress) {
                         pb.setProgress(currentProgress);
                        progressDialog1.setProgress(currentProgress);
                    }

                    @Override
                    public void onSuccess() {
                        progressDialog1.dismiss();
                        Toast.makeText(CommonToolsActivity.this,"还原成功",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail() {
                        progressDialog1.dismiss();
                        Toast.makeText(CommonToolsActivity.this,"还原失败",Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.siv_programm_lock:
                Intent intent1=new Intent(CommonToolsActivity.this,AppLockAcitivity.class);
                startActivity(intent1);
                break;
            case R.id.siv_dog_service:
                break;
        }
    }
}
