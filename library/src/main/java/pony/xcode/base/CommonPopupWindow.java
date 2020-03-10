package pony.xcode.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.annotation.ColorInt;

import pony.xcode.utils.SystemUtils;

public abstract class CommonPopupWindow extends PopupWindow {

    protected Context mContext;
    protected View mContentView;
    private int mBottomMargin;

    public CommonPopupWindow(Context context) {
        this.mContext = context;
        initPopup();
    }

    private void initPopup() {
        setClippingEnabled(false);
        setFocusable(isFocusable());
        setOutsideTouchable(true);
        mContentView = LayoutInflater.from(mContext).inflate(getLayoutResourceId(), null, false);
        setContentView(mContentView);
        setWidth(getWidth());
        setHeight(getHeight());
        setBackgroundDrawable(new ColorDrawable(getColorDrawable()));
    }

    protected abstract int getLayoutResourceId();

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public int getWidth() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int getHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @ColorInt
    protected int getColorDrawable() {
        return Color.TRANSPARENT;
    }

    public int getBottomMargin() {
        return mBottomMargin;
    }

    public void setBottomMargin(int bottomMargin) {
        this.mBottomMargin = bottomMargin;
    }

    @Override
    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        showAsDropDown(anchor, xoff, yoff, Gravity.NO_GRAVITY);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = getSupportHeight() - visibleFrame.bottom - getBottomMargin();  //屏幕高度减去 anchor 的 bottom
            setHeight(height);
        }
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    private int getSupportHeight() {
        int height = SystemUtils.getScreenHeight(mContext);
        height += SystemUtils.getNotchHeight(((Activity) mContext).getWindow());
        return height;
    }

    protected void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }
}
