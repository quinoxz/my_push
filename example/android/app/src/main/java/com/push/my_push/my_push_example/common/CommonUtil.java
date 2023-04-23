package example.android.app.src.main.java.com.push.my_push.my_push_example.common;

public class CommonUtil {

    public static boolean isHarmonyOs(){
        try {
            Class<?> buildExClass = Class.forName("com.huawei.system.BuildEx");
            Object osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass);
            return "harmony".equalsIgnoreCase(osBrand.toString());
        }catch (Throwable e){
            return false;
        }
    }
}
