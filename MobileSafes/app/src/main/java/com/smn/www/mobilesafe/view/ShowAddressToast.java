package com.smn.www.mobilesafe.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smn.www.mobilesafe.R;

import com.smn.www.mobilesafe.ShardPreUtils.SharePreUtils;

/**
 * Created by Administrator on 2018-03-30.
 */

public class ShowAddressToast {
    private Context mCtx;
    private WindowManager mWM;
    private View mView;
    public ShowAddressToast(Context ctx){
        this.mCtx=ctx;
        mWM= (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);
    }
    public void ShowToast(String address){
         hideToast();
        mView=View.inflate(mCtx, R.layout.show_address_toast,null);
        // 找到 显示归属地 布局的 根布局
        RelativeLayout rlLocation= (RelativeLayout) mView.findViewById(R.id.rl_location);
        // 找到 之前设置的 归属地风格
        int drawableDesID =SharePreUtils.getInt(mCtx,"drawableDesID",R.drawable.shape_alpha_style);
        // 找到 之前设置的 归属地风格
        rlLocation.setBackgroundResource(drawableDesID);
        TextView tvLocation= (TextView) mView.findViewById(R.id.tv_location);
        tvLocation.setText(address);

        WindowManager.LayoutParams params=new WindowManager.LayoutParams();;
        params.height=WindowManager.LayoutParams.WRAP_CONTENT;
        params.width=WindowManager.LayoutParams.WRAP_CONTENT;
        params.format= PixelFormat.TRANSPARENT;
        params.type=WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWM.addView(mView,params);
    }

    public void hideToast() {
        if (mView!=null){
            if (mView.getParent()!=null){
                mWM.removeView(mView);
            }
        }
    }
}
