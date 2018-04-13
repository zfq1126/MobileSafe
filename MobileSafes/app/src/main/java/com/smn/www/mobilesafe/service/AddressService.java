package com.smn.www.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.smn.www.mobilesafe.db.AddressDao;
import com.smn.www.mobilesafe.view.ShowAddressToast;


/**
 * Created by Administrator on 2018-03-20.
 */

public class AddressService extends Service {

    private ShowAddressToast showAddressToast;
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("AddressService","服务绑定");
        return null;
    }

    @Override
    public void onCreate() {

        TelephonyManager mTM= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        myPhoneStateLinstener myPhoneStateLinstener = new myPhoneStateLinstener();

        mTM.listen(myPhoneStateLinstener,PhoneStateListener.LISTEN_CALL_STATE);

        showAddressToast = new ShowAddressToast(getApplicationContext());
        Log.i("AddressService","已经开启服务");
        super.onCreate();
    }

    class myPhoneStateLinstener extends PhoneStateListener{
        /**
         *
         * @param state    监听电话状态
         * @param incomingNumber  监听到的 接收到的电话号码
         */
        public void onCallStateChanged(int state, String incomingNumber) {
            // 监听 呼叫状态 变化的 方法
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:  //电话处于空闲状态
                    showAddressToast.hideToast();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:  //电话处于响铃状态
                    Log.i("aaaaa",incomingNumber);

                    //Toast.makeText(getApplicationContext(),"当前拨打的电话号码"+incomingNumber,Toast.LENGTH_LONG).show();
                    String address = AddressDao.getAddress(getApplicationContext(), incomingNumber);
                    showAddressToast.ShowToast(address);
                    Log.i("AddressService",address);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:  //电话处于接通状态
                   showAddressToast.hideToast();
                    break;
            }



            super.onCallStateChanged(state, incomingNumber);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AddressService","服务已经绑定了。");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("AddressService","服务已经销毁了");
        super.onDestroy();
    }
}
