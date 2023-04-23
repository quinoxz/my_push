package example.android.app.src.main.java.com.push.my_push.my_push_example.common;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public static String post(String url,HashMap<String, String> paramMap){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                        paramMap.toString()))
                .build();

        String result = null;
        try {
            result = okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.i("HttpUtil",result);
        return result;
    }


}
