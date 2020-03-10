package pony.xcode.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.util.UUID;

public class SystemUtils {

    /*获取宽高-[0]为屏幕宽度，[1]为屏幕高度*/
    @SuppressWarnings("ConstantConditions")
    public static int[] getScreenSize(Context context) {
        int[] sizes = new int[2];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        sizes[0] = outMetrics.widthPixels;
        sizes[1] = outMetrics.heightPixels;
        return sizes;
    }

    /*获取屏幕宽度*/
    public static int getScreenWidth(Context context) {
        return getScreenSize(context)[0];
    }

    /*获取屏幕高度*/
    public static int getScreenHeight(Context context) {
        return getScreenSize(context)[1];
    }

    // 屏幕宽度（像素）
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    // 屏幕高度（像素）
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }


    /*--------------------------Android中 dp、px、sp的相互转换*****************************/
    public static int dp2px(Context context, int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int px2dp(Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    public static float px2sp(Context context, int pxValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pxValue, context.getResources().getDisplayMetrics());
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        return (int) (spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    public static float sp2px(Context context, int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, context.getResources().getDisplayMetrics());
    }

    private static final String UUID_PREF_NAME = "uuid_pref";

    /*获取设备唯一标识的正确姿势*/
    @SuppressLint("HardwareIds")
    public static String getDeviceUUid(Context context) {
        SharedPreferences sp = context.getSharedPreferences(UUID_PREF_NAME, Context.MODE_PRIVATE);
        String uuid = sp.getString("uuid", "");
        if (TextUtils.isEmpty(uuid)) {
            String androidId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            String id = androidId == null ? "" : androidId;
            UUID deviceUuid = new UUID(id.hashCode(), ((long) id.hashCode() << 32));
            String deviceId = deviceUuid.toString();
            sp.edit().putString("uuid", deviceId).apply();  //持久化保存-保证唯一性
            return deviceId;
        }
        return uuid;
    }

    //判断手机是否为刘海屏手机
    public static boolean isNotchScreen(Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false;
        WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();
        if (windowInsets == null) {
            return false;
        }
        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        if (displayCutout == null) {
            return false;
        } else {
            displayCutout.getBoundingRects();
        }
        return true;
    }

    //获取刘海高度
    public static int getNotchHeight(Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return 0;
        WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();
        if (windowInsets == null) {
            return 0;
        }
        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        if (displayCutout == null) {
            return 0;
        } else {
            displayCutout.getBoundingRects();
        }
        return displayCutout.getSafeInsetTop();
    }
}
