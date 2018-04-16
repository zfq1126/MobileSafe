package com.smn.www.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018-04-13.
 */

public class LockActivity extends Activity {

    @InjectView(R.id.iv_lock_icon)
    ImageView ivLockIcon;
    @InjectView(R.id.tv_lock_name)
    TextView tvLockName;
    @InjectView(R.id.et_lock_pwd)
    EditText etLockPwd;
    @InjectView(R.id.bt_lock_confirm)
    Button btLockConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        ButterKnife.inject(this);
         Intent intent=getIntent();
        String packagename = intent.getStringExtra("packageName");
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packagename, 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            // 取得 被锁屏应用程序 的图标 在锁屏界面 上 显示出来
            Drawable drawable = applicationInfo.loadIcon(packageManager);
            String name = applicationInfo.loadLabel(packageManager).toString();

            ivLockIcon.setImageDrawable(drawable);
            tvLockName.setText(name);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.bt_lock_confirm)
    public void onViewClicked() {
        String pwd = etLockPwd.getText().toString();
        // 如果 输入的密码是 123 的话, 结束 锁屏界面
        if ("123".equals(pwd)){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
          /* <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.HOME" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.MONKEY"/>
        </intent-filter>*/
        Intent intent=new Intent();
        intent.setAction("android.intent.action.MAIN" );
        intent.addCategory("android.intent.category.HOME" );
        startActivity(intent);
    }
}
