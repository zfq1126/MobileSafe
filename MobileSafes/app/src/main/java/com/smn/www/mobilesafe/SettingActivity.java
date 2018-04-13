package com.smn.www.mobilesafe;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.smn.www.mobilesafe.view.DialogStyle;
import com.smn.www.mobilesafe.view.SettingItemView;


import com.smn.www.mobilesafe.ShardPreUtils.ServiceUtil;
import com.smn.www.mobilesafe.ShardPreUtils.SharePreUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.smn.www.mobilesafe.service.AddressService;

public class SettingActivity extends Activity {
    @InjectView(R.id.siv_update)
    SettingItemView sivUpdate;
    @InjectView(R.id.siv_show_location)
    SettingItemView sivShowLocation;
    @InjectView(R.id.siv_location_style)
    SettingItemView sivLocationStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        ButterKnife.inject(this);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.SYSTEM_ALERT_WINDOW},2);

        boolean key= SharePreUtils.getBoolean(this,"key",true);
        sivUpdate.setOpen(key);
        boolean running = ServiceUtil.isRunning(this, AddressService.class);
        sivShowLocation.setOpen(running);

        if(!Settings.canDrawOverlays(getApplicationContext())) {
            //启动Activity让用户授权
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
            return;
        }
    }


    @OnClick({R.id.siv_update, R.id.siv_show_location, R.id.siv_location_style})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.siv_update:
                boolean open=sivUpdate.Open();//true
                if (open){
                    //如果当前的值是true则点击过后是false
                    sivUpdate.setOpen(false);
                    SharePreUtils.saveBoolean(getApplicationContext(),"key",false);
                }else {
                    sivUpdate.setOpen(true);

                    //当前值是false,则点击过后是true
                    SharePreUtils.saveBoolean(getApplicationContext(),"key",true);
                }
                break;
            case R.id.siv_show_location:
                Intent intent = new Intent(this, AddressService.class);
                //点击过程中开启关闭，归属地显示的服务
                boolean open1 = ServiceUtil.isRunning(this, AddressService.class);
                if (open1){
                    //如果点击前是服务是开启的，点击后就关闭
                    sivShowLocation.setOpen(false);
                    stopService(intent);
                }else {
                    sivShowLocation.setOpen(true);
                    startService(intent);
                }
                break;
            case R.id.siv_location_style:
                DialogStyle dialogStyle = new DialogStyle(SettingActivity.this);
                dialogStyle.setTitle("对话框的标题");
                dialogStyle.show();
                break;
        }
    }
}
