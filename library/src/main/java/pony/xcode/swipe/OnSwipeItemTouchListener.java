package pony.xcode.swipe;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*recyclerView.addOnItemTouchListener*/
public class OnSwipeItemTouchListener implements RecyclerView.OnItemTouchListener {
    private SwipeLayout mCaptureItem;
    private float mLastMotionX;
    private float mLastMotionY;
    private VelocityTracker mVelocityTracker;

    private int mActivePointerId;

    private int mTouchSlop;
    private int mMaximumVelocity;

    private boolean mParentHandled;
    private boolean mProbingParentProcess;

    private boolean mIgnoreActions = false;

    public OnSwipeItemTouchListener(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mActivePointerId = -1;
        mParentHandled = false;
        mProbingParentProcess = false;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent ev) {
        if (mProbingParentProcess)
            return false;

        boolean intercept = false;
        final int action = ev.getActionMasked();
        if (action != MotionEvent.ACTION_DOWN && mIgnoreActions)
            return true;
        if (action != MotionEvent.ACTION_DOWN && (mCaptureItem == null || mParentHandled))
            return false;
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIgnoreActions = false;
                mParentHandled = false;
                mActivePointerId = ev.getPointerId(0);
                final float x = ev.getX();
                final float y = ev.getY();
                mLastMotionX = x;
                mLastMotionY = y;
                boolean pointOther = false;
                SwipeLayout pointItem = null;
                //首先知道ev针对的是哪个item
                View pointView = ViewHelper.findTopChildUnder(rv, (int) x, (int) y);
                if (!(pointView instanceof SwipeLayout)) {
                    //可能是head view或bottom view
                    pointOther = true;
                } else
                    pointItem = (SwipeLayout) pointView;
                //此时的pointOther=true，意味着点击的view为空或者点击的不是item
                //还没有把点击的是item但是不是capture item给过滤出来
                if (!pointOther && (mCaptureItem == null || mCaptureItem != pointItem))
                    pointOther = true;

                //点击的是capture item
                if (!pointOther) {
                    SwipeLayout.Mode mode = mCaptureItem.getTouchMode();
                    //如果它在fling，就转为drag
                    //需要拦截，并且requestDisallowInterceptTouchEvent
                    boolean disallowIntercept = false;
                    if (mode == SwipeLayout.Mode.FLING) {
                        mCaptureItem.setTouchMode(SwipeLayout.Mode.DRAG);
                        disallowIntercept = true;
                        intercept = true;
                    } else {//如果是expand的，就不允许parent拦截
                        mCaptureItem.setTouchMode(SwipeLayout.Mode.CLICK);
                        if (mCaptureItem.getScrollOffset() != 0)
                            disallowIntercept = true;
                    }

                    if (disallowIntercept) {
                        final ViewParent parent = rv.getParent();
                        if (parent != null)
                            parent.requestDisallowInterceptTouchEvent(true);
                    }
                } else {//capture item为null或者与point item不一样
                    //直接将其close掉
                    if (mCaptureItem != null &&
                            mCaptureItem.getScrollOffset() != 0) {
                        mCaptureItem.close();
                        mIgnoreActions = true;
                        return true;
                    }
                    mCaptureItem = null;
                    if (pointItem != null) {
                        mCaptureItem = pointItem;
                        mCaptureItem.setTouchMode(SwipeLayout.Mode.CLICK);
                    }
                }

                //如果parent处于fling状态，此时，parent就会转为drag。应该将后续move都交给parent处理
                mProbingParentProcess = true;
                mParentHandled = rv.onInterceptTouchEvent(ev);
                mProbingParentProcess = false;
                if (mParentHandled) {
                    intercept = false;
                    //在down时，就被认定为parent的drag，所以，直接交给parent处理即可
                    if (mCaptureItem != null && mCaptureItem.getScrollOffset() != 0)
                        mCaptureItem.close();
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int actionIndex = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(actionIndex);
                mLastMotionX = ev.getX(actionIndex);
                mLastMotionY = ev.getY(actionIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int actionIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(actionIndex);
                if (pointerId == mActivePointerId) {
                    final int newIndex = actionIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newIndex);
                    mLastMotionX = ev.getX(newIndex);
                    mLastMotionY = ev.getY(newIndex);
                }
                break;
            }

            //down时，已经将capture item定下来了。所以，后面可以安心考虑event处理
            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1)
                    break;

