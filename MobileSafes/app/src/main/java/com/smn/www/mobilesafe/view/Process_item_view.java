package com.smn.www.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smn.www.mobilesafe.R;

/**
 * Created by Administrator on 2018-04-23.
 */

public class Process_item_view extends LinearLayout {
    private TextView tvLeft;
    private TextView tvMiddle;
    private TextView tvRight;
    private ProgressBar progressBar;

    public Process_item_view(Context context) {
        // 如果 是 一个 参数,让他调用 两个参数的 构造函数
        this(context,null);
    }

    public Process_item_view(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
        setOrientation(VERTICAL);
    }

    public Process_item_view(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化 数据
        initData();
    }

    private void initData() {
        View  view = View.inflate(getContext(), R.layout.process_item_progress,null);
        tvLeft = (TextView) view.findViewById(R.id.tv_left);
        // alt + ctrl + F  将 局部变量转换为类的成员变量 快捷键
        tvMiddle = (TextView) view.findViewById(R.id.tv_middle);
        tvRight = (TextView) view.findViewById(R.id.tv_right);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        // 将转换后的 view 添加到自定义控件当中
        this.addView(view);
    }

    public void setTvLeft(String leftString) {
        // 在调用自定义控件的时候, 根据传递过来的字符串内容 给相应的控件赋值
        tvLeft.setText(leftString);
    }

    public void setTvMiddle(String middleString) {
        tvMiddle.setText(middleString);
    }

    public void setTvRight(String rightString) {
        tvRight.setText(rightString);
    }

    public void setProgressBar(int pb) {
        progressBar.setProgress(pb);
    }
}
