package com.smn.www.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smn.www.mobilesafe.R;
import com.smn.www.mobilesafe.bean.ProcessInfo;
import com.smn.www.mobilesafe.engine.ProcessProvider;
import com.smn.www.mobilesafe.view.Process_item_view;
import com.smn.www.mobilesafe.view.SettingItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.smn.www.mobilesafe.R.id.piv_process;

/**
 * Created by Administrator on 2018-04-16.
 */

public class ProgressManageActivity extends Activity {
    private static final int TEXT_ITEM = 0;
    private static final int IMG_TEXT_ITEM = 1;
    @InjectView(piv_process)
    Process_item_view pivProcess;
    @InjectView(R.id.piv_memory)
    Process_item_view pivMemory;
    @InjectView(R.id.lv_process)
    ListView lvProcess;
    @InjectView(R.id.siv_show_sys)
    SettingItemView sivShowSys;
    @InjectView(R.id.siv_clean_auto)
    SettingItemView sivCleanAuto;
    @InjectView(R.id.bt_select_all)
    Button btSelectAll;
    @InjectView(R.id.bt_select_reverse)
    Button btSelectReverse;
    private int runningProcess;
    private List<ProcessInfo> customList;
    private List<ProcessInfo> systemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        ButterKnife.inject(this);

        initTopData();
        initMiddleData();  //用来填充 中间部分 数据
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
                lvProcess.setAdapter(new myAdapter());
            }
        }.start();
    }

  /*  @OnClick({R.id.siv_show_sys, R.id.siv_clean_auto, R.id.bt_select_all, R.id.bt_select_reverse})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.siv_show_sys:
                break;
            case R.id.siv_clean_auto:
                break;
            case R.id.bt_select_all:
                break;
            case R.id.bt_select_reverse:
                break;
        }
    }*/

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
            // 用户进程数 + 系统进程数 + 2个显示文本条目
            return customList.size() + systemList.size() + 2;
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
                pivProcess.setTvMiddle("正在运行" + runningProcess + "个");
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
