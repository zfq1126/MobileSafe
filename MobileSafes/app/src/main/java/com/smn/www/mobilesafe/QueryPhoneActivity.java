package com.smn.www.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smn.www.mobilesafe.db.AddressDao;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018-03-16.
 * 此activity 创建的日期
 */
public class QueryPhoneActivity extends Activity {
    @InjectView(R.id.et_phonenumber)
    EditText etPhonenumber;
    @InjectView(R.id.bt_query_phonenumber)
    Button btQueryPhonenumber;
    @InjectView(R.id.tv_show_phonenumber)
    TextView tvShowPhonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queryphone_activity);
        ButterKnife.inject(this);
        etPhonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //文本输入完成时候进行查询
                String number = etPhonenumber.getText().toString();
                if (!TextUtils.isEmpty(number)){
                    String address = AddressDao.getAddress(QueryPhoneActivity.this,number);
                    tvShowPhonenumber.setText(address);
                }

            }
        });
    }

    @OnClick(R.id.bt_query_phonenumber)
    public void onViewClicked() {
        String number = etPhonenumber.getText().toString();
        if(!TextUtils.isEmpty(number)){
            String address = AddressDao.getAddress(this,number);
            tvShowPhonenumber.setText(address);
        }else {
            Toast.makeText(this,"请输入手机号码",Toast.LENGTH_SHORT).show();
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            etPhonenumber.startAnimation(animation);
        }
    }
}
