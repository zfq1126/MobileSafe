package com.smn.www.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smn.www.mobilesafe.R;

/**
 * Created by Administrator on 2018-03-09.
 */

public class SettingItemView extends RelativeLayout
{
    private TextView  tv_textView;
    private ImageView iv_imageView;
    private  boolean isOpen=true;

    public SettingItemView(Context context) {
        this(context,null);
    }
    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitViw();
        //初始化自定义的参数
        InitAttrs(attrs);
    }

    private void InitAttrs(AttributeSet attrs) {
        boolean isToggle=attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res-auto","isToggle",true);
        String title= attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","title");
        tv_textView.setText(title);
        if (isToggle){
            //设置为可见
            iv_imageView.setVisibility(View.VISIBLE);
        }else{
            //设置为不可见
            iv_imageView.setVisibility(View.GONE);
        }
    }
    private void InitViw() {
        View view=View.inflate(getContext(), R.layout.setting_item_view,null);
        addView(view);
        //将局部变量变成成员变量alt + ctrl +F
        tv_textView= (TextView) view.findViewById(R.id.tv_title);
        iv_imageView=(ImageView)view.findViewById(R.id.iv_toggle);
    }
    //通过这个方法可以取出当前的默认值
    public boolean Open(){

        return isOpen;
    }

    public void setOpen(boolean b){

        if (b) {
            this.isOpen=b;
            //若为true 则为绿色
            iv_imageView.setImageResource(R.mipmap.on);
          }else{
            this.isOpen=b;
            iv_imageView.setImageResource(R.mipmap.off);
        }
        }
    }