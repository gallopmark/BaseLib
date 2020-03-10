package pony.xcode.common;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;

public class ActivityThemeProcessor {

    private Activity mActivity;
    /*activity动画适配*/
    private int mActivityCloseEnterAnimation = -1;
    private int mActivityCloseExitAnimation = -1;

    private ActivityThemeProcessor(Activity activity) {
        Resources.Theme theme = activity.getTheme();
        if (theme != null) {
            TypedArray activityStyle = theme.obtainStyledAttributes(new int[]{android.R.attr.windowAnimationStyle});
            int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
            activityStyle.recycle();
            activityStyle = theme.obtainStyledAttributes(windowAnimationStyleResId, new int[]{
                    android.R.attr.activityOpenEnterAnimation,
                    android.R.attr.activityOpenExitAnimation,
                    android.R.attr.activityCloseEnterAnimation,
                    android.R.attr.activityCloseExitAnimation});
            int enterAnim = activityStyle.getResourceId(0, 0);
            int exitAnim = activityStyle.getResourceId(1, 0);
            activity.overridePendingTransition(enterAnim, exitAnim);
            mActivityCloseEnterAnimation = activityStyle.getResourceId(2, 0);
            mActivityCloseExitAnimation = activityStyle.getResourceId(3, 0);
            activityStyle.recycle();
        }
        this.mActivity = activity;
    }

    public static ActivityThemeProcessor display(Activity activity) {
        return new ActivityThemeProcessor(activity);
    }

    public void exit() {
        if (mActivityCloseEnterAnimation != -1 || mActivityCloseExitAnimation != -1) {
            mActivity.overridePendingTransition(mActivityCloseEnterAnimation, mActivityCloseExitAnimation);
        }
    }
}
