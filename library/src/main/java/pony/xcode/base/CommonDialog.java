package pony.xcode.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;



/*dialog基础类*/
public abstract class CommonDialog extends Dialog {
    protected Context mContext;
    protected View mContentView;
    private int mWidth = FrameLayout.LayoutParams.WRAP_CONTENT,
            mHeight = FrameLayout.LayoutParams.WRAP_CONTENT; //默认height为自动填充
    private int mWindowAnimations = -1;
    private Drawable mBackground; //窗口背景drawable
    private int mGravity = Gravity.CENTER;  //默认弹窗位置为屏幕中央

    public CommonDialog(Context context) {
        this(context, R.style.CommonDialogStyle);
    }

    public CommonDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup.LayoutParams params;
        if (getLayoutParams() != null) {
            params = getLayoutParams();
            setWidth(getLayoutParams().width);
            setHeight(getLayoutParams().height);
        } else {
            params = new FrameLayout.LayoutParams(getWidth(), getHeight());
        }
        mContentView = LayoutInflater.from(mContext).inflate(getLayoutResourceId(), null, false);
        initView(mContentView);
        setContentView(mContentView, params);
        Window window = getWindow();
        if (window != null) {
            //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            renderWindow(window);
        }
    }

    /*获取屏幕宽度*/
    protected int getScreenWidth() {
        return mContext.getResources().getDisplayMetrics().widthPixels;
    }

    /*获取屏幕高度*/
    protected int getScreenHeight() {
        return mContext.getResources().getDisplayMetrics().heightPixels;
    }

    protected int getCommonWidth() {
        return getScreenWidth() - mContext.getResources().getDimensionPixelSize(R.dimen.common_dialog_horizontal_margin) * 2;
    }

    protected int getCommonHeight() {
        return getScreenHeight() - mContext.getResources().getDimensionPixelSize(R.dimen.common_dialog_vertical_margin) * 2;
    }

    public View getContentView() {
        return mContentView;
    }

    /*子类可以重写此方法自定义宽度*/
    public int getWidth() {
        return mWidth;
    }

    /*窗口宽度默认左右边距30dp*/
    public void setWidth(int width) {
        this.mWidth = width;
    }

    /*子类可以重写此方法自定义高度*/
    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    /*子类可以重写此方法自定义窗口动画*/
    public int getWindowAnimations() {
        return mWindowAnimations;
    }

    /*对话框弹出动画*/
    public void setWindowAnimations(@StyleRes int windowAnimations) {
        this.mWindowAnimations = windowAnimations;
    }

    public void setBackgroundDrawable(Drawable background) {
        this.mBackground = background;
    }

    /*重写此方法可以设置窗口background*/
    public Drawable getBackgroundDrawable() {
        return mBackground;
    }

    public void setBackgroundDrawableResource(@DrawableRes int resId) {
        this.mBackground = ContextCompat.getDrawable(mContext, resId);
    }

    @Nullable
    public ViewGroup.MarginLayoutParams getLayoutParams() {
        return null;
    }

    /*重写此方法可以设置窗口弹出位置*/
    public int getGravity() {
        return mGravity;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    protected void renderWindow(Window window) {
        if (isFullScreen()) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        } else {
            if (fullWidth()) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, getHeight());
            }
        }
        if (getWindowAnimations() != -1) {
            window.setWindowAnimations(getWindowAnimations());
        }
        if (getBackgroundDrawable() != null) {
            window.setBackgroundDrawable(getBackgroundDrawable());
        }
        if (getGravity() != -1) {
            window.setGravity(getGravity());
        }
    }

    protected abstract int getLayoutResourceId();

    protected void initView(View contentView) {
    }

    /*是否全屏显示*/
    protected boolean isFullScreen() {
        return false;
    }

    protected boolean fullWidth() {
        return false;
    }
}
