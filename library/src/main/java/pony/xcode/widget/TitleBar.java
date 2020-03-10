package pony.xcode.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.TextViewCompat;

import java.lang.reflect.Field;

import pony.xcode.base.R;

/*标题居中toolbar*/
public class TitleBar extends Toolbar {
    private TextView mTitleTextView;  //title textView
    private CharSequence mTitleText;    //title text
    private int mTitleTextAppearance;
    private int mTitleTextSize; //标题文本大小
    private int mTitleTextColor; //标题文本颜色
    private int mTitleTextStyle = -1;  //标题文本风格

    public static final int NORMAL = 1;
    public static final int ITALIC = 2;
    public static final int BOLD = 3;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttribute(context, attrs);
    }

    private void resolveAttribute(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        final int titleTextSize = ta.getDimensionPixelSize(R.styleable.TitleBar_tb_titleTextSize, defaultTextSize(context));
        setTitleTextSize(titleTextSize);
        final int titleTextColor = ta.getColor(R.styleable.TitleBar_tb_titleTextColor, Color.parseColor("#222222"));
        setTitleTextColor(titleTextColor);
        final int titleTextStyle = ta.getInt(R.styleable.TitleBar_tb_titleTextStyle, 0);
        setTitleTextStyle(titleTextStyle);
        final int titleTextAppearance = ta.getResourceId(R.styleable.TitleBar_tb_titleAppearance, 0);
        setTitleTextAppearance(titleTextAppearance);
        final CharSequence titleText = ta.getText(R.styleable.TitleBar_tb_titleText);
        if (!TextUtils.isEmpty(titleText)) {
            setTitle(titleText);
        }
        ta.recycle();
    }

    /*默认字体大小 16sp*/
    private int defaultTextSize(Context context) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (16 * scale + 0.5f);
    }

    @Override
    public CharSequence getTitle() {
        return mTitleText;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            if (mTitleTextView == null) {
                mTitleTextView = new TextView(getContext());
                mTitleTextView.setSingleLine();
                mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
                if (obtainTitleTextStyle() != 0) {
                    setTextStyle(obtainTitleTextStyle());
                }
                if (obtainTitleTextAppearance() != 0) {
                    TextViewCompat.setTextAppearance(mTitleTextView, obtainTitleTextAppearance());
                }
                if (obtainTitleTextColor() != 0) {
                    mTitleTextView.setTextColor(obtainTitleTextColor());
                }
            }
            if (mTitleTextView.getParent() != this) {
                ensureTitleView(mTitleTextView);
            }
        } else if (mTitleTextView != null && mTitleTextView.getParent() == this) {// 当title为空时，remove
            removeView(mTitleTextView);
        }
        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
        mTitleText = title;
    }

    private void ensureTitleView(View v) {
        ViewGroup.LayoutParams vlp = v.getLayoutParams();
        LayoutParams lp;
        if (vlp == null) {
            lp = generateDefaultLayoutParams();
        } else if (!checkLayoutParams(vlp)) {
            lp = generateLayoutParams(vlp);
        } else {
            lp = (LayoutParams) vlp;
        }
        lp.gravity = Gravity.CENTER;
        lp.setMargins(getTitleMarginStart(), getTitleMarginTop(), getTitleMarginEnd(), getTitleMarginBottom());
        addView(v, lp);
    }

    public void setTitleTextSize(int titleTextSize) {
        if (mTitleTextView != null) {
            mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        }
        this.mTitleTextSize = titleTextSize;
    }

    public int obtainTitleTextSize() {
        return mTitleTextSize;
    }

    @Override
    public void setTitleTextColor(int titleTextColor) {
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(titleTextColor);
        }
        this.mTitleTextColor = titleTextColor;
    }

    public int obtainTitleTextColor() {
        return mTitleTextColor;
    }

    public void setTitleTextStyle(int titleTextStyle) {
        if (mTitleTextView != null) {
            setTextStyle(titleTextStyle);
        }
        this.mTitleTextStyle = titleTextStyle;
    }

    private void setTextStyle(int titleTextStyle) {
        if (titleTextStyle == NORMAL) {
            mTitleTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        } else if (titleTextStyle == ITALIC) {
            mTitleTextView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        } else if (titleTextStyle == BOLD) {
            mTitleTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            mTitleTextView.setTypeface(Typeface.DEFAULT);
        }
    }

    public int obtainTitleTextStyle() {
        return mTitleTextStyle;
    }

    public void setTitleTypeface(Typeface typeface) {
        if (mTitleTextView != null) {
            mTitleTextView.setTypeface(typeface);
        }
    }

    public void setTitleTextAppearance(int titleTextAppearance) {
        if (mTitleTextView != null) {
            TextViewCompat.setTextAppearance(mTitleTextView, titleTextAppearance);
        }
        this.mTitleTextAppearance = titleTextAppearance;
    }

    public int obtainTitleTextAppearance() {
        return mTitleTextAppearance;
    }

    @Override
    public void setTitleTextAppearance(Context context, int resId) {
        if (mTitleTextView != null) {
            TextViewCompat.setTextAppearance(mTitleTextView, resId);
        }
    }

    @Nullable
    public TextView obtainTitleTextView() {
        return mTitleTextView;
    }

    @Override
    public void setNavigationIcon(@Nullable Drawable icon) {
        super.setNavigationIcon(icon);
        ensureCenterView("mNavButtonView");  //设置返回键居中
    }

    @Override
    public void inflateMenu(int resId) {
        super.inflateMenu(resId);
        ensureMenuView();
    }

    private void ensureMenuView() {
        ensureCenterView("mMenuView");
        /*取消menu长按显示Toast*/
        for (int i = 0; i < getMenu().size(); i++) {
            getMenu().getItem(i).setTitle(null);
        }
    }

    protected void ensureCenterView(String fieldName) {
        Object obj = getField(fieldName);//拿到对应的Object
        if (obj == null) return;
        if (obj instanceof View) {
            ViewGroup.LayoutParams lp = ((View) obj).getLayoutParams();//拿到LayoutParams
            if (lp instanceof ActionBar.LayoutParams) {
                ((ActionBar.LayoutParams) lp).gravity = Gravity.CENTER;//设置居中
                ((View) obj).setLayoutParams(lp);
            }
        }
    }

    /*通过反射获取属性值*/
    @Nullable
    protected Object getField(@NonNull String fieldName) {
        try {
            Class<?> superclass = getClass().getSuperclass();
            if (superclass == null) return null;
            Field field = superclass.getDeclaredField(fieldName);//反射得到父类Field
            field.setAccessible(true);
            return field.get(this);
        } catch (Exception e) {
            return null;
        }
    }
}
