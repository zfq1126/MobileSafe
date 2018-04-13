package com.smn.www.mobilesafe;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class HomeActivity extends Activity {

    @InjectView(R.id.ivlog)
    ImageView ivlog;
    @InjectView(R.id.tv_logtext)
    TextView tvLogtext;
    @InjectView(R.id.ll_title)
    LinearLayout llTitle;
    @InjectView(R.id.iv_setting)
    ImageView ivSetting;
    @InjectView(R.id.gv)
    GridView gv;
    private String[] names = new String[]{"常用工具", "进程管理", "杀毒软件", "功能设置"};
    private String[] descs = new String[]{"工具大全", "管理运行进程", "病毒无处藏身", "占位"};
    private int[] icons = new int[]{R.mipmap.cygj, R.mipmap.jcgl, R.mipmap.sjsd, R.mipmap.srlj};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.inject(this);

        ImageView ivLog = (ImageView) findViewById(R.id.ivlog);
        //初始化 动画的对象 并设置动画属性
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivLog, "rotationY", 0, 360);
        //设置转一周需要的时间ms
        objectAnimator.setDuration(2000);
        //设置旋转的方式  翻转
        objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        // 设置旋转的次数  一直循环下去
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        //开启动画
        objectAnimator.start();

        GridView gv = (GridView) findViewById(R.id.gv);
        gv.setAdapter(new myAdapter());
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //通过position判断点击的位
                switch (position){
                        //常用工具管理
                    case 0:
                        Intent intent=new Intent(HomeActivity.this,CommonToolsActivity.class);
                        startActivity(intent);
                        //进程管理
                    case 1:
                        //杀毒软件
                    case 2:
                        //功能设置
                    case 3:

                }
            }
        });
    }

    @OnClick(R.id.iv_setting)
    public void onViewClicked() {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int i) {
            return names[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(HomeActivity.this, R.layout.home_item, null);
            }
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_item_icon);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_item_name);
            TextView tv_desc = (TextView) view.findViewById(R.id.tv_item_desc);
            ivIcon.setImageResource(icons[i]);
            tv_name.setText(names[i]);
            tv_desc.setText(descs[i]);
            return view;
        }
    }

}