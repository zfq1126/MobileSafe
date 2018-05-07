package com.smn.www.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smn.www.mobilesafe.R;
import com.smn.www.mobilesafe.ShardPreUtils.ServiceUtil;
import com.smn.www.mobilesafe.ShardPreUtils.SharePreUtils;
import com.smn.www.mobilesafe.bean.ProcessInfo;
import com.smn.www.mobilesafe.engine.ProcessProvider;
import com.smn.www.mobilesafe.service.ClearProcessService;
import com.smn.www.mobilesafe.view.Process_item_view;
import com.smn.www.mobilesafe.view.SettingItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by Administrator on 2018-04-16.
 */

public class ProgressManageActivity extends Activity {
    private static final int TEXT_ITEM = 0;
    private static final int IMG_TEXT_ITEM = 1;
    private static final String SHOWSYS = "showsys";
    @InjectView(R.id.piv_process)
    Process_item_view pivProcess;
    @InjectView(R.id.piv_memory)
    Process_item_view pivMemory;
    @InjectView(R.id.lv_process)
    ListView lvProcess;
    @InjectView(R.id.iv_clean_process)
    ImageView ivCleanProcess;
    @InjectView(R.id.siv_show_sys)
    SettingItemView sivShowSys;
    @InjectView(R.id.bt_select_all)
    Button btSelectAll;
    @InjectView(R.id.bt_select_reverse)
    Button btSelectReverse;
    @InjectView(R.id.siv_clean_auto)
    SettingItemView sivCleanAuto;

    private int runningProcess;
    private List<ProcessInfo> customList;
    private List<ProcessInfo> systemList;
    private myAdapter processAdapter;
    private boolean isShowSys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        ButterKnife.inject(this);

        initTopData();
        initMiddleData();  //用来填充 中间部分 数据

        //取得已经保存的是否显示系统进程状态的值
        boolean isShow = SharePreUtils.getBoolean(this, SHOWSYS, true);
        //给是否显示系统进程的按钮进行初始化操作
        sivShowSys.setOpen(isShow);

