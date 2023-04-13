package com.push.my_push.my_push.huawei;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;

public class HuaweiService{

    public static String TAG = "huaweiService";

    public static void getToken(Context context, Handler handler){
        new Thread(){
            @Override
            public void run() {
                try {
                    // 从agconnect-services.json文件中读取APP_ID
                    String appId = "107659857";
                    // 输入token标识"HCM"
                    String tokenScope = "HCM";
                    String token = HmsInstanceId.getInstance(context).getToken(appId, tokenScope);
                    Log.i(TAG, "hauwei get token: " + token);
                    // 判断token是否为空
                    if(!TextUtils.isEmpty(token)) {
                        Message message = Message.obtain();
                        message.what = 10000;
                        message.obj = token;
                        handler.sendMessage(message);
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
    }




}
