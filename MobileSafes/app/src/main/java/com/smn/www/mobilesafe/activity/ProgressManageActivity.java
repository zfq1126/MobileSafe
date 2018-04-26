package com.smn.www.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;

import com.smn.www.mobilesafe.R;
import com.smn.www.mobilesafe.bean.ProcessInfo;
import com.smn.www.mobilesafe.engine.ProcessProvider;
import com.smn.www.mobilesafe.view.Process_item_view;

import java.util.List;

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
    private int runningProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        ButterKnife.inject(this);

        initTopData();
        initMiddleData();  //用来填充 中间部分 数据
    }

    private void initMiddleData() {
        new Thread(){
            @Override
            public void run() {
                List<ProcessInfo> progressInfo = ProcessProvider.getProgressInfo(ProgressManageActivity.this);
            }
        }.start();
    }

    private void initTopData() {
        pivProcess.setTvLeft("进程数");
        // 取得正在运行的进程数
        new Thread(){
            @Override
            public void run() {
                runningProcess = ProcessProvider.getRunningProcess();
                pivProcess.setTvMiddle("正在运行"+ runningProcess +"个");
            }
        }.start();
        // 一共有多少个进程数
        int allProcess = ProcessProvider.getAllProcess(this);
        pivProcess.setTvRight("共有进程数"+allProcess);
        //设置进度条大小
        pivProcess.setProgressBar(runningProcess*100/allProcess);


        pivMemory.setTvLeft("内存");
        long runningMemory = ProcessProvider.getRunningMemory(this);
        String runningMem = Formatter.formatFileSize(this, runningMemory);
        pivMemory.setTvMiddle("占用内存"+runningMem);

        long allMemory=ProcessProvider.getAllMemory(this);
        long useMem = allMemory - runningMemory;
        String formatFileSize = Formatter.formatFileSize(this, useMem);
        pivMemory.setTvRight("可用内存"+formatFileSize);
        int bar=(int)(allMemory*100/useMem);
        pivMemory.setProgressBar(bar);
    }

}
