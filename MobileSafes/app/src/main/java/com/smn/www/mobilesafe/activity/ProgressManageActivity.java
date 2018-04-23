package com.smn.www.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;

import com.smn.www.mobilesafe.R;
import com.smn.www.mobilesafe.engine.ProcessProvider;
import com.smn.www.mobilesafe.view.Process_item_view;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.smn.www.mobilesafe.R.id.piv_process;

/**
 * Created by Administrator on 2018-04-16.
 */

public class ProgressManageActivity extends Activity {
    @InjectView(piv_process)
    Process_item_view pivProcess;
    @InjectView(R.id.piv_memory)
    Process_item_view pivMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        ButterKnife.inject(this);

        //初始化进度条
        initTopData();
    }

    private void initTopData() {
        pivProcess.setTvLeft("进程数");
        // 取得正在运行的进程数
        int runningProcess = ProcessProvider.getRunningProcess(this);
        pivProcess.setTvMiddle("正在运行"+runningProcess+"个");

        // 一共有多少个进程数
        int allProcess = ProcessProvider.getAllProcess(this);
        pivProcess.setTvRight("共有进程数"+allProcess);

        pivMemory.setTvLeft("内存");
        long runningMemory = ProcessProvider.getRunningMemory(this);
        String runningMem = Formatter.formatFileSize(this, runningMemory);
        pivMemory.setTvMiddle("占用内存"+runningMem);
    }

}
