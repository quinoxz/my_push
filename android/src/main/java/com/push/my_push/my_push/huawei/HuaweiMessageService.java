package com.push.my_push.my_push.huawei;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;

public class HuaweiMessageService extends HmsMessageService {

    public static String TAG = "HuaweiMessageService";
    public Handler handler;

    public HuaweiMessageService(Handler handler){
        this.handler = handler;
    }

    @Override
    public void onNewToken(String token, Bundle bundle) {
        super.onNewToken(token, bundle);
        // 获取token
        Log.i(TAG, "have received refresh token " + token);
        // 判断token是否为空
        if (!TextUtils.isEmpty(token)) {
            Message message = Message.obtain();
            message.what = 10000;
            message.obj = token;
            handler.sendMessage(message);
        }
    }
}
