package com.push.my_push.my_push;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.hihonor.push.sdk.HonorPushCallback;
import com.hihonor.push.sdk.HonorPushClient;
import com.push.my_push.my_push.common.CommonUtil;
import com.push.my_push.my_push.huawei.HuaweiMessageService;
import com.push.my_push.my_push.huawei.HuaweiService;
import com.vivo.push.PushClient;
import com.vivo.push.util.VivoPushException;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.HashMap;
import java.util.Locale;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** MyPushPlugin */
public class MyPushPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;
  private String TAG = "~~~~~MyPushPlugin:~~~~~";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "my_push");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if (call.method.equals("initPush")) {
      initPush(call,result);
    }else if (call.method.equals("setAliasOrToken")) {
      setAliasOrToken(call,result);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }


  public void initPush(MethodCall call,Result result){
    String brand = Build.BRAND.toLowerCase(Locale.getDefault());
    boolean isHarmonyOs = CommonUtil.isHarmonyOs();
    Log.i(TAG, "initPush brand:" + brand + "isHarmonyOs:" + isHarmonyOs);

    HashMap<String, Object> map = call.arguments();
    String alias = map.get("alias").toString();
    if(brand.equals("xiaomi") || brand.equals("redmi")){
      MiPushClient.registerPush(context,map.get("xiaomiAppId").toString(), map.get("xiaomiAppKey").toString());
    }
    if(brand == "vivo"){
      try {
        HashMap hashMap = new HashMap();
        hashMap.put("api_key",map.get("vivoAppKey").toString());
        hashMap.put("app_id",map.get("vivoAppId").toString());
        initManifest(hashMap);
        PushClient.getInstance(context).initialize();
        PushClient.getInstance(context).turnOnPush((int state)->{
          Log.d(TAG, "turnOnPush state= "+ state + "regId" + PushClient.getInstance(context).getRegId());
        });
      } catch (VivoPushException e) {
        throw new RuntimeException(e);
      }
    }
    if(brand.equals("oppo")){
      HeytapPushManager.init(context,true);
      HeytapPushManager.register(context,map.get("oppoAppKey").toString(), map.get("oppoAppSecret").toString(),mPushCallback);
      HeytapPushManager.enableAppNotificationSwitch(state->{
        Log.d(TAG, "oppoenableAppNotificationSwitch state= "+state);
      });
    }
    if(brand.equals("huawei") || (brand.equals("honor") && CommonUtil.isHarmonyOs())){
      HuaweiService.getToken(context,handler);
      HuaweiMessageService huaweiMessageService = new HuaweiMessageService(handler);
    }
    if(brand.equals("honor") && !CommonUtil.isHarmonyOs()){
      boolean isSupport = HonorPushClient.getInstance().checkSupportHonorPush(context);
      if (isSupport) {
        HonorPushClient.getInstance().init(context, true);
        //打开通知栏消息状态
        HonorPushClient.getInstance().turnOnNotificationCenter(new HonorPushCallback<Void>(){
          @Override
          public void onSuccess(Void aVoid) {
            Log.i(TAG,"turnOnNotificationCenter"+aVoid);
          }
          @Override
          public void onFailure(int errorCode,String errorString) {
            Log.i(TAG,"urnOnNotificationCenter onFailure"+errorString);
          }
        });
      }
    }

  }

  public void setAliasOrToken(MethodCall call,Result result){
    String brand = Build.BRAND.toLowerCase(Locale.getDefault());
    boolean isHarmonyOs = CommonUtil.isHarmonyOs();
    Log.i(TAG, "initPush brand:" + brand + "isHarmonyOs:" + isHarmonyOs);

    HashMap<String, Object> map = call.arguments();
    String alias = map.get("alias").toString();
    Log.i(TAG, "setAliasOrToken $brand ${call.arguments} ${CommonUtil.isHarmonyOs()}");
    if(brand == "xiaomi" || brand == "redmi"){
      MiPushClient.setUserAccount(this, call.arguments.toString(), null);
    }
    if(brand == "vivo"){
      PushClient.getInstance(context).bindAlias(alias,(int state)->{
        Log.d(TAG, "bindAlias state= "+ state);
      });
    }
    if(brand == "oppo"){
      HeytapPushManager.register(context, oppo_APP_KEY, oppo_APP_Secret,mPushCallback);
    }
    if(brand == "honor" && !CommonUtil.isHarmonyOs()){
      // 获取PushToken
      HonorPushClient.getInstance().getPushToken(new HonorPushCallback<String>(){
        @Override
        public void onSuccess(String pushToken) {
          Log.i(TAG,"getPushToken pushToken"+pushToken);
          channel.invokeMethod("pushToken",pushToken);
        }
        @Override
        public void onFailure(int errorCode,String errorString) {
          Log.i(TAG,"getPushToken onFailure "+errorString);
        }
      });
    }

  }


  private ICallBackResultService mPushCallback = new ICallBackResultService() {
    @Override
    public void onRegister(int code, String registerId) {
      if (code == 0) {
        showResult("注册成功", "registerId:" + registerId);
        channel.invokeMethod("oppoRegisterId",registerId);
      } else {
        showResult("注册失败", "code=" + code + ",msg=" + registerId);
      }
    }

    @Override
    public void onUnRegister(int code) {
      if (code == 0) {
        showResult("注销成功", "code=" + code);
      } else {
        showResult("注销失败", "code=" + code);
      }
    }

    @Override
    public void onGetPushStatus(final int code, int status) {
      if (code == 0 && status == 0) {
        showResult("Push状态正常", "code=" + code + ",status=" + status);
      } else {
        showResult("Push状态错误", "code=" + code + ",status=" + status);
      }
    }

    @Override
    public void onGetNotificationStatus(final int code, final int status) {
      if (code == 0 && status == 0) {
        showResult("通知状态正常", "code=" + code + ",status=" + status);
      } else {
        showResult("通知状态错误", "code=" + code + ",status=" + status);
      }
    }

    @Override
    public void onError(int i, String s) {
      showResult("onError", "onError code : " + i + "   message : " + s);
    }

    @Override
    public void onSetPushTime(final int code, final String s) {
      showResult("SetPushTime", "code=" + code + ",result:" + s);
    }

    public void showResult(String tag,String msg){
      Log.i(TAG,msg);
    }
  };

  private void initManifest(HashMap hashMap) {
    ApplicationInfo appInfo = null;
    try {
      appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      for(Object item :hashMap.keySet()){
        //String key = appInfo.metaData.getString("com.push.my_push.my_push."+item);
        String value = "";
        if(hashMap.get(item) != null){
          value = hashMap.get(item).toString();
        }
        appInfo.metaData.putString("com.push.my_push.my_push."+item,value);
      }
    }
  }


  @SuppressLint("HandlerLeak")
  public Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      int what = msg.what;
      if (what == 10000) {  //华为 获取token返回
        String token = msg.obj.toString();
        Log.i(TAG,"handleMessage"+token);
        channel.invokeMethod("pushToken",token);
      }
    }
  };

}
