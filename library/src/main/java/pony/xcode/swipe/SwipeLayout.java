package pony.xcode.swipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import androidx.core.view.ViewCompat;

import pony.xcode.base.R;

public class SwipeLayout extends ViewGroup {
    enum Mode {
        RESET, DRAG, FLING, CLICK
    }

    private Mode mTouchMode;
    private View mMainItemView;
    private boolean mInLayout = false;
    private int mScrollOffset;
    private int mMaxScrollOffset;
    private ScrollRunnable mScrollRunnable;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchMode = Mode.RESET;
        mScrollOffset = 0;
        mScrollRunnable = new ScrollRunnable(context);
    }

    public int getScrollOffset() {
        return mScrollOffset;
    }

    public void open() {
        if (mScrollOffset != -mMaxScrollOffset) {
            if (mTouchMode == Mode.FLING)
                mScrollRunnable.abort();
            mScrollRunnable.startScroll(mScrollOffset, -mMaxScrollOffset);
        }
    }

    public void close() {
        if (mScrollOffset != 0) {
            if (mTouchMode == Mode.FLING)
                mScrollRunnable.abort();
            mScrollRunnable.startScroll(mScrollOffset, 0);
        }
    }

    void fling(int xVel) {
        mScrollRunnable.startFling(mScrollOffset, xVel);
    }

    void revise() {
        if (mScrollOffset < -mMaxScrollOffset / 2)
            open();
        else
            close();
    }

    private void ensureChildren() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            ViewGroup.LayoutParams tempLp = childView.getLayoutParams();
            if (!(tempLp instanceof LayoutParams))
                throw new IllegalStateException("缺少layout参数");
            LayoutParams lp = (LayoutParams) tempLp;
            if (lp.itemType == LayoutParams.TYPE_MAIN) {
                mMainItemView = childView;
            }
        }
        if (mMainItemView == null)
            throw new IllegalStateException("main item不能为空");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //确定children
        ensureChildren();
        //先测量main
        LayoutParams lp = (LayoutParams) mMainItemView.getLayoutParams();
        measureChildWithMargins(mMainItemView,
                widthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                heightMeasureSpec, getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(mMainItemView.getMeasuredWidth() + getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
                , mMainItemView.getMeasuredHeight() + getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin);
        //测试menu
        int menuWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int menuHeightSpec = MeasureSpec.makeMeasureSpec(mMainItemView.getMeasuredHeight(), MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View menuView = getChildAt(i);
            lp = (LayoutParams) menuView.getLayoutParams();
            if (lp.itemType == LayoutParams.TYPE_MAIN)
                continue;
            measureChildWithMargins(menuView, menuWidthSpec, 0, menuHeightSpec, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInLayout = true;
        //确定children
        ensureChildren();
        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        int pr = getPaddingRight();
        int pb = getPaddingBottom();
        LayoutParams lp;
        //layout main
        lp = (LayoutParams) mMainItemView.getLayoutParams();
        mMainItemView.layout(
                pl + lp.leftMargin,
                pt + lp.topMargin,
                getWidth() - pr - lp.rightMargin,
                getHeight() - pb - lp.bottomMargin);

        //layout menu
        int totalLength = 0;
        int menuLeft = mMainItemView.getRight() + lp.rightMargin;
        for (int i = 0; i < getChildCount(); i++) {
            View menuView = getChildAt(i);
            lp = (LayoutParams) menuView.getLayoutParams();

            if (lp.itemType == LayoutParams.TYPE_MAIN)
                continue;

            int tempLeft = menuLeft + lp.leftMargin;
            int tempTop = pt + lp.topMargin;
            menuView.layout(
                    tempLeft,
                    tempTop,
                    tempLeft + menuView.getMeasuredWidth() + lp.rightMargin,
                    tempTop + menuView.getMeasuredHeight() + lp.bottomMargin);

            menuLeft = menuView.getRight() + lp.rightMargin;
            totalLength += lp.leftMargin + lp.rightMargin + menuView.getMeasuredWidth();
        }

        mMaxScrollOffset = totalLength;
        mScrollOffset = mScrollOffset < -mMaxScrollOffset / 2 ? -mMaxScrollOffset : 0;

        offsetChildrenLeftAndRight(mScrollOffset);

        mInLayout = false;
    }

    void offsetChildrenLeftAndRight(int delta) {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            ViewCompat.offsetLeftAndRight(childView, delta);
        }
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mScrollRunnable);
        mTouchMode = Mode.RESET;
        mScrollOffset = 0;
    }

    //展开的情况下，拦截down event，避免触发点击main事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                View pointView = ViewHelper.findTopChildUnder(this, x, y);
                if (pointView != null && pointView == mMainItemView && mScrollOffset != 0)
                    return true;
                break;
            }

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                View pointView = ViewHelper.findTopChildUnder(this, x, y);
                if (pointView != null && pointView == mMainItemView && mTouchMode == Mode.CLICK && mScrollOffset != 0)
                    return true;
            }
        }

        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                View pointView = ViewHelper.findTopChildUnder(this, x, y);
                if (pointView != null && pointView == mMainItemView && mScrollOffset != 0)
                    return true;
                break;
            }

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                View pointView = ViewHelper.findTopChildUnder(this, x, y);
                if (pointView != null && pointView == mMainItemView && mTouchMode == Mode.CLICK && mScrollOffset != 0) {
                    close();
                    return true;
                }
            }
        }

        return false;
    }

    void setTouchMode(Mode mode) {
        if (mode == mTouchMode)
            return;
        if (mTouchMode == Mode.FLING)
            removeCallbacks(mScrollRunnable);
        mTouchMode = mode;
    }

    public Mode getTouchMode() {
        return mTouchMode;
    }

    boolean trackMotionScroll(int deltaX) {
        if (deltaX == 0)
            return true;

        boolean over = false;
        int newLeft = mScrollOffset + deltaX;
        if ((deltaX > 0 && newLeft > 0) || (deltaX < 0 && newLeft < -mMaxScrollOffset)) {
            over = true;
            newLeft = Math.min(newLeft, 0);
            newLeft = Math.max(newLeft, -mMaxScrollOffset);
        }

        offsetChildrenLeftAndRight(newLeft - mScrollOffset);
        mScrollOffset = newLeft;
        return over;
    }

    private final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    private class ScrollRunnable implements Runnable {
        private Scroller scroller;
        private boolean abort;
        private int minVelocity;

        ScrollRunnable(Context context) {
            scroller = new Scroller(context, sInterpolator);
            abort = false;

            ViewConfiguration configuration = ViewConfiguration.get(context);
            minVelocity = configuration.getScaledMinimumFlingVelocity();
        }

        void startScroll(int startX, int endX) {
            if (startX != endX) {
                setTouchMode(Mode.FLING);
                abort = false;
                scroller.startScroll(startX, 0, endX - startX, 0, 500);
                ViewCompat.postOnAnimation(SwipeLayout.this, this);
            }
        }

        void startFling(int startX, int xVel) {
            if (xVel > minVelocity && startX != 0) {
                startScroll(startX, 0);
                return;
            }
            if (xVel < -minVelocity && startX != -mMaxScrollOffset) {
                startScroll(startX, -mMaxScrollOffset);
                return;
            }
            startScroll(startX, startX > -mMaxScrollOffset / 2 ? 0 : -mMaxScrollOffset);
        }

        void abort() {
            if (!abort) {
                abort = true;
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                    removeCallbacks(this);
                }
            }
        }

        @Override
        public void run() {
            if (!abort) {
                boolean more = scroller.computeScrollOffset();
                int curX = scroller.getCurrX();
                boolean atEdge = false;
                if (curX != mScrollOffset)
                    atEdge = trackMotionScroll(curX - mScrollOffset);
                if (more && !atEdge) {
                    ViewCompat.postOnAnimation(SwipeLayout.this, this);
                } else {
                    removeCallbacks(this);
                    if (!scroller.isFinished())
                        scroller.abortAnimation();
                    setTouchMode(Mode.RESET);
                }
            }
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams ? (LayoutParams) p : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int itemType = -1;
        public static final int TYPE_MAIN = 0x01;
        public static final int TYPE_MENU = 0x02;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SwipeLayout_Layout);
            itemType = a.getInt(R.styleable.SwipeLayout_Layout_swipe_type, -1);
            a.recycle();
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            itemType = source.itemType;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
