package com.smn.www.mobilesafe;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.smn.www.mobilesafe.ShardPreUtils.SharePreUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SplashActivity extends AppCompatActivity {

    @InjectView(R.id.version)
    TextView version;
    @InjectView(R.id.activity_main)
    RelativeLayout activityMain;
    private static final int BACK_REQUESTCODE = 100;
    private String downloadUrl;
    private AssetManager assets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ButterKnife.inject(this);
        version = (TextView) findViewById(R.id.version);
        String versionName = getVersionName();
        version.setText("版本号：" + versionName);
        //获取文件中,是否更新的存储状态
        boolean key= SharePreUtils.getBoolean(this,"key",true);
        if (key){
           // Log.i("SplashActivity","需要更新版本,请及时更新");
            checkVersion();
        }else {
            //Log.i("SplashActivity","网络连接失败,无需更新");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startHomeAcitivty();
                }
            },3000);
        }
        copyDB();
    }

    private void  copyDB() {
        try {
            AssetManager assets = getAssets();
            InputStream is = assets.open("address.db");
            File file=new File(getFilesDir()+"/address.db");
            if (file.exists()){
                //如果数据已经存在，就不需要执行下面的拷贝的工作
                return;
            }
            //Ctrl + Alt + V 快捷生成变量的快捷键
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer=new byte[1024];
            int len=-1;
            while ((len=is.read(buffer))!=-1){
                fos.write(buffer,0,len);
            }
            is.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkVersion() {
        //用来检测新版本
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build();
        String url = "http://192.168.1.106:8080/update.json";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //跳转到主页面
                SystemClock.sleep(1000);
                startHomeAcitivty();
            }

            private void startHomeAcitivty() {

                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String json = body.string();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    final String desc = jsonObject.getString("desc");
                    downloadUrl = jsonObject.getString("downloadUrl");
                    int ramoteVersionCode = jsonObject.getInt("versionCode");
                    int localVersionCode = getVersionCode();
                    if (ramoteVersionCode > localVersionCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //需要弹出一个对话框 提示有新的版本 是否需要更新
                                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                                builder.setTitle("发现新版本");
                                builder.setMessage(desc);
                                builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //想 现在更新版本
                                        //下载新的版本
                                        downloadApk();
                                    }
                                });
                                builder.setNegativeButton("取消更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //跳转到新的页面
                                        SystemClock.sleep(1000);
                                        startHomeAcitivty();
                                    }
                                });
                                builder.show();

                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    startHomeAcitivty();
                }
            }
        });
    }

    private void downloadApk() {
        //首先需要判断 sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            progressDialog.show();

            String path = Environment.getExternalStorageDirectory() + "/mobilesafe_2.0.apk";
            final File file = new File(path);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .readTimeout(2, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .get()
                    .url(downloadUrl)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    startHomeAcitivty();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    ResponseBody body = response.body();
                    long length = body.contentLength();
                    progressDialog.setMax((int) length);
                    InputStream inputStream = body.byteStream();
                    int currentProgress = 0;
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        while ((len = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            currentProgress = currentProgress + len;
                            SystemClock.sleep(2);
                            progressDialog.setProgress(currentProgress);
                        }
                        //安装新版本

                        installApk(file);

                    } catch (Exception e) {
                        e.printStackTrace();
                        startHomeAcitivty();

                    } finally {
                        try {
                            fos.close();
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }


                }
            });

        }

    }

    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, BACK_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BACK_REQUESTCODE) {
            startHomeAcitivty();
        }
    }

    private void startHomeAcitivty() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    //取得包名
    private String getVersionName() {
        //取得包的管理者
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getVersionCode() {
        //取得 包的管理者
        PackageManager packageManager = getPackageManager();
        // 第一个参数 是包名称  第二个参数 是一个整数  0 取得最基本信息
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
    @OnClick({R.id.version, R.id.activity_main})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.version:
                break;
            case R.id.activity_main:
                break;
        }
    }
}