        isShowSys = isShow;
        boolean isOpen= ServiceUtil.isRunning(this,ClearProcessService.class);
        sivCleanAuto.setOpen(isShow);
    }

    private void initMiddleData() {
        new Thread() {
            @Override
            public void run() {
                //获取所有集合
                List<ProcessInfo> progressInfos = ProcessProvider.getProgressInfo(ProgressManageActivity.this);
                //创建系统用户进程和用户系统进程
                customList = new ArrayList<>();
                systemList = new ArrayList<>();
                for (ProcessInfo progressInfo : progressInfos) {
                    if (progressInfo.isSys()) {
                        // 添加到系统进程集合中
                        systemList.add(progressInfo);
                    } else {
                        // 添加到用户进程集合中
                        customList.add(progressInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processAdapter = new myAdapter();
                        lvProcess.setAdapter(processAdapter);
                        lvProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                //获取当前点击的条目信息
                                ProcessInfo item = processAdapter.getItem(position);
                                //获取当前的clickbox状态值
                                boolean check = item.isCheck();
                                //将当前的状态取反
                                item.setCheck(!check);
                                //刷新适配器
                                processAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

            }
        }.start();
    }

    @OnClick({R.id.iv_clean_process, R.id.siv_show_sys, R.id.bt_select_all, R.id.bt_select_reverse,R.id.siv_clean_auto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_clean_process:
                // 实现清理 进程的
                // 首先找到需要清理的 条目集合
                List<ProcessInfo> tempList = new ArrayList<>();
                tempList.addAll(customList);
                tempList.addAll(systemList);
                int temapp=0;
                for (ProcessInfo info: tempList) {
                    if (info.isCheck()){
                        if (info.getPackageName().equals(getPackageName())){
                            continue;
                        }if (customList.contains(info)){
                            customList.remove(info);
                            temapp=temapp+1;
                        }else if (systemList.contains(info)){
                            systemList.remove(info);
                            temapp=temapp+1;
                        }
                    }
                    ProcessProvider.killProcess(this,info.getPackageName());
                }
               /* int count2 = processAdapter.getCount();
                for (int i = 0; i < count2; i++) {
                    ProcessInfo item = processAdapter.getItem(i);
                    if (item != null) {
                        if (item.isCheck()) {
                            // 需要被清理的对象  在适配器中被清理出去(不显示)  需要在系统中把该进程杀死
                            if (customList.contains(item)) {
                                customList.remove(item);
                            }
                            if (systemList.contains(item)) {
                                systemList.remove(item);
                            }
                            ProcessProvider.killProcess(this, item.getPackageName());
                        }
                    }
                }
                processAdapter.notifyDataSetChanged();*/
                processAdapter.notifyDataSetChanged();
                initTopData();
                break;
            case R.id.siv_clean_auto:
                boolean isOpen=ServiceUtil.isRunning(this,ClearProcessService.class);
                Intent intent = new Intent(this,ClearProcessService.class);
                if (isOpen){
                    sivCleanAuto.setOpen(false);
                    stopService(intent);
                }else {
                    sivCleanAuto.setOpen(true);
                    startService(intent);
                }

                break;
            case R.id.siv_show_sys:
                //当点击了显示系统进程条目结束后，切换图片的状态
                boolean open = sivShowSys.Open();
                if (open) {//对当前的状态取反
                    //设置一个变量记录当前是需要显示系统进程还是不显示
                    isShowSys = false;
                    //当前状态为绿色，点击后为红色
                    sivShowSys.setOpen(false);
                    SharePreUtils.saveBoolean(this, SHOWSYS, false);
                } else {
                    //当前是关闭状态，需要变换状态切换成开启的状态
                    isShowSys = true;
                    sivShowSys.setOpen(true);
                    SharePreUtils.saveBoolean(this, SHOWSYS, true);
                }
                //刷新适配器
                processAdapter.notifyDataSetChanged();
                break;
            case R.id.bt_select_all:
                // 由于 在适配器中已经实现了判断
                // 把 当前适配器中 要显示的进程条目  都循环一遍,同时把 当前条目的 checkbox 设置为 true
                int count = processAdapter.getCount();
                for (int i = 0; i < count; i++) {
                    ProcessInfo item = processAdapter.getItem(i);
                    if (item != null) {
                        //当前是图片+文本条目，是有checkbox
                        if (getPackageName().equals(item.getPackageName())) {
                            continue;//如果是本应用，需要结束此次循环
                        } else {
                            item.setCheck(true);
                        }
                    }
                }
                //刷新适配器
                processAdapter.notifyDataSetChanged();
                break;
            case R.id.bt_select_reverse:
                int count1 = processAdapter.getCount();
                for (int i = 0; i < count1; i++) {
                    ProcessInfo item = processAdapter.getItem(i);
                    if (item != null) {
                        //当前是图片+文本条目，是有checkbox
                        if (getPackageName().equals(item.getPackageName())) {
                            continue;//如果是本应用，需要结束此次循环
                        } else {
                            item.setCheck(false);
                        }
                    }
                }
                //刷新适配器
                processAdapter.notifyDataSetChanged();
                break;
        }
    }
    class myAdapter extends BaseAdapter {

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == customList.size() + 1) {
                return TEXT_ITEM;//只有文本的条目
            } else {
                //含有文本和图片的条目
                return IMG_TEXT_ITEM;
            }
        }

        @Override
        public int getCount() {
            if (isShowSys) {
                // 用户进程数 + 系统进程数 + 2个显示文本条目
                return customList.size() + systemList.size() + 2;
            } else {
                //不显示系统进程，仅仅显示用户进程
                return customList.size() + 1;
            }

        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 | position == customList.size() + 1) {
                return null; // 返回当前位置 在相应集合的条目信息
            } else if (position < customList.size() + 1) {
                return customList.get(position - 1);// 返回 用户集合中的条目
            } else {
                return systemList.get(position - customList.size() - 2);
            }

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //每个条目所要保存的样式
            if (getItemViewType(position) == TEXT_ITEM) {
                ViewHolderText viewHolderText = new ViewHolderText();
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_process_text, null);
                    viewHolderText.tvDesc = (TextView) convertView.findViewById(R.id.tv_des);
                    convertView.setTag(viewHolderText);
                }
                viewHolderText = (ViewHolderText) convertView.getTag();
                if (position == 0) {
                    viewHolderText.tvDesc.setText("用户进程（" + customList.size() + ")");
                } else {
                    viewHolderText.tvDesc.setText("系统进程(" + systemList.size() + ")");
                }
            } else {
                ViewHolder viewHolder = new ViewHolder();
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_process_text_img, null);
                    viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                    viewHolder.tvUsedMemory = (TextView) convertView.findViewById(R.id.tv_used_memory);
                    viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
                    convertView.setTag(viewHolder);
                }
                viewHolder = (ViewHolder) convertView.getTag();
                ProcessInfo item = getItem(position);
                viewHolder.ivIcon.setImageDrawable(item.getDrawable());
                viewHolder.tvName.setText(item.getName());
                // 在格式化时候,第二个参数应该是 字节为单位的.  把获取的kb 转换为自己,再进行格式化
                String size = Formatter.formatFileSize(getApplicationContext(), item.getIntMemory() * 1024);
                viewHolder.tvUsedMemory.setText(size);
                viewHolder.cb.setChecked(item.isCheck());
                if (getPackageName().equals(item.getPackageName())){
                    viewHolder.cb.setVisibility(View.GONE);
                }else {
                    viewHolder.cb.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }
    }

    // 需要有一个类
    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvUsedMemory;
        CheckBox cb;
    }

    class ViewHolderText {
        TextView tvDesc;
    }

    private void initTopData() {
        pivProcess.setTvLeft("进程数");
        // 取得正在运行的进程数
        new Thread() {
            @Override
            public void run() {
                runningProcess = ProcessProvider.getRunningProcess();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pivProcess.setTvMiddle("正在运行" + runningProcess + "个");
                    }
                });
            }
        }.start();
        // 一共有多少个进程数
        int allProcess = ProcessProvider.getAllProcess(this);
        pivProcess.setTvRight("共有进程数" + allProcess);
        //设置进度条大小
        pivProcess.setProgressBar(runningProcess * 100 / allProcess);


        pivMemory.setTvLeft("内存");
        long runningMemory = ProcessProvider.getRunningMemory(this);
        String runningMem = Formatter.formatFileSize(this, runningMemory);
        pivMemory.setTvMiddle("占用内存" + runningMem);

        long allMemory = ProcessProvider.getAllMemory(this);
        long useMem = allMemory - runningMemory;
        String formatFileSize = Formatter.formatFileSize(this, useMem);
        pivMemory.setTvRight("可用内存" + formatFileSize);
        int bar = (int) (runningMemory * 100 / allMemory);
        pivMemory.setProgressBar(bar);
    }
}
