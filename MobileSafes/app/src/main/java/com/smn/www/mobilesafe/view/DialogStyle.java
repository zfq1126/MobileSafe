package com.smn.www.mobilesafe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smn.www.mobilesafe.R;

import com.smn.www.mobilesafe.ShardPreUtils.SharePreUtils;

/**
 * Created by Administrator on 2018-03-24.
 */

public class DialogStyle extends Dialog {

    private int[] drawables=new int[]{R.drawable.shape_alpha_style,R.drawable.shape_orange_style,
    R.drawable.shape_blue_style,R.drawable.shape_gray_style,R.drawable.shape_green_style};
    private String[] desc=new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
    private DialogStyle.myAdapter myAdapter;
    private ListView lvDialog;

    public DialogStyle(Context context) {
        super(context,R.style.myDialogStyle);
    }
    //类似于Acitivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_style);
        //自定义窗口
        Window window=getWindow();
        //得到窗口的属性
        WindowManager.LayoutParams attributes = window.getAttributes();
       //设置属性
        attributes.gravity= Gravity.BOTTOM |Gravity.CENTER_HORIZONTAL;
        //将设置好属性应用到当前窗口
        window.setAttributes(attributes);

        lvDialog = (ListView) findViewById(R.id.lv_dialog);
         myAdapter = new myAdapter();
         lvDialog.setAdapter(myAdapter);
        int drawableDesID=SharePreUtils.getInt(getContext(),"drawableDesID",R.drawable.shape_alpha_style);

        LinearLayout ll= (LinearLayout) findViewById(R.id.ll_dialog_style);
        ll.setBackgroundResource(drawableDesID);

         lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharePreUtils.saveInt(getContext(),"drawableDesID",drawables[position]);
                myAdapter.notifyDataSetChanged();
                dismiss();
            }
        });
    }
    private  class myAdapter extends BaseAdapter{

        private View view;

        @Override
        public int getCount() {
            return desc.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view==null){
                view=View.inflate(getContext(),R.layout.dialog_style_item,null);
            }
            ImageView ivcolor= (ImageView) view.findViewById(R.id.iv_dialog_color);
            TextView tvdesc= (TextView) view.findViewById(R.id.tv_dialog_desc);

            ImageView ivCheck= (ImageView) view.findViewById(R.id.iv_check);
            int  drawableDesID =SharePreUtils.getInt(getContext(),"drawableDesID",R.drawable.shape_alpha_style);
            if (drawableDesID==drawables[position]){
                ivCheck.setVisibility(View.VISIBLE);
            }else {
                ivCheck.setVisibility(View.GONE);
            }
            ivcolor.setImageResource(drawables[position]);
            tvdesc.setText(desc[position]);
            return view;
        }
    }
}
