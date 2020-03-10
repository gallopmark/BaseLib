package pony.xcode.system;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class SystemTintHelper {
    private static final String TAG_STATUS_BAR = "TAG_STATUS_BAR";

    public static void setStatusBarLightMode(Activity activity) {
        setStatusBarLightMode(activity.getWindow());
    }

    private static void setStatusBarLightMode(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * @param activity 当前activity
     * @param colorId  颜色值
     */
    public static void setStatusBarColor(Activity activity, @ColorInt int colorId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上
            Window window = activity.getWindow();
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(colorId);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            //让view不根据系统窗口来调整自己的布局
            ViewGroup contentView = window.findViewById(Window.ID_ANDROID_CONTENT);
            View childView;
            if (contentView != null && (childView = contentView.getChildAt(0)) != null) {
                childView.setFitsSystemWindows(true);
                childView.requestApplyInsets();
            }
        } else {//4.4-5.0  setContentView之后调用才有效
            applyStatusBarColor(activity, colorId);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void applyStatusBarColor(Activity activity, @ColorInt int colorId) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(attributes);
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        // 生成一个状态栏大小的矩形
        View statusView = decorView.findViewWithTag(TAG_STATUS_BAR);
        if (statusView == null) {
            statusView = createStatusBarView(activity);
            // 添加 statusView 到布局中
            decorView.addView(statusView);
        }
        statusView.setBackgroundColor(colorId);
        // 设置根布局的参数
        ViewGroup contentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        if (contentView != null) {
            ViewGroup childView = (ViewGroup) contentView.getChildAt(0);
            if (childView != null) {
                childView.setFitsSystemWindows(true);
                childView.setClipToPadding(true);
            }
        }
    }

    private static View createStatusBarView(final Context context) {
        View statusBarView = new View(context);
        statusBarView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(context)));
        statusBarView.setTag(TAG_STATUS_BAR);
        return statusBarView;
    }

    /*获取状态栏高度*/
    public static int getStatusBarHeight(Context context) {
        try {
            Resources resources = context.getResources();
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            return resources.getDimensionPixelSize(resourceId);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void setStatusViewColor(Context context, View statusView, @ColorInt int colorId) {
        if (statusView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = statusView.getLayoutParams();
            params.height = getStatusBarHeight(context);
            if (colorId != 0) {
                statusView.setBackgroundColor(colorId);
            }
        }
    }

    //设置全屏  
    public static void setFullscreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /*取消全屏*/
    public static void clearFullscreen(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /*全屏透明沉浸状态栏*/
    public static void setTransparentStatusBar(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else {
                //让contentView延伸到状态栏并且设置状态栏颜色透明
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * Subtract the top margin size equals status bar's height for view.
     * 当设置全屏沉浸状态栏时，一般使用此方法将toolbar等设置topMargin为statusHeight
     *
     * @param view The view.
     */
    public static void addMarginTopEqualStatusBarHeight(Context context, @NonNull View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin,
                layoutParams.topMargin + getStatusBarHeight(context),
                layoutParams.rightMargin,
                layoutParams.bottomMargin);
    }

    /*与上述方法效果相反*/
    public static void subtractMarginTopEqualStatusBarHeight(Context context, @NonNull View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin,
                layoutParams.topMargin - getStatusBarHeight(context),
                layoutParams.rightMargin,
                layoutParams.bottomMargin);
    }
}
