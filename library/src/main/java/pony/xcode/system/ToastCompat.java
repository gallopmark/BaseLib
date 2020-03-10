package pony.xcode.system;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.view.ViewCompat;

import java.lang.reflect.Field;

import pony.xcode.base.R;

public class ToastCompat {

    /*自定义toast动画*/
    class AnimationToast extends Toast {

        @SuppressWarnings("JavaReflectionMemberAccess")
        AnimationToast(Context context, int windowAnimations) {
            super(context);
            Class<Toast> clazz = Toast.class;
            try {
                Field field = clazz.getDeclaredField("mTN");
                field.setAccessible(true);
                Object obj = field.get(this);
                if (obj != null) {
                    // 取消掉各个系统的默认toast弹出动画
                    Field fieldParam = obj.getClass().getDeclaredField("mParams");
                    field.setAccessible(true);
                    Object params = fieldParam.get(obj);
                    if (params == null) return;
                    if (params instanceof WindowManager.LayoutParams) {
                        WindowManager.LayoutParams wl = (WindowManager.LayoutParams) params;
                        wl.windowAnimations = windowAnimations;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Context mContext;
    private Toast mToast;
    private View mContentView;
    private TextView mToastTextView;
    private int mDuration = Toast.LENGTH_SHORT;
    private int mGravity = -1;
    private float mHorizontalMargin = -1, mVerticalMargin = -1;
    private int mWindowAnimations = R.style.ToastCompatStyle; //默认toast动画 淡入淡出

    private ToastCompat(Context context) {
        this.mContext = context;
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.layout_android_toast_compat, new LinearLayout(mContext), false);
        mToastTextView = mContentView.findViewById(R.id.tv_toast);
    }

    public static ToastCompat from(Context context) {
        return new ToastCompat(context);
    }

    public static ToastCompat makeText(Context context, CharSequence text, int duration) {
        ToastCompat toast = new ToastCompat(context);
        toast.with(text).setDuration(duration);
        return toast;
    }

    public static ToastCompat makeText(Context context, @StringRes int resId, int duration) {
        ToastCompat toast = new ToastCompat(context);
        toast.with(resId).setDuration(duration);
        return toast;
    }

    public FluentInitializer with(@StringRes int resId) {
        return new FluentInitializer(resId);
    }

    public FluentInitializer with(CharSequence text) {
        return new FluentInitializer(text);
    }

    public class FluentInitializer {

        FluentInitializer(@StringRes int resId) {
            this(mContext.getString(resId));
        }

        /*构造方法带toast文本*/
        FluentInitializer(CharSequence text) {
            mToastTextView.setText(text);
        }

        /*设置toast动画*/
        public FluentInitializer setWindowAnimations(int windowAnimations) {
            mWindowAnimations = windowAnimations;
            return this;
        }

        /*设置toast文本边距*/
        public FluentInitializer setContentPadding(int left, int top, int right, int bottom) {
            mContentView.setPadding(left, top, right, bottom);
            return this;
        }

        public FluentInitializer setBackground(Drawable background) {
            ViewCompat.setBackground(mContentView, background);
            return this;
        }

        /*设置toast窗口背景颜色*/
        public FluentInitializer setBackgroundColor(@ColorInt int color) {
            mContentView.setBackgroundColor(color);
            return this;
        }

        /*设置toast窗口背景*/
        public FluentInitializer setBackgroundResource(@DrawableRes int resId) {
            mContentView.setBackgroundResource(resId);
            return this;
        }

        /*设置文本大小*/
        public FluentInitializer setTextSize(float size) {
            mToastTextView.setTextSize(size);
            return this;
        }

        /*TypedValue*/
        public FluentInitializer setTextSize(int unit, float size) {
            mToastTextView.setTextSize(unit, size);
            return this;
        }

        /*设置文本颜色*/
        public FluentInitializer setTextColor(@ColorInt int color) {
            mToastTextView.setTextColor(color);
            return this;
        }

        /*设置toast时长*/
        public FluentInitializer setDuration(int duration) {
            mDuration = duration;
            return this;
        }

        /*设置toast gravity*/
        public FluentInitializer setGravity(int gravity) {
            mGravity = gravity;
//            mToast.setGravity(gravity, 0, 0);
            return this;
        }

        /*设置toast margin*/
        public FluentInitializer setMargin(float horizontalMargin, float verticalMargin) {
            mHorizontalMargin = horizontalMargin;
            mVerticalMargin = verticalMargin;
            return this;
        }

        public void show() {
            ToastCompat.this.show();
        }
    }

    /*先判断toast知否为null，不为null则取消toast并设为null*/
    public void show() {
        mToast = new AnimationToast(mContext, mWindowAnimations);  /*new Toast创建toast 没有设置view会报错*/
        mToast.setView(mContentView);
        mToast.setDuration(mDuration);  //默认toast时间
        if (mGravity != -1) {
            mToast.setGravity(mGravity, 0, 0);
        }
        if (mHorizontalMargin != -1 || mVerticalMargin != -1) {
            mToast.setMargin(mHorizontalMargin, mVerticalMargin);
        }
        mToast.show();
    }

    public void cancel() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
