package com.smn.www.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.smn.www.mobilesafe.R;
import com.smn.www.mobilesafe.bean.VirusInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018-05-10.
 */
public class KillVirusActivity extends Activity {
    @InjectView(R.id.arc_progress)
    ArcProgress arcProgress;
    @InjectView(R.id.tv_app_name)
    TextView tvAppName;
    @InjectView(R.id.rl_safe)
    RelativeLayout rlSafe;
    @InjectView(R.id.tv_safe)
    TextView tvSafe;
    @InjectView(R.id.btn_safe)
    Button btnSafe;
    @InjectView(R.id.ll_safe)
    LinearLayout llSafe;
    @InjectView(R.id.iv_left)
    ImageView ivLeft;
    @InjectView(R.id.iv_right)
    ImageView ivRight;
    @InjectView(R.id.ll_anim)
    LinearLayout llAnim;
    @InjectView(R.id.lv_safe)
    ListView lvSafe;
    private ArrayList<VirusInfo> appList;
    private PackageManager packageManager;
    private KillVirusActivity.myAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_virus);
        ButterKnife.inject(this);
        initData();
        // 异步 操作
    }
    // 泛型一:  doInBackground 的参数
    //泛型二:  publishProgress 的参数
    // 泛型三: doInBackground 返回值类型
    class scanTask extends AsyncTask<String,VirusInfo,String>{
        //异步操作前
        @Override
        protected void onPreExecute() {
            // 在 doInBackground  之前执行 该方法 是在主线程中执行 例如:  当子线程正网络中下载 数据时候, 在主页面中出现进度条 或 转圈的 等待的图标
            Log.i("","onPreExecute方法执行了.......");
            packageManager = getPackageManager();
            appList=new ArrayList<>();
            myAdapter = new myAdapter();
            lvSafe.setAdapter(myAdapter);
            super.onPreExecute();
        }
         //异步操作中
        @Override
        protected String doInBackground(String... params) {
            // 由 谷歌开发好的 已经开启了子线程 该方法是在子线程中工作  耗时的操作放在该位置
            Log.i("",params[0]);
            Log.i("","doInBackground 执行了.");
            List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
            for (PackageInfo info:installedPackages) {
                String packageName = info.packageName;
                ApplicationInfo applicationInfo = info.applicationInfo;
                //获取应用程序的图标
                Drawable drawable = applicationInfo.loadIcon(packageManager);
                //获取应用程序的名称
                String name = applicationInfo.loadLabel(packageManager).toString();
                VirusInfo virusInfo = new VirusInfo(name, packageName, drawable, true);
                publishProgress(virusInfo); //将当前的数据 实时的更新到 Progress
                SystemClock.sleep(180);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(VirusInfo... values) {
            // 在主线程中操作的
            // 将 循环的包信息 实时的传递到该方法 ,以此的添加到listview中,并实时刷新 适配器
            //当 listview中一个屏幕满了的时候,应该将当前屏幕的信息向上移动
            VirusInfo virusInfo = values[0];

            appList.add(virusInfo);
            // 当屏幕,满了的时候 当前移动到 哪个位置
            myAdapter.notifyDataSetChanged();
            lvSafe.smoothScrollToPosition(appList.size()-1);
            super.onProgressUpdate(values);

        }

        //异步操作后
        @Override
        protected void onPostExecute(String s) {
            //doInBackground 之后执行的方法  该方法也是在 主线程中  例如 当子线程执行完了 网络下载.需要把进度条 或者 转圈的图标 隐藏
            Log.i("","onPostExecute 执行了.");
            myAdapter.notifyDataSetChanged();
            // 当扫描完所有的 应用程序后,定位到第0个位置
            lvSafe.smoothScrollToPosition(0);
            super.onPostExecute(s);
        }
    }
    private void initData() {
        scanTask scanTask = new scanTask();
        scanTask.execute("我是参数...........");

    }
    private class myAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return appList.size();

        }

        @Override
        public VirusInfo getItem(int position) {
            return appList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder=null;
            //ViewHolder用来优化FindViewById查找速率，不用反复的来查找
            if (convertView==null){
                viewHolder=new ViewHolder();
                convertView=View.inflate(getApplicationContext(),R.layout.item_app_info,null);
                viewHolder.ivAppIcon= (ImageView) convertView.findViewById(R.id.iv_app_icon);
                viewHolder.tvAppName= (TextView) convertView.findViewById(R.id.tv_app_name);
                viewHolder.tvAppSafe= (TextView) convertView.findViewById(R.id.tv_app_safe);
                convertView.setTag(viewHolder);
            }else {
                // 当 contentview 有数据的时候,需要把前面放入进去的viewholder 拿出来用
                viewHolder= (ViewHolder) convertView.getTag();
            }
            VirusInfo item = getItem(position);
            viewHolder.ivAppIcon.setImageDrawable(item.getDrawable());
            viewHolder.tvAppName.setText(item.getName());
            if (item.isSafe()){
                viewHolder.tvAppSafe.setText("安全");
                viewHolder.tvAppSafe.setTextColor(Color.GREEN);
            }else {
                viewHolder.tvAppSafe.setText("病毒");
                viewHolder.tvAppSafe.setTextColor(Color.RED);
            }
            return convertView;
        }
    }
    class ViewHolder{
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvAppSafe;
    }
    @OnClick(R.id.btn_safe)
    public void onViewClicked() {
    }


}
