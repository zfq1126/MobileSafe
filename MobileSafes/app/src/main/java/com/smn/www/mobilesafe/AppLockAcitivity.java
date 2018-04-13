package com.smn.www.mobilesafe;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.smn.www.mobilesafe.bean.AppInfo;
import com.smn.www.mobilesafe.db.AppInfoDao;
import com.smn.www.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018-04-02.
 */
public class AppLockAcitivity extends Activity {
    @InjectView(R.id.bt_unlock)
    Button btUnlock;
    @InjectView(R.id.bt_lock)
    Button btLock;
    @InjectView(R.id.lv_unlock)
    ListView lvUnlock;
    @InjectView(R.id.lv_lock)
    ListView lvLock;
    private List<AppInfo> unLockList;
    private List<AppInfo> lockList;
    private AppInfoDao dao;
    private TextView tv_lock_desc;
    private TranslateAnimation leftTorightAnimation;
    private TranslateAnimation rightToleftAnimation;
    private MyAdapter myadapterLock;
    private MyAdapter myadapterUnLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        ButterKnife.inject(this);
        tv_lock_desc = (TextView) findViewById(R.id.tv_lock_desc);
        dao = new AppInfoDao(this);
        initDada();
        initAnimation();
    }

    private void initAnimation() {
        //从左往右移动
         leftTorightAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF,1.0f,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0
        );
        leftTorightAnimation.setDuration(500);

        //从右往右移动
        rightToleftAnimation = new TranslateAnimation(
              Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,-1.0f,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0

       );
        rightToleftAnimation.setDuration(500);
    }

    private void initDada() {
        new Thread() {
            @Override
            public void run() {
                //遍历已将安装城程序的所有信息
                List<AppInfo> appInfos = AppInfoProvider.AppInfoList(AppLockAcitivity.this);

                //未加锁的集合
                unLockList = new ArrayList<>();
                //已加锁的集合
                lockList = new ArrayList<>();
                for (AppInfo appInfo : appInfos) {
                    // 需要检测这些应用程序是加锁的还是没有加锁
                    // 需要一个已加锁的 数据库,专门用来 对是否加锁应用程序信息的 增 删 查
                    // 如何判断 属于已加锁 的还是 未加锁的 : 通过包名在数据库中查询 ,如果查到,说明属于已经加锁的 应用,否则属于没有加锁的应用

                    boolean b = dao.QueryPackagename(appInfo.getPackageName());
                    if (b) {
                        // 说明属于已经加锁的应用   将这些应用放在一个已加锁的应用集合中
                        lockList.add(appInfo);
                    } else {
                        // 说明属于已经加锁的应用   将这些应用放在一个未加锁的应用集合中
                        unLockList.add(appInfo);
                    }
                    //要为ListView匹配数据，不能再子线程中，需要在主线程中
                    Log.i("zzzzzzzzz", appInfo.toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //对listview数据进行适配
                        //为了区分是哪个数据匹配的数据匹配器需要在匹配时候传入一个参数
                        myadapterLock = new MyAdapter(true);
                        //加锁时视图时候的数据适配器
                        lvLock.setAdapter(myadapterLock);
                        //未加锁时视图是的数据适配
                        myadapterUnLock = new MyAdapter(false);
                        lvUnlock.setAdapter(myadapterUnLock);

                        tv_lock_desc.setText("未加锁（"+unLockList.size()+")");
                    }
                });
            }
        }.start();

    }
    private class MyAdapter extends BaseAdapter {
        private boolean isLock;
        private boolean isStart;
        public MyAdapter(boolean isLock) {
            this.isLock = isLock;
        }

        @Override
        public int getCount() {
            if (isLock) {
                //匹配的是已经加锁的集合
                return lockList.size();
            } else {
                //匹配的是已经加锁的集合
                return unLockList.size();
            }
        }

        @Override
        public Object getItem(int position) {
            //返回的是当前记录
            if (isLock) {
                return lockList.get(position);

            } else {
                return unLockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(AppLockAcitivity.this, R.layout.app_lock_item, null);
                // 对 多次使用findviewbyid 进行优化
                // 第一次将找到 的 id 号 保存到一个viewHolder类中 ,然后将这个类放在 convertview 中
                viewHolder = new ViewHolder();
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_item_lock_icon);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_item_lock_desc);
                viewHolder.ivPress = (ImageView) convertView.findViewById(R.id.iv_item_lock_press);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // 当前是 哪条记录 (已加锁 中集合记录 还是 未加锁 集合中的记录 )
            final AppInfo appInfo = (AppInfo) getItem(position);
            viewHolder.ivIcon.setImageDrawable(appInfo.getDrawable());
            viewHolder.tvName.setText(appInfo.getPackageName());
            if (isLock) {
                // 如果是 加锁 页面,显示 开锁的图标
                viewHolder.ivPress.setImageResource(R.drawable.selector_item_unlock_bg);
            } else {
                // 如果是 未加锁 页面,显示 加锁的图标
                viewHolder.ivPress.setImageResource(R.drawable.selector_item_lock_bg);
            }
            final View itemAnimation=convertView;
            viewHolder.ivPress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //给每个加锁图标或者没有加锁的图标添加点击事件
                    if (isStart){
                        return;//当动画在运行的时候，不能够在执行点击事件的按钮
                    }
                    if (isLock) {
                        /*如果点击事件已经加锁
                        *在加锁的数据库中移除在已经加锁的集合中删除记录
                        * 在未加锁的集合中添加该记录
                        * 然后刷新两个适配器的界面
                        * */
                        boolean success=dao.delete(appInfo.getPackageName());
                        if (success){
                            //当一个动画在执行的时候，其他的点击状态不能够点击
                            rightToleftAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    isStart=true;//当正在运行的时候
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    isStart=false;
                                    lockList.remove(appInfo);
                                    unLockList.add(appInfo);
                                    myadapterLock.notifyDataSetChanged();
                                    myadapterUnLock.notifyDataSetChanged();

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            itemAnimation.startAnimation(rightToleftAnimation);
                        }else {
                            Toast.makeText(AppLockAcitivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        // 如果点击的是 未加锁
                        // 当前这个 页面 少一条记录
                        // 已加锁 页面 多一条记录         向已加锁数据库中添加一条数据
                        boolean success=dao.Insert(appInfo.getPackageName());
                        if (success) {
                            leftTorightAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    isStart=true;
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    isStart=false;
                                    unLockList.remove(appInfo);
                                    lockList.add(appInfo);
                                    /* 刷新Listview*/
                                    myadapterUnLock.notifyDataSetChanged();
                                    myadapterLock.notifyDataSetChanged();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            itemAnimation.startAnimation(leftTorightAnimation);
                        }else {
                            Toast.makeText(AppLockAcitivity.this,"插入不成功",Toast.LENGTH_SHORT);
                        }
                        }
                    }
            });
            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        ImageView ivPress;
    }

    @OnClick({R.id.bt_unlock, R.id.bt_lock})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_unlock:
                // 当点击 未加锁的时候  未加锁的 背景 变为紫色 文字颜色为白色
                // 已加锁 背景变为 白色  文字颜色变为紫色，
                //修改关于背景颜色的设置
                btUnlock.setBackgroundResource(R.drawable.shape_unlock_purple_bg);
                btUnlock.setTextColor(Color.WHITE);

                btLock.setBackgroundResource(R.drawable.shape_lock_write_bg);
                btLock.setTextColor(Color.BLUE);
                lvUnlock.setVisibility(View.VISIBLE);
                lvLock.setVisibility(View.GONE);
                //显示未加锁的数目
                tv_lock_desc.setText("未加锁（"+unLockList.size()+")");

                break;
            case R.id.bt_lock:
                // 当点击 已加锁的时候  已加锁的 背景 变为紫色 文字颜色为白色
                // 未加锁 背景变为 白色  文字颜色变为紫色
                btLock.setBackgroundResource(R.drawable.shape_lock_purple_bg);
                btLock.setTextColor(Color.WHITE);

                btUnlock.setBackgroundResource(R.drawable.shape_unlock_write_bg);
                btUnlock.setTextColor(Color.BLUE);

                lvUnlock.setVisibility(View.GONE);
                lvLock.setVisibility(View.VISIBLE);
                tv_lock_desc.setText("已加锁（"+lockList.size()+")");
                break;
        }
    }


}