                final int x = (int) (ev.getX(activePointerIndex) + .5f);
                final int y = (int) ((int) ev.getY(activePointerIndex) + .5f);

                int deltaX = (int) (x - mLastMotionX);
                int deltaY = (int) (y - mLastMotionY);
                final int xDiff = Math.abs(deltaX);
                final int yDiff = Math.abs(deltaY);

                SwipeLayout.Mode mode = mCaptureItem.getTouchMode();

                if (mode == SwipeLayout.Mode.CLICK) {
                    //如果capture item是open的，下拉有两种处理方式：
                    //  1、下拉后，直接close item
                    //  2、只要是open的，就拦截所有它的消息，这样如果点击open的，就只能滑动该capture item
                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        mCaptureItem.setTouchMode(SwipeLayout.Mode.DRAG);
                        final ViewParent parent = rv.getParent();
                        parent.requestDisallowInterceptTouchEvent(true);

                        deltaX = deltaX > 0 ? deltaX - mTouchSlop : deltaX + mTouchSlop;
                    } else/* if(yDiff>mTouchSlop)*/ {
                        mProbingParentProcess = true;
                        mParentHandled = rv.onInterceptTouchEvent(ev);
                        mProbingParentProcess = false;

                        if (mParentHandled && mCaptureItem.getScrollOffset() != 0)
                            mCaptureItem.close();
                    }
                }
                mode = mCaptureItem.getTouchMode();
                if (mode == SwipeLayout.Mode.DRAG) {
                    intercept = true;
                    mLastMotionX = x;
                    mLastMotionY = y;

                    //对capture item进行拖拽
                    mCaptureItem.trackMotionScroll(deltaX);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
                SwipeLayout.Mode mode = mCaptureItem.getTouchMode();
                if (mode == SwipeLayout.Mode.DRAG) {
                    final VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int xVel = (int) velocityTracker.getXVelocity(mActivePointerId);
                    mCaptureItem.fling(xVel);
                    intercept = true;
                }
                cancel();
                break;
            case MotionEvent.ACTION_CANCEL:
                mCaptureItem.revise();
                cancel();
                break;
        }

        return intercept;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent ev) {
        if (mIgnoreActions)
            return;

        final int action = ev.getActionMasked();
        final int actionIndex = ev.getActionIndex();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                mActivePointerId = ev.getPointerId(actionIndex);

                mLastMotionX = ev.getX(actionIndex);
                mLastMotionY = ev.getY(actionIndex);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                final int pointerId = ev.getPointerId(actionIndex);
                if (pointerId == mActivePointerId) {
                    final int newIndex = actionIndex == 0 ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newIndex);

                    mLastMotionX = ev.getX(newIndex);
                    mLastMotionY = ev.getY(newIndex);
                }
                break;
            //down时，已经将capture item定下来了。所以，后面可以安心考虑event处理
            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1)
                    break;

                final float x = ev.getX(activePointerIndex);
                final float y = (int) ev.getY(activePointerIndex);

                int deltaX = (int) (x - mLastMotionX);

                if (mCaptureItem != null && mCaptureItem.getTouchMode() == SwipeLayout.Mode.DRAG) {
                    mLastMotionX = x;
                    mLastMotionY = y;

                    //对capture item进行拖拽
                    mCaptureItem.trackMotionScroll(deltaX);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                if (mCaptureItem != null) {
                    SwipeLayout.Mode mode = mCaptureItem.getTouchMode();
                    if (mode == SwipeLayout.Mode.DRAG) {
                        final VelocityTracker velocityTracker = this.mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                        int xVel = (int) velocityTracker.getXVelocity(mActivePointerId);
                        mCaptureItem.fling(xVel);
                    }
                }
                cancel();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mCaptureItem != null)
                    mCaptureItem.revise();
                cancel();
                break;

        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private void cancel() {
        mParentHandled = false;
        mActivePointerId = -1;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